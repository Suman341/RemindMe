package com.reminder.remindme.ui.reminders;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.reminder.remindme.R;
import com.reminder.remindme.data.model.ReminderEntity;
import com.reminder.remindme.di.injector.Injectable;
import com.reminder.remindme.notification.AlarmHelper;
import com.reminder.remindme.ui.add.AddReminderActivity;
import com.reminder.remindme.viewmodel.TaskViewModel;

import javax.inject.Inject;


public class ReminderFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView remindersRecyclerView;
    private TextView emptyView;
    private FloatingActionButton addReminderBtn;

    private TaskViewModel taskViewModel;
    private SimpleAdapter sectionAdapter;
    private AlarmHelper alarmHelper;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        // we want to cache these and not allocate anything repeatedly in the onChildDraw method
        Drawable background;
        Drawable xMark;
        int xMarkMargin;
        boolean initiated;

        private void init() {
            background = new ColorDrawable(Color.RED);
            xMark = ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear_24dp);
            xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            xMarkMargin = (int) requireContext().getResources().getDimension(R.dimen.ic_clear_margin);
            initiated = true;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            View itemView = viewHolder.itemView;

            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1) {
                // not interested in those
                return;
            }

            if (!initiated) {
                init();
            }

            // draw red background
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            // draw x mark
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int xMarkBottom = xMarkTop + intrinsicHeight;
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

            xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            ReminderEntity reminder = sectionAdapter.getItem(position);

            alarmHelper.cancelAlarm(reminder);
            taskViewModel.moveToHistory(reminder);

            sectionAdapter.removeItemSelected(position);
            sectionAdapter.notifyItemRemoved(position);

        }
    };
    private MultiSelector mMultiSelector = new MultiSelector();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
        alarmHelper = new AlarmHelper(requireContext());
        taskViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskViewModel.class);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        remindersRecyclerView = view.findViewById(R.id.reminderRecyclerView);
        emptyView = view.findViewById(R.id.no_reminder_text);
        addReminderBtn = view.findViewById(R.id.addReminderFab);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        remindersRecyclerView.setLayoutManager(linearLayoutManager);

        // swipe action
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(remindersRecyclerView);

        // reminder adapters
        sectionAdapter = new SimpleAdapter(
                mMultiSelector,
                (reminderEntity, position) -> {
                    Intent intent = new Intent(getContext(), AddReminderActivity.class);
                    intent.putExtras(reminderEntity.toBundle());
                    startActivity(intent);
                },
                v -> false
        );
        remindersRecyclerView.setAdapter(sectionAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            taskViewModel.refreshReminders();
        });

        taskViewModel.getReminders().observe(this, response -> {
            if (response == null) return;

            swipeRefreshLayout.setRefreshing(response.isLoading());

            if (response.isSuccessful()) {
                remindersRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                sectionAdapter.setItems(response.getData());
                sectionAdapter.notifyDataSetChanged();
            } else if (response.hasError()) {
                remindersRecyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(R.string.no_reminder_message);
            }
        });

        addReminderBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddReminderActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (taskViewModel != null) {
            taskViewModel.refreshReminders();
        }
    }
}
