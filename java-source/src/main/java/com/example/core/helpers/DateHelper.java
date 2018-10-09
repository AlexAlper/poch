package com.example.core.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateHelper {

    public final static String SIMPLE_DATE_FORMAT_RU = "d.MM.yyyy";


    public static Date createFromString(String strValue, String format) {
        if (strValue == null) return null;

        DateFormat formatter = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = formatter.parse(strValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static String getDateFormatFromDateTime_dMyyyy(Date dateTime) {
        if (dateTime == null) return null;

        DateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMAT_RU);
        return formatter.format(dateTime);
    }
}
