package com.reminder.remindme.util;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class AppExecutors{
    private Executor diskIO;
    private Executor networkThread;
    private Executor mainThread;

    @Inject
    public AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), new MainThreadExecutor());
    }

    public AppExecutors(Executor diskIO, Executor networkThread, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkThread = networkThread;
        this.mainThread = mainThread;
    }

    public Executor mainThread() {
        return mainThread;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkThread() {
        return networkThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
