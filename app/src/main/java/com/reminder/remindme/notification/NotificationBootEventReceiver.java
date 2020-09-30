package com.reminder.remindme.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.reminder.remindme.data.model.ReminderEntity;
import com.reminder.remindme.data.source.DBHelper;
import com.reminder.remindme.util.WakeLocker;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public final class NotificationBootEventReceiver extends BroadcastReceiver {
    final static String LOG_TAG = "NoticeEventReceiver";

    @Inject
    DBHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);


        Log.i(LOG_TAG, "NotificationServiceStarterReceiver loading scheduled notifications");

        AlarmHelper alarmHelper = new AlarmHelper(context);
        List<ReminderEntity> reminders = dbHelper.getReminders(DBHelper.REMINDERS);
        for (ReminderEntity reminder : reminders) {
            if (!reminder.isActive()) continue;

            if (reminder.getRemindTime() < System.currentTimeMillis()) {
                Log.i(LOG_TAG, "NotificationServiceStarterReceiver: Showing notification for " + reminder.getDocumentId());
                // wake the device
                WakeLocker.acquire(context);

                alarmHelper.createNotificationAndNotify(reminder.toBundle());

                // remove notification after its shown
                if (reminder.getReminderType().equalsIgnoreCase("once")) {
                    dbHelper.deleteReminder(reminder.getDocumentId(), null);
                    dbHelper.saveReminder(reminder, DBHelper.REMINDER_HISTORIES, null);
                }
            } else {
                Log.i(LOG_TAG, "NotificationServiceStarterReceiver: Scheduling notification for " + reminder.getDocumentId());
                alarmHelper.setAlarm(reminder);
            }
        }

    }
}