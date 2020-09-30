package com.reminder.remindme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.reminder.remindme.R;
import com.reminder.remindme.ui.about.AboutUsFragment;
import com.reminder.remindme.ui.auth.AuthActivity;
import com.reminder.remindme.ui.history.HistoryFragment;
import com.reminder.remindme.ui.reminders.ReminderFragment;
import com.reminder.remindme.viewmodel.UserViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView nameTV = header.findViewById(R.id.nameTV);
        TextView emailTV = header.findViewById(R.id.emailTV);
        TextView mobileTV = header.findViewById(R.id.mobileTV);

        userViewModel.profile().observe(this, response -> {
            if (response == null) return;

            if (response.isSuccessful()) {
                nameTV.setText(response.getData().getFullName());
                emailTV.setText(response.getData().getEmail());
                mobileTV.setText(response.getData().getMobileNumber());
            } else if (response.hasError()) {
                Toast.makeText(this, "Could not load the profile", Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new ReminderFragment())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction()
                        .replace(
                                R.id.main_container,
                                new ReminderFragment())
                        .commitAllowingStateLoss();
                break;
            case R.id.nav_history:
                getSupportFragmentManager().beginTransaction()
                        .replace(
                                R.id.main_container,
                                new HistoryFragment())
                        .commitAllowingStateLoss();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction()
                        .replace(
                                R.id.main_container,
                                new AboutUsFragment())
                        .commitAllowingStateLoss();
                break;
            case R.id.nav_logout:
                userViewModel.logout();
                startActivity(new Intent(this, AuthActivity.class));
                finish();
                break;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingFragmentInjector;
    }
}
