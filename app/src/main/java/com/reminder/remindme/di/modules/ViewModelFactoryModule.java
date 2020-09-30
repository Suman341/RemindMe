package com.reminder.remindme.di.modules;

import androidx.lifecycle.ViewModelProvider;

import com.reminder.remindme.viewmodel.ViewModelFactory;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Madhusudan Sapkota on 11/25/2018.
 */
@Module
public abstract class ViewModelFactoryModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
