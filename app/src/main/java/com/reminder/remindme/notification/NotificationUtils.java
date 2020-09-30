package com.reminder.remindme.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.reminder.remindme.R;
import com.reminder.remindme.data.model.ReminderEntity;

public class NotificationUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    public static void createChannel(Context context, String id, String name) {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Alaram notifications are shown in this channel");
        configureChannel(context, channel);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static void configureChannel(Context context, NotificationChannel channel) {
        long[] vibratingPattern = {500L};

        channel.setVibrationPattern(vibratingPattern);
        channel.enableVibration(true);
        channel.setLightColor(ContextCompat.getColor(context, R.color.colorPrimary));
        channel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManagerCompat.from(context).createNotificationChannel(channel);
    }

    public static Notification createNotification(Context context, ReminderEntity reminder, PendingIntent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context, "remindMe", "Alarm");
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "remindMe")
                .setContentTitle("Alarm Notification")
                .setAutoCancel(true)
                .setContentText(reminder.getTitle() == null ? "Alarm" : reminder.getTitle())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_alarm_on_white_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setLights(ContextCompat.getColor(context, R.color.colorPrimary), 2000, 2000);

        if (reminder.isPlaySound()) {
            builder.setSound(alarmSound);
        }

        if (intent != null) {
            builder.setContentIntent(intent);
        }

        return builder.build();
    }
}
