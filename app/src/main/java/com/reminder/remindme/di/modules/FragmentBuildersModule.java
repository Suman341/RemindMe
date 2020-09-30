package com.reminder.remindme.di.modules;

import com.reminder.remindme.ui.auth.LoginFragment;
import com.reminder.remindme.ui.auth.SignUpFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Madhusudan Sapkota on 7/31/2018.
 */
@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

    @ContributesAndroidInjector
    abstract SignUpFragment contributeSignUpFragment();
}