package com.reminder.remindme.ui.reminders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.reminder.remindme.R;
import com.reminder.remindme.data.model.ReminderEntity;
import com.reminder.remindme.util.Constant;
import com.reminder.remindme.util.DateTimeSorter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
    private ArrayList<ReminderEntity> mItems;

    private SimpleDateFormat sdf = new SimpleDateFormat(Constant.DateConstant.DATE_TIME_FORMAT_SLASH, Locale.getDefault());

    private MultiSelector multiSelector;
    private OnReminderOnClickListener onReminderClickListener;
    private View.OnLongClickListener onReminderLongClickListener;

    public SimpleAdapter(MultiSelector multiSelector, OnReminderOnClickListener onReminderClickListener, View.OnLongClickListener onReminderLongClickListener) {
        mItems = new ArrayList<>();
        this.multiSelector = multiSelector;
        this.onReminderClickListener = onReminderClickListener;
        this.onReminderLongClickListener = onReminderLongClickListener;
    }

    public SimpleAdapter() {
        mItems = new ArrayList<>();
    }

    public void setItems(List<ReminderEntity> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    public void removeItemSelected(int selected) {
        if (mItems.isEmpty()) return;
        mItems.remove(selected);
        notifyItemRemoved(selected);
    }

    // View holder for recycler view items
    @Override
    public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View root = inflater.inflate(R.layout.reminder_item, container, false);

        return new VerticalItemHolder(root);
    }

    @Override
    public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
        ReminderEntity item = mItems.get(position);
        itemHolder.setReminderTitle(item.getTitle());
        itemHolder.setReminderDateTime(sdf.format(new Date(item.getRemindTime())));
        itemHolder.setReminderRepeatInfo(item.getRepeat(), item.getRepeatNo(), item.getRepeatType());
        itemHolder.setActiveImage(item.isActive());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public ReminderEntity getItem(int position) {
        return mItems.get(position);
    }

    interface OnReminderOnClickListener {
        void onClick(ReminderEntity reminderEntity, int position);
    }

    // Class to compare date and time so that items are sorted in ascending order
    public class DateTimeComparator implements Comparator {
        public int compare(Object a, Object b) {
            String o1 = ((DateTimeSorter) a).getDateTime();
            String o2 = ((DateTimeSorter) b).getDateTime();

            try {
                return sdf.parse(o1).compareTo(sdf.parse(o2));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    // UI and data class for recycler view items
    public class VerticalItemHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
        private ImageView mActiveImage, mThumbnailImage;
        private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
        private TextDrawable mDrawableBuilder;

        public VerticalItemHolder(View itemView) {
            // super(itemView, multiSelector);
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setLongClickable(true);

            // Initialize views
            mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
            mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
            mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
            mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
            mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
        }

        // On clicking a reminder item
        @Override
        public void onClick(View v) {
            if (onReminderClickListener != null) {
                int position = getAdapterPosition();
                onReminderClickListener.onClick(mItems.get(position), position);
            }
        }

        // On long press enter action mode with context menu
        @Override
        public boolean onLongClick(View v) {
            if (onReminderLongClickListener != null) {
                onReminderLongClickListener.onLongClick(v);
            }
            return true;
        }

        // Set reminder title view
        public void setReminderTitle(String title) {
            mTitleText.setText(title);
            String letter = "A";

            if (title != null && !title.isEmpty()) {
                letter = title.substring(0, 1);
            }

            int color = mColorGenerator.getRandomColor();

            // Create a circular icon consisting of  a random background colour and first letter of title
            mDrawableBuilder = TextDrawable.builder()
                    .buildRound(letter, color);
            mThumbnailImage.setImageDrawable(mDrawableBuilder);
        }

        // Set date and time views
        public void setReminderDateTime(String datetime) {
            mDateAndTimeText.setText(datetime);
        }

        // Set repeat views
        public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
            if ("true".equals(repeat)) {
                mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
            } else if ("false".equals(repeat)) {
                mRepeatInfoText.setText("Repeat Off");
            }
        }

        // Set active image as on or off
        public void setActiveImage(Boolean active) {
            if (active) {
                mActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
            } else {
                mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
            }
        }
    }
}