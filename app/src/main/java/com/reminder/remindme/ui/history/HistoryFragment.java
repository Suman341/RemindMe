package com.reminder.remindme.ui.history;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.reminder.remindme.R;
import com.reminder.remindme.di.injector.Injectable;
import com.reminder.remindme.ui.reminders.SimpleAdapter;
import com.reminder.remindme.viewmodel.TaskViewModel;

import javax.inject.Inject;

public class HistoryFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private TextView emptyView;
    private RecyclerView remindersRecyclerView;

    private TaskViewModel taskViewModel;

    private MultiSelector mMultiSelector = new MultiSelector();
    private SimpleAdapter sectionAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.history);
        }

        taskViewModel = ViewModelProviders.of(this, viewModelFactory).get(TaskViewModel.class);

        emptyView = view.findViewById(R.id.no_reminder_text);
        remindersRecyclerView = view.findViewById(R.id.reminderRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        remindersRecyclerView.setLayoutManager(linearLayoutManager);


        // reminder adapters
        sectionAdapter = new SimpleAdapter();
        remindersRecyclerView.setAdapter(sectionAdapter);

        taskViewModel.getReminderHistory().observe(this, response -> {
            if (response == null) return;

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
        taskViewModel.refreshReminderHistory();

        return view;
    }
}
