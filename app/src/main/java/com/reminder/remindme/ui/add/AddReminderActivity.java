package com.reminder.remindme.ui.add;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.reminder.remindme.R;
import com.reminder.remindme.data.model.ReminderEntity;
import com.reminder.remindme.data.source.DBHelper;
import com.reminder.remindme.notification.AlarmHelper;
import com.reminder.remindme.util.Constant;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class AddReminderActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;
    @Inject
    DBHelper database;

    private Toolbar mToolbar;
    private EditText mTitleText;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText;
    private FloatingActionButton mFAB1;
    private FloatingActionButton mFAB2;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;

    private View dateSelector, timeSelector, repeatSwitchSelector, repeatNoSelector, repeatTypeSelector;

    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private boolean mActive;

    private ReminderEntity reminder;

    private SimpleDateFormat sdf = new SimpleDateFormat(Constant.DateConstant.DATE_TIME_FORMAT_SLASH, Locale.getDefault());

    private AlarmHelper alarmHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        alarmHelper = new AlarmHelper(this);

        // Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);

        mFAB1.setOnClickListener(this::selectFab1);
        mFAB2.setOnClickListener(this::selectFab2);


        dateSelector = (View) findViewById(R.id.date);
        timeSelector = (View) findViewById(R.id.time);
        repeatNoSelector = (View) findViewById(R.id.RepeatNo);
        repeatTypeSelector = (View) findViewById(R.id.RepeatType);
        repeatSwitchSelector = (Switch) findViewById(R.id.repeat_switch);

        dateSelector.setOnClickListener(this::setDate);
        timeSelector.setOnClickListener(this::setTime);
        repeatSwitchSelector.setOnClickListener(this::onSwitchRepeat);
        repeatNoSelector.setOnClickListener(this::setRepeatNo);
        repeatTypeSelector.setOnClickListener(this::selectRepeatType);

        setSupportActionBar(mToolbar);


        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(R.string.title_activity_add_reminder);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // Initialize default values
        mActive = true;
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;

        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Setup TextViews using reminder values
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

        // arguments
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            getSupportActionBar().setTitle(R.string.title_activity_edit_reminder);
            reminder = ReminderEntity.fromBundle(bundle);
            setReminderInfo(reminder);
        }

        // To save state on device rotation
        if (savedInstanceState != null) {
            populateReminderInfo(savedInstanceState);
        }

        // Setup up active buttons
        if (mActive) {
            mFAB1.setVisibility(View.GONE);
            mFAB2.setVisibility(View.VISIBLE);
        } else {
            mFAB1.setVisibility(View.VISIBLE);
            mFAB2.setVisibility(View.GONE);
        }
    }

    private void setReminderInfo(ReminderEntity reminder) {
        String savedTitle = reminder.getTitle();
        mTitleText.setText(savedTitle);
        mTitle = savedTitle;

        SimpleDateFormat timeFormatter = new SimpleDateFormat(Constant.DateConstant.TIME_FORMAT, Locale.getDefault());
        SimpleDateFormat dateFormatter = new SimpleDateFormat(Constant.DateConstant.DATE_FORMAT, Locale.getDefault());

        Date reminderDate = new Date(reminder.getRemindTime());

        String savedTime = timeFormatter.format(reminderDate);
        mTimeText.setText(savedTime);
        mTime = savedTime;

        String savedDate = dateFormatter.format(reminderDate);
        mDateText.setText(savedDate);
        mDate = savedDate;

        String saveRepeat = reminder.getRepeat();
        mRepeatText.setText(saveRepeat);
        mRepeat = saveRepeat;

        String savedRepeatNo = reminder.getRepeatNo();
        mRepeatNoText.setText(savedRepeatNo);
        mRepeatNo = savedRepeatNo;

        String savedRepeatType = reminder.getRepeatType();
        mRepeatTypeText.setText(savedRepeatType);
        mRepeatType = savedRepeatType;

        mActive = reminder.isActive();
    }

    private void populateReminderInfo(Bundle bundle) {
        String savedTitle = bundle.getString(KEY_TITLE);
        mTitleText.setText(savedTitle);
        mTitle = savedTitle;

        String savedTime = bundle.getString(KEY_TIME);
        mTimeText.setText(savedTime);
        mTime = savedTime;

        String savedDate = bundle.getString(KEY_DATE);
        mDateText.setText(savedDate);
        mDate = savedDate;

        String saveRepeat = bundle.getString(KEY_REPEAT);
        mRepeatText.setText(saveRepeat);
        mRepeat = saveRepeat;

        String savedRepeatNo = bundle.getString(KEY_REPEAT_NO);
        mRepeatNoText.setText(savedRepeatNo);
        mRepeatNo = savedRepeatNo;

        String savedRepeatType = bundle.getString(KEY_REPEAT_TYPE);
        mRepeatTypeText.setText(savedRepeatType);
        mRepeatType = savedRepeatType;

        mActive = bundle.getBoolean(KEY_ACTIVE);
    }

    // To save state on device rotation
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putBoolean(KEY_ACTIVE, mActive);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            // On clicking the back arrow
            // Discard any changes
            case android.R.id.home:
                finish();
                return true;

            // On clicking save reminder button
            // Update reminder
            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if (mTitleText.getText().toString().length() == 0) {
                    mTitleText.setError("Reminder Title cannot be blank!");
                } else {
                    saveReminder();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.discard_reminder:
                Toast.makeText(this, "Discarded",
                        Toast.LENGTH_SHORT).show();

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // On clicking Time picker
    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = new TimePickerDialog(
                this,
                (view1, hourOfDay, minute) -> {
                    if (hourOfDay < now.get(Calendar.HOUR_OF_DAY) ||
                            (hourOfDay < now.get(Calendar.HOUR_OF_DAY) &&
                                    minute < now.get(Calendar.MINUTE))) {
                        Toast.makeText(this, "Please select future time!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mHour = hourOfDay;
                    mMinute = minute;
                    if (minute < 10) {
                        mTime = hourOfDay + ":" + "0" + minute;
                    } else {
                        mTime = hourOfDay + ":" + minute;
                    }
                    mTimeText.setText(mTime);
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show();
    }

    // On clicking Date picker
    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(
                this,
                (d, year, monthOfYear, dayOfMonth) -> {
                    monthOfYear++;
                    mDay = dayOfMonth;
                    mMonth = monthOfYear;
                    mYear = year;
                    mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                    mDateText.setText(mDate);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show();
    }

    // On clicking the active button
    public void selectFab1(View v) {
        mFAB1.setVisibility(View.GONE);

        mFAB2.setVisibility(View.VISIBLE);
        mActive = true;
    }

    // On clicking the inactive button
    public void selectFab2(View v) {
        mFAB2.setVisibility(View.GONE);
        mFAB1.setVisibility(View.VISIBLE);
        mActive = false;
    }

    // On clicking the repeat switch
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);
        }
    }

    // On clicking repeat type button
    public void selectRepeatType(View v) {
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, (dialog, item) -> {

            mRepeatType = items[item];
            mRepeatTypeText.setText(mRepeatType);
            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // On clicking repeat interval button
    public void setRepeatNo(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        FrameLayout container = new FrameLayout(this);
        container.setPadding(15, 10, 15, 10);
        container.addView(input);

        alert.setView(container);
        alert.setPositiveButton("Ok",
                (dialog, whichButton) -> {

                    if (input.getText().toString().length() == 0) {
                        mRepeatNo = Integer.toString(1);
                    } else {
                        mRepeatNo = input.getText().toString().trim();
                    }
                    mRepeatNoText.setText(mRepeatNo);
                    mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                });
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // do nothing
        });
        alert.show();
    }

    private Date parseDate(String date) {
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return Calendar.getInstance().getTime();
        }
    }

    // On clicking the save button
    private void saveReminder() {
        ReminderEntity updatedReminder = createReminderObject();
        SecureRandom randomNumberGenerator = new SecureRandom();
        if (this.reminder != null) {
            updatedReminder.setNotificationId(TextUtils.isEmpty(reminder.getNotificationId()) ? String.valueOf(randomNumberGenerator.nextInt()) : reminder.getNotificationId());
            updatedReminder.setDocumentId(reminder.getDocumentId());

            // cancel previous notification
            alarmHelper.cancelAlarm(reminder);

            database.updateReminder(updatedReminder, DBHelper.REMINDERS, new DBHelper.DBOperationListener<String>() {
                @Override
                public void onComplete(String value) {
                    Toast.makeText(getApplicationContext(), "Reminder successfully updated!!", Toast.LENGTH_SHORT).show();

                    if (updatedReminder.isActive()) {
                        alarmHelper.setAlarm(updatedReminder);
                    }

                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to update a reminder!!", Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            updatedReminder.setNotificationId(String.valueOf(randomNumberGenerator.nextInt()));
            database.saveReminder(updatedReminder, DBHelper.REMINDERS, new DBHelper.DBOperationListener<ReminderEntity>() {
                @Override
                public void onComplete(ReminderEntity value) {
                    Toast.makeText(getApplicationContext(), "Reminder successfully created!!", Toast.LENGTH_SHORT).show();

                    if (value.isActive()) {
                        alarmHelper.setAlarm(updatedReminder);
                    }

                    finish();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to create a reminder!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private ReminderEntity createReminderObject() {
        ReminderEntity reminder = new ReminderEntity();
        reminder.setTitle(mTitle);
        reminder.setRemindTime(parseDate(mDate + " " + mTime).getTime());
        reminder.setRepeat(mRepeat);
        reminder.setRepeatNo(mRepeatNo);
        reminder.setRepeatType(mRepeatType);
        reminder.setActive(mActive);
        return reminder;
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }
}
