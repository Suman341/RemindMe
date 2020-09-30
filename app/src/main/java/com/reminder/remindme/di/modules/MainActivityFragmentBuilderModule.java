package com.reminder.remindme.di.modules;

import com.reminder.remindme.ui.history.HistoryFragment;
import com.reminder.remindme.ui.reminders.ReminderFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Madhusudan Sapkota on 11/30/2018.
 */
@Module
public abstract class MainActivityFragmentBuilderModule {
    @ContributesAndroidInjector
    abstract ReminderFragment contributeReminderFragment();

    @ContributesAndroidInjector
    abstract HistoryFragment contributeHistoryFragment();

}
