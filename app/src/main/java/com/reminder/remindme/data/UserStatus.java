package com.reminder.remindme.data;

/**
 * Created by Madhusudan Sapkota on 11/25/2018.
 */
public enum UserStatus {
    UNKNOWN(-1), UNAUTHENTICATED(0), AUTHENTICATED(1);

    public final int value;

    UserStatus(int value) {
        this.value = value;
    }

    public static UserStatus of(int value) {
        switch (value) {
            case 1:
                return AUTHENTICATED;
            case 0:
                return UNAUTHENTICATED;
            default:
                return UNKNOWN;
        }
    }
}
