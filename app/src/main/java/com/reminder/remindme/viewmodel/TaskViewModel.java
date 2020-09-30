package com.reminder.remindme.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.reminder.remindme.data.model.ReminderEntity;
import com.reminder.remindme.data.model.Response;
import com.reminder.remindme.data.model.State;
import com.reminder.remindme.data.source.DBHelper;

import java.security.SecureRandom;
import java.util.List;

import javax.inject.Inject;


public class TaskViewModel extends ViewModel {

    MutableLiveData<Response<List<ReminderEntity>>> remindersLiveData = new MutableLiveData<>();
    MutableLiveData<Response<List<ReminderEntity>>> reminderHistoryLiveData = new MutableLiveData<>();

    private DBHelper dbHelper;

    @Inject
    public TaskViewModel(DBHelper database) {
        this.dbHelper = database;
        refreshReminders();
        refreshReminderHistory();
    }

    public LiveData<Response<List<ReminderEntity>>> getReminders() {
        return remindersLiveData;
    }

    public LiveData<Response<List<ReminderEntity>>> getReminderHistory() {
        return reminderHistoryLiveData;
    }

    public void refreshReminders() {
        remindersLiveData.setValue(new Response<>("Loading...", State.LOADING, null));

        List<ReminderEntity> reminders = dbHelper.getReminders(DBHelper.REMINDERS);

        if (reminders.isEmpty()) {
            remindersLiveData.setValue(new Response<>("No reminders available", State.ERROR, null));
        } else {
            remindersLiveData.setValue(new Response<>("Success", State.SUCCESS, reminders));
        }
    }

    public void refreshReminderHistory() {
        reminderHistoryLiveData.setValue(new Response<>("Loading...", State.LOADING, null));

        List<ReminderEntity> reminders = dbHelper.getReminders(DBHelper.REMINDER_HISTORIES);

        if (reminders.isEmpty()) {
            reminderHistoryLiveData.setValue(new Response<>("No reminder history available", State.ERROR, null));
        } else {
            reminderHistoryLiveData.setValue(new Response<>("Success", State.SUCCESS, reminders));
        }
    }

    /**
     * @param reminder {@link ReminderEntity} data which is to be stored
     * @return {@link LiveData} of {@link Response} which contains the create reminder process status
     */
    public LiveData<Response<ReminderEntity>> createReminder(ReminderEntity reminder) {
        MutableLiveData<Response<ReminderEntity>> result = new MutableLiveData<>();
        result.setValue(new Response<>("Loading...", State.LOADING, null));

        SecureRandom randomNumberGenerator = new SecureRandom();
        reminder.setNotificationId(reminder.getNotificationId() == null ? String.valueOf(randomNumberGenerator.nextInt()) : reminder.getNotificationId());
        dbHelper.saveReminder(reminder, DBHelper.REMINDERS, new DBHelper.DBOperationListener<ReminderEntity>() {
            @Override
            public void onComplete(ReminderEntity value) {
                result.setValue(new Response<>("Reminder successfully created!", State.SUCCESS, value));
                refreshReminders();
            }

            @Override
            public void onError(Exception e) {
                result.setValue(new Response<>(e.getMessage(), State.ERROR, null));
            }
        });
        return result;
    }

    /**
     * @param reminder {@link ReminderEntity} data which is to be deleted
     * @return {@link LiveData} of {@link Response} which contains the delete reminder process status
     */
    public LiveData<Response<Boolean>> deleteReminder(ReminderEntity reminder) {
        MutableLiveData<Response<Boolean>> result = new MutableLiveData<>();
        result.setValue(new Response<>("Loading...", State.LOADING, null));

        dbHelper.deleteReminder(reminder.getDocumentId(), new DBHelper.DBOperationListener<String>() {
            @Override
            public void onComplete(String value) {
                refreshReminders();
                result.setValue(new Response<>("Reminder Deleted Successfully", State.SUCCESS, null));
            }

            @Override
            public void onError(Exception e) {
                result.setValue(new Response<>("Could not delete the reminder", State.ERROR, null));
            }
        });

        return result;
    }

    public void moveToHistory(ReminderEntity reminder) {
        dbHelper.deleteReminder(reminder.getDocumentId(), null);
        dbHelper.saveReminder(reminder, DBHelper.REMINDER_HISTORIES, null);

        refreshReminders();
        refreshReminderHistory();
    }
}