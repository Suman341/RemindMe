package com.reminder.remindme.di.modules;

import com.reminder.remindme.ui.MainActivity;
import com.reminder.remindme.ui.add.AddReminderActivity;
import com.reminder.remindme.ui.auth.AuthActivity;
import com.reminder.remindme.ui.splash.SplashScreenActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Madhusudan Sapkota on 11/25/2018.
 */

@Module
public abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract SplashScreenActivity bindSplashScreenActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract AuthActivity bindAuthActivity();

    @ContributesAndroidInjector(modules = MainActivityFragmentBuilderModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector
    abstract AddReminderActivity bindAddReminderActivity();
}
