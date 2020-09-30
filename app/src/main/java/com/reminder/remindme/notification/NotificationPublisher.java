package com.reminder.remindme.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.reminder.remindme.data.model.ReminderEntity;
import com.reminder.remindme.data.source.DBHelper;

import java.security.SecureRandom;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class NotificationPublisher extends BroadcastReceiver {

    final static String LOG_TAG = "NoticeEventReceiver";
    final static String NOTIFICATION_ID = "notificationId";

    @Inject
    DBHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);

        final Bundle bundle = intent.getExtras();

        Log.v(LOG_TAG, "onMessageReceived: " + bundle);

        if (bundle == null) return;

        long currentTime = System.currentTimeMillis();

        Log.i(LOG_TAG, "NotificationPublisher: Prepare To Publish: " + bundle.getString(NOTIFICATION_ID) + ", Now Time: " + currentTime);


        handleLocalNotification(context, bundle);

        ReminderEntity reminder = ReminderEntity.fromBundle(bundle);
        if (reminder.getReminderType().equalsIgnoreCase("once")) {
            dbHelper.deleteReminder(reminder.getDocumentId(), null);
            dbHelper.saveReminder(reminder, DBHelper.REMINDER_HISTORIES, null);
        }
    }

    private void handleLocalNotification(Context context, Bundle bundle) {
        // If notification ID is not provided by the user for push notification, generate one at random
        if (bundle.getString(NOTIFICATION_ID) == null) {
            SecureRandom randomNumberGenerator = new SecureRandom();
            bundle.putString("notificationId", String.valueOf(randomNumberGenerator.nextInt()));

            ReminderEntity reminder = ReminderEntity.fromBundle(bundle);
            dbHelper.updateReminder(reminder, DBHelper.REMINDERS, null);
        }

        AlarmHelper pushNotificationHelper = new AlarmHelper(context);

        Log.v(LOG_TAG, "sendNotification: " + bundle);

        pushNotificationHelper.createNotificationAndNotify(bundle);
    }
}
