package com.reminder.remindme.data.model;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reminder.remindme.util.StringUtil.*;

/**
 * Created by Madhusudan Sapkota on 11/29/2018.
 */
public class ReminderEntity implements Serializable {
    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;


    private String documentId;
    private String notificationId;

    private String title;

    private String repeat;
    private String repeatNo;
    private String repeatType;

    private boolean active;
    private boolean playSound;

    private long remindTime;
    private long createdAt;

    public ReminderEntity() {
        this.createdAt = Calendar.getInstance().getTimeInMillis();
    }

    public static ReminderEntity fromBundle(Bundle bundle) {
        ReminderEntity reminder = new ReminderEntity();
        reminder.setDocumentId(bundle.getString("documentId"));
        reminder.setNotificationId(bundle.getString("notificationId"));

        reminder.setTitle(bundle.getString("title"));

        reminder.setRepeat(bundle.getString("repeat"));
        reminder.setRepeatNo(bundle.getString("repeatNo"));
        reminder.setRepeatType(bundle.getString("repeatType"));

        reminder.setActive(bundle.getBoolean("active"));
        reminder.setPlaySound(bundle.getBoolean("playSound"));

        reminder.setCreatedAt(bundle.getLong("createdAt"));
        reminder.setRemindTime(bundle.getLong("remindTime"));

        return reminder;
    }

    public static List<Object> generateKeys(Map<String, Object> document) {
        List<Object> keys = new ArrayList<Object>();
        keys.add(document.get("title"));
        keys.add(document.get("repeat"));
        keys.add(document.get("repeatNo"));
        keys.add(document.get("repeatType"));
        keys.add(document.get("active"));
        keys.add(document.get("playSound"));
        keys.add(document.get("createdAt"));
        keys.add(document.get("remindTime"));
        keys.add(document.get("notificationId"));
        return keys;
    }


    public static ReminderEntity fromProperties(String documentId, Map<String, Object> data) {
        Object title = data.get("title");
        Object repeat = data.get("repeat");
        Object repeatNo = data.get("repeatNo");
        Object repeatType = data.get("repeatType");

        Object active = data.get("active");
        Object playSound = data.get("playSound");

        Object reminderTime = data.get("remindTime");
        Object createdDate = data.get("createdAt");

        Object notificationId = data.get("notificationId");

        ReminderEntity reminder = new ReminderEntity();
        reminder.setDocumentId(documentId);
        reminder.setNotificationId(safeString(notificationId));

        reminder.setTitle(safeString(title));

        reminder.setRepeat(safeString(repeat));
        reminder.setRepeatNo(safeString(repeatNo));
        reminder.setRepeatType(safeString(repeatType));

        reminder.setActive(safeBoolean(active));
        reminder.setPlaySound(safeBoolean(playSound));

        reminder.setCreatedAt(safeLong(createdDate));
        reminder.setRemindTime(safeLong(reminderTime));

        return reminder;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(long remindTime) {
        this.remindTime = remindTime;
    }

    public String getReminderType() {
        return "";
    }

    public void setReminderType(String reminderType) {

    }

    public boolean isPlaySound() {
        return playSound;
    }

    public void setPlaySound(boolean playSound) {
        this.playSound = playSound;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String mRepeat) {
        this.repeat = mRepeat;
    }

    public String getRepeatNo() {
        return repeatNo;
    }

    public void setRepeatNo(String mRepeatNo) {
        this.repeatNo = mRepeatNo;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String mRepeatType) {
        this.repeatType = mRepeatType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);

        bundle.putString("repeat", repeat);
        bundle.putString("repeatNo", repeatNo);
        bundle.putString("repeatType", repeatType);

        bundle.putBoolean("active", active);
        bundle.putBoolean("playSound", playSound);

        bundle.putLong("createdAt", createdAt);
        bundle.putLong("remindTime", remindTime);

        bundle.putString("documentId", documentId);
        bundle.putString("notificationId", notificationId);
        return bundle;
    }

    public HashMap<String, Object> toProperties() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("title", title);

        data.put("repeat", repeat);
        data.put("repeatNo", repeatNo);
        data.put("repeatType", repeatType);

        data.put("active", active);
        data.put("playSound", playSound);

        data.put("createdAt", createdAt);
        data.put("remindTime", remindTime);

        data.put("notificationId", notificationId);
        return data;
    }

    public long repeatTime() {
        long mRepeatTime = 0;
        // Check repeat type
        switch (repeatType) {
            case "Minute":
                mRepeatTime = Integer.parseInt(repeatNo) * milMinute;
                break;
            case "Hour":
                mRepeatTime = Integer.parseInt(repeatNo) * milHour;
                break;
            case "Day":
                mRepeatTime = Integer.parseInt(repeatNo) * milDay;
                break;
            case "Week":
                mRepeatTime = Integer.parseInt(repeatNo) * milWeek;
                break;
            case "Month":
                mRepeatTime = Integer.parseInt(repeatNo) * milMonth;
                break;
        }
        return mRepeatTime;
    }
}
