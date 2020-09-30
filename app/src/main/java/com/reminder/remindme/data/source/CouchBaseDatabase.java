package com.reminder.remindme.data.source;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.reminder.remindme.ReminderApplication;
import com.reminder.remindme.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

/**
 * Created by Madhusudan Sapkota on 9/16/2018.
 */

@Singleton
public class CouchBaseDatabase implements ReminderApplication.LifeCycleListener {
    public static final String TAG = CouchBaseDatabase.class.getSimpleName();


    private static final String STORAGE_TYPE = Manager.SQLITE_STORAGE;

    // Encryption (Don't store encryption key in the source code. We are doing it here just as an example):
    private static final boolean ENCRYPTION_ENABLED = false;
    private static final String ENCRYPTION_KEY = "reminderPassword";

    // Guest database:
    private static final String DATABASE_NAME = "remind_me";
    private static final String GUEST_DATABASE_NAME = "guest";

    private static final boolean LOGGING_ENABLED = true;
    private final Map<String, Database> databaseMap = new HashMap<>();
    private Database database;
    private Manager mManager;

    public CouchBaseDatabase(ReminderApplication application) {
        application.registerLifeCycleListener(this);
        try {
            AndroidContext context = new AndroidContext(application);
            mManager = new Manager(context, Manager.DEFAULT_OPTIONS);
        } catch (Exception e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot create Manager object", e);
        }
    }

    private Manager getManager() {
        return mManager;
    }

    public Database getUserDatabase(String name) {
        Database database = databaseMap.get(name);
        if (database == null) {
            try {
                String dbName = "db" + StringUtil.MD5(name);
                DatabaseOptions options = new DatabaseOptions();
                options.setCreate(true);
                options.setStorageType(STORAGE_TYPE);
                options.setEncryptionKey(ENCRYPTION_ENABLED ? ENCRYPTION_KEY : null);
                return getManager().openDatabase(dbName, options);
            } catch (CouchbaseLiteException e) {
                com.couchbase.lite.util.Log.e(TAG, "Cannot create database for name: " + name, e);
            }
        }
        return database;
    }

    public Database getDatabase() {
        return getUserDatabase(GUEST_DATABASE_NAME);
    }

    @Override
    public void onCreate() {
        enableLogging();
    }

    @Override
    public void onTerminate() {

    }

    private void enableLogging() {
        if (LOGGING_ENABLED) {
            Manager.enableLogging(TAG, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC_ASYNC_TASK, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_QUERY, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_VIEW, com.couchbase.lite.util.Log.VERBOSE);
            Manager.enableLogging(com.couchbase.lite.util.Log.TAG_DATABASE, com.couchbase.lite.util.Log.VERBOSE);
        }
    }

}
