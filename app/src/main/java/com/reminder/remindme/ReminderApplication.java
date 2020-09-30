package com.reminder.remindme;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;

import com.reminder.remindme.di.injector.ApplicationInjector;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;
import timber.log.Timber;


public class ReminderApplication extends Application implements HasActivityInjector, HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Inject
    DispatchingAndroidInjector<Service> serviceInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverDispatchingAndroidInjector;


    private Collection<LifeCycleListener> lifeCycleListeners = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        ApplicationInjector.init(this);

        for (LifeCycleListener listener : lifeCycleListeners) {
            listener.onCreate();
        }
    }

    @Override
    public void onTerminate() {
        for (LifeCycleListener listener : lifeCycleListeners) {
            listener.onTerminate();
        }
        super.onTerminate();
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return broadcastReceiverDispatchingAndroidInjector;
    }

    public void registerLifeCycleListener(LifeCycleListener lifeCycleListener) {
        lifeCycleListeners.add(lifeCycleListener);
    }

    public interface LifeCycleListener {
        void onCreate();

        void onTerminate();
    }
}
