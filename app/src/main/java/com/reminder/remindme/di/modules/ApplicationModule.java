package com.reminder.remindme.di.modules;

import com.reminder.remindme.ReminderApplication;
import com.reminder.remindme.data.source.CouchBaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Madhusudan Sapkota on 7/31/2018.
 */
@Module
public class ApplicationModule {

    @Singleton
    @Provides
    CouchBaseDatabase provideDatabase(ReminderApplication application) {
        return new CouchBaseDatabase(application);
    }
}
