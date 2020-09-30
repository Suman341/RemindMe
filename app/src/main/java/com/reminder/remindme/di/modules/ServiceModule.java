package com.reminder.remindme.di.modules;

import com.reminder.remindme.notification.NotificationBootEventReceiver;
import com.reminder.remindme.notification.NotificationPublisher;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Madhusudan Sapkota on 7/31/2018.
 */
@Module
public abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract NotificationBootEventReceiver provideNotificationServiceStarterReceiver();

    @ContributesAndroidInjector
    abstract NotificationPublisher provideNotificationEventReceiver();
}
