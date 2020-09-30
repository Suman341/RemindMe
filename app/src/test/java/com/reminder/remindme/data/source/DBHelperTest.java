package com.reminder.remindme.data.source;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.SavedRevision;
import com.reminder.remindme.data.model.ReminderEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DBHelperTest {

    private CouchBaseDatabase databaseWrapper;

    private Database database;

    private DBHelper dbHelper;

    @Before
    public void setUp() throws Exception {
        database = Mockito.mock(Database.class);
        databaseWrapper = Mockito.mock(CouchBaseDatabase.class);
        Mockito.when(databaseWrapper.getDatabase()).thenReturn(database);

        dbHelper = new DBHelper(databaseWrapper);
    }

    @Test
    public void testSaveReminder() throws CouchbaseLiteException {
        ReminderEntity testEntity = new ReminderEntity();
        testEntity.setTitle("Test title");

        Document document = Mockito.mock(Document.class);

        SavedRevision revision = Mockito.mock(SavedRevision.class);
        Mockito.when(revision.getId()).thenReturn("1");

        Mockito.when(document.putProperties(Mockito.anyMap())).thenReturn(revision);
        Mockito.when(database.createDocument()).thenReturn(document);

        DBHelper.DBOperationListener<ReminderEntity> reminderAddListener = Mockito.mock(DBHelper.DBOperationListener.class);

        dbHelper.saveReminder(testEntity, DBHelper.REMINDERS, reminderAddListener);

        Mockito.verify(reminderAddListener).onComplete(testEntity);
    }

    @Test
    public void testUpdateReminder() throws CouchbaseLiteException {
        ReminderEntity testEntity = new ReminderEntity();
        testEntity.setTitle("Test title");
        testEntity.setDocumentId("1");

        Document document = Mockito.mock(Document.class);
        SavedRevision revision = Mockito.mock(SavedRevision.class);

        Mockito.doNothing().when(document).purge();
        Mockito.when(document.putProperties(Mockito.anyMap())).thenReturn(revision);

        Mockito.when(database.getDocument("1")).thenReturn(document);

        DBHelper.DBOperationListener<String> reminderAddListener = Mockito.mock(DBHelper.DBOperationListener.class);

        dbHelper.updateReminder(testEntity, DBHelper.REMINDERS, reminderAddListener);

        Mockito.verify(reminderAddListener).onComplete(Mockito.anyString());
    }

    @Test
    public void testDeleteReminder() throws CouchbaseLiteException {
        ReminderEntity testEntity = new ReminderEntity();
        testEntity.setTitle("Test title");
        testEntity.setDocumentId("1");

        Document document = Mockito.mock(Document.class);
        SavedRevision revision = Mockito.mock(SavedRevision.class);

        Mockito.when(document.delete()).thenReturn(true);

        Mockito.when(database.getDocument("1")).thenReturn(document);

        DBHelper.DBOperationListener<String> reminderAddListener = Mockito.mock(DBHelper.DBOperationListener.class);

        dbHelper.deleteReminder(testEntity.getDocumentId(), reminderAddListener);

        Mockito.verify(reminderAddListener).onComplete(Mockito.anyString());
    }

    @After
    public void tearDown() throws Exception {
        database = null;
        databaseWrapper = null;
        dbHelper = null;
    }
}