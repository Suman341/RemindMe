package com.reminder.remindme.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.reminder.remindme.R;
import com.reminder.remindme.data.UserStatus;
import com.reminder.remindme.data.model.State;
import com.reminder.remindme.ui.MainActivity;
import com.reminder.remindme.ui.auth.AuthActivity;
import com.reminder.remindme.viewmodel.UserViewModel;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SplashScreenActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    private final String TAG = SplashScreenActivity.class.getSimpleName();
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);


        // Observes the user status
        userViewModel.checkAuthentication().observe(this, response -> {
            if (response == null || response.getState() == State.LOADING) {
                Log.d(TAG, "Null response/Loading...");
                return;
            }

            // check if user is authenticated or not
            if (response.getState() == State.SUCCESS && response.getData() == UserStatus.AUTHENTICATED) {
                // redirect to the Dashboard screen
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        navigateToDashboard();
                    }
                }, 1000);
            } else {
                // redirect to the Login screen
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        navigateToLogin();
                    }
                }, 1000);
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }
}
