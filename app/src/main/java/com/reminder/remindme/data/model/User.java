package com.reminder.remindme.data.model;

import java.util.HashMap;
import java.util.Map;

import static com.reminder.remindme.util.StringUtil.safeString;

/**
 * Created by Madhusudan Sapkota on 8/24/2018.
 */
public class User {
    private String id;
    private String fullName;
    private String address;
    private String mobileNumber;
    private String email;
    private String password;
    private long createdAt;

    public User() {
    }

    public User(String id, String fullName, String mobileNumber, String email) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }

    public User(String id, String fullName, String mobileNumber, String email, String password) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.password = password;
    }

    public User(String id, String fullName, String mobileNumber, String email, String password, long createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public static User fromProperties(Map<String, Object> properties) {
        User user = new User();
        user.setFullName(safeString(properties.get("name")));
        user.setEmail(safeString(properties.get("email")));
        user.setAddress(safeString(properties.get("address")));
        user.setMobileNumber(safeString(properties.get("mobile")));
        return user;
    }

    public Map<String, Object> toProperties() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", fullName);
        data.put("email", email);
        data.put("address", address);
        data.put("mobile", mobileNumber);
        data.put("password", password);
        return data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
