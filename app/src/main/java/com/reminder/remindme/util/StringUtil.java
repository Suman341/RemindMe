package com.reminder.remindme.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
    public static String MD5(String string) {
        if (string == null)
            return null;

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] inputBytes = string.getBytes();
            byte[] hashBytes = digest.digest(inputBytes);
            return byteArrayToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) { }

        return null;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }


    public static String safeString(Object value) {
        if (value instanceof String) return (String) value;
        else return null;
    }

    public static long safeLong(Object value) {
        try {
            if (value != null) return (long) value;
            else return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean safeBoolean(Object value) {
        try {
            if (value != null) return (boolean) value;
            else return false;
        } catch (Exception e) {
            return false;
        }
    }
}
