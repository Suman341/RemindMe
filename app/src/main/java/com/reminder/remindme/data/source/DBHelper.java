package com.reminder.remindme.data.source;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.SavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.util.Log;
import com.reminder.remindme.data.model.ReminderEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class DBHelper {
    public static String REMINDERS = "reminderList";
    public static String REMINDER_HISTORIES = "reminderHistoryList";
    private CouchBaseDatabase database;

    @Inject
    public DBHelper(CouchBaseDatabase database) {
        this.database = database;
    }

    public ReminderEntity getReminder(String documentId) {
        Document document = database.getDatabase().getDocument(documentId);
        Map<String, Object> data = document == null ? Collections.emptyMap() : document.getProperties();
        return ReminderEntity.fromProperties(documentId, data);
    }

    private List<ReminderEntity> extractReminders(QueryEnumerator enumerator) {
        List<ReminderEntity> entities = new ArrayList<>();
        if (enumerator == null) return entities;

        for (int i = 0; i < enumerator.getCount(); i++) {
            Document document = enumerator.getRow(i).getDocument();
            Map<String, Object> data = document.getProperties();
            entities.add(ReminderEntity.fromProperties(document.getId(), data));
        }
        return entities;
    }

    public List<ReminderEntity> getReminders(String storeName) {
        try {
            QueryEnumerator enumerator = createReminderView(storeName).createQuery().run();
            return extractReminders(enumerator);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public View createReminderView(String storeName) {
        View view = database.getDatabase().getView(storeName);
        if (view.getMap() == null) {
            Mapper map = (document, emitter) -> {
                if (storeName.equals(document.get("type"))) {
                    emitter.emit(ReminderEntity.generateKeys(document), document);
                }
            };
            view.setMap(map, "1.0");
        }
        return view;
    }

    public void saveReminder(ReminderEntity reminder, String storeName, DBOperationListener<ReminderEntity> listener) {
        Map<String, Object> properties = reminder.toProperties();
        properties.put("type", storeName);

        Document document = database.getDatabase().createDocument();
        reminder.setDocumentId(document.getId());
        try {
            SavedRevision revision = document.putProperties(properties);
            reminder.setDocumentId(revision.getId());
            if (listener != null) listener.onComplete(reminder);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            if (listener != null) listener.onError(e);
        }
    }


    public void updateReminder(ReminderEntity reminder, String storeName, DBOperationListener<String> listener) {
        Document document = database.getDatabase().getDocument(reminder.getDocumentId());

        if (document == null) {
            listener.onError(new IllegalArgumentException("Could not found the reminder"));
            return;
        }

        Map<String, Object> properties = reminder.toProperties();
        properties.put("type", storeName);

        try {

            document.purge();
            document.putProperties(properties);
            if (listener != null) listener.onComplete("Reminder successfully updated");
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            if (listener != null) listener.onError(e);
        }
    }

    public void deleteReminder(String documentId, DBOperationListener<String> listener) {
        try {
            Document document = database.getDatabase().getDocument(documentId);
            document.delete();

            if (listener != null) listener.onComplete("Reminder deleted successfully");
        } catch (Exception e) {
            if (listener != null) listener.onError(e);
        }
    }

    public interface DBOperationListener<V> {
        void onComplete(V value);

        void onError(Exception e);
    }
}
