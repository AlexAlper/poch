package com.example.core.helpers;

/**
 * Работа со строками
 */
public class StringHelper {
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static String EMPTY = "";

    public static boolean isEmptyOrWhitespace(String s) {
        return s == null || isWhitespace(s);

    }

    private static boolean isWhitespace(String s) {
        int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public static String getValueOrDefault(String value, String defaultValue) {
        if (StringHelper.isEmptyOrWhitespace(value)) {
            return defaultValue;
        } else {
            return value;
        }
    }


    public static String getValueOrDefault(String value) {
        if (StringHelper.isEmptyOrWhitespace(value)) {
            return StringHelper.EMPTY;
        } else {
            return value;
        }
    }
}
