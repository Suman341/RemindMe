package com.reminder.remindme.data.model;

/**
 * Created by Madhusudan Sapkota on 11/25/2018.
 */
public enum State {
    LOADING(0), ERROR(1), SUCCESS(2);

    public final int value;

    State(int value) {
        this.value = value;
    }
}
