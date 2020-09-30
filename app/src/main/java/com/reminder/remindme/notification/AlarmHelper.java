package com.reminder.remindme.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.reminder.remindme.R;
import com.reminder.remindme.data.model.ReminderEntity;
import com.reminder.remindme.ui.MainActivity;

import java.util.Calendar;

public class AlarmHelper {
    final static String LOG_TAG = "NotificationHelper";
    final MediaPlayer mediaPlayer;
    private final Context context;

    public AlarmHelper(Context context) {
        this.context = context;
        mediaPlayer = MediaPlayer.create(context, R.raw.analog_watch);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);

    }

    public void setAlarm(ReminderEntity reminder) {

        long interval = reminder.repeatTime();

        if (interval > 0) {
            setRepeatAlarm(reminder, interval);
            return;
        }
        Log.d(LOG_TAG, String.format("setAlarm() with id %s at time %s", reminder.getNotificationId(), reminder.getRemindTime()));

        PendingIntent mPendingIntent = toScheduleNotificationIntent(reminder);
        if (mPendingIntent == null) return;

        // Calculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = reminder.getRemindTime() - currentTime;

        // Start alarm using notification time
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);

        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, NotificationBootEventReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void setRepeatAlarm(ReminderEntity reminder, long repeatTime) {
        Log.d(LOG_TAG, String.format("setRepeatAlarm() with id %s at time %s with interval of %d", reminder.getNotificationId(), reminder.getRemindTime(), repeatTime));

        PendingIntent mPendingIntent = toScheduleNotificationIntent(reminder);
        if (mPendingIntent == null) return;

        // Calculate notification time in
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = reminder.getRemindTime() - currentTime;

        // Start alarm using initial notification time and repeat interval time
        getAlarmManager().setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                repeatTime, mPendingIntent);

        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, NotificationBootEventReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(ReminderEntity reminder) {
        Log.d(LOG_TAG, String.format("cancelAlarm() with id %s", reminder.getNotificationId()));

        // Cancel Alarm using Reminder ID
        PendingIntent mPendingIntent = toScheduleNotificationIntent(reminder);
        if (mPendingIntent == null) return;

        getAlarmManager().cancel(mPendingIntent);

        // Disable alarm
        ComponentName receiver = new ComponentName(context, NotificationBootEventReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void createNotificationAndNotify(Bundle bundle) {
        ReminderEntity reminder = ReminderEntity.fromBundle(bundle);
        int notificationID = Integer.parseInt(reminder.getNotificationId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                notificationID,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = NotificationUtils.createNotification(context, reminder, pendingIntent);
        NotificationManagerCompat.from(context).notify(notificationID, notification);
    }

    public void scheduledNotificationDelicate(ReminderEntity reminder) {

        // If the fireDate is in past, this will fire immediately and show the
        // notification to the user
        PendingIntent mPendingIntent = toScheduleNotificationIntent(reminder);
        if (mPendingIntent == null) return;

        long interval = getInterval(reminder.getReminderType());

        Log.d(LOG_TAG, String.format("Setting a notification with id %s at time %s with interval of %d", reminder.getNotificationId(), reminder.getRemindTime(), interval));
        if (interval > 0) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, reminder.getRemindTime(), interval, mPendingIntent);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.getRemindTime(), mPendingIntent);
            } else {
                getAlarmManager().setExact(AlarmManager.RTC_WAKEUP, reminder.getRemindTime(), mPendingIntent);
            }
        } else {
            getAlarmManager().set(AlarmManager.RTC_WAKEUP, reminder.getRemindTime(), mPendingIntent);
        }
    }

    private PendingIntent toScheduleNotificationIntent(ReminderEntity reminderEntity) {
        try {
            int notificationID = Integer.parseInt(reminderEntity.getNotificationId());

            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationID);
            notificationIntent.putExtras(reminderEntity.toBundle());

            return PendingIntent.getBroadcast(context, notificationID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Unable to parse Notification ID", e);
        }

        return null;
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private long getInterval(String alarmType) {
        Log.d(LOG_TAG, " AlarmType" + alarmType);
        switch (alarmType) {
            case "Hourly":
                return AlarmManager.INTERVAL_HOUR;
            case "Every 10 minutes":
                return 600000;
            case "Every 5 minutes":
                return 60000;
            case "Daily":
                return AlarmManager.INTERVAL_DAY;
            case "Weekly":
                return AlarmManager.INTERVAL_DAY * 7;
            case "Monthly":
                return AlarmManager.INTERVAL_DAY * 30;
            default:
                return 0;
        }
    }
}
