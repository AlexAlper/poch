package com.example.core.helpers;

/**
 * Created by Tarasenko on 30.09.2014.
 */
public class StringComparator {


    public static boolean IsEquals(String firstString, String secondString) {
        if (null == firstString && null == secondString) return true;
        if (null == firstString || null == secondString) return false;

        return firstString.equalsIgnoreCase(secondString);
    }


    public static boolean Contains(String firstString, String secondString) {
        if (null == firstString && null == secondString) return true;
        if (null == firstString || null == secondString) return false;

        return firstString.toLowerCase().contains(secondString.toLowerCase());
    }

    public static int Compare(String firstString, String secondString) {
        if (null == firstString && null == secondString) return 0;
        if (null == firstString) return -1;
        if (null == secondString) return 1;

        return firstString.compareToIgnoreCase(secondString);
    }

}
