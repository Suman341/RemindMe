package com.reminder.remindme.di.modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.reminder.remindme.di.ViewModelKey;
import com.reminder.remindme.viewmodel.TaskViewModel;
import com.reminder.remindme.viewmodel.UserViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by Madhusudan Sapkota on 7/31/2018.
 */
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TaskViewModel.class)
    abstract ViewModel bindTaskViewModel(TaskViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel bindUserViewModel(UserViewModel viewModel);
}
