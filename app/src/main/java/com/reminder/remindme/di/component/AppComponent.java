package com.reminder.remindme.di.component;

import com.reminder.remindme.ReminderApplication;
import com.reminder.remindme.data.source.CouchBaseDatabase;
import com.reminder.remindme.di.modules.ActivityBuilderModule;
import com.reminder.remindme.di.modules.ApplicationModule;
import com.reminder.remindme.di.modules.FragmentBuildersModule;
import com.reminder.remindme.di.modules.ServiceModule;
import com.reminder.remindme.di.modules.ViewModelFactoryModule;
import com.reminder.remindme.di.modules.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by Madhusudan Sapkota on 7/31/2018.
 */
@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ApplicationModule.class,
                ActivityBuilderModule.class,
                FragmentBuildersModule.class,
                ServiceModule.class,
                ViewModelModule.class,
                ViewModelFactoryModule.class
        }
)
public interface AppComponent {
    void inject(ReminderApplication application);

    ReminderApplication application();

    CouchBaseDatabase couchbaseDatabase();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(ReminderApplication application);

        AppComponent build();
    }
}
