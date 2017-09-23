package com.kaamel;

/**
 * Created by kaamel on 9/21/17.
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    private static final long DAY_IN_MILLI = 24 * 60 * 60 * 1000;

    public static DateFormat getDateFormat() {
        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    }

    public static boolean validDate(String dueDate) {
        DateFormat formatter = getDateFormat();
        if (dueDate == null || dueDate.trim().equals(""))
            return false;
        formatter.setLenient(false);
        try {
            formatter.parse(dueDate);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static long todayToLong() {
        return dateToLong(null);
    }

    public static String getTodayString() {
        return longToDateString(System.currentTimeMillis());
    }

    public static long getTodayLong() {
        return dateToLong(null);
    }

    public static String longToDateString(long time) {
        Date date = new Date(time);
        DateFormat formatter = getDateFormat();
        formatter.setLenient(false);
        return formatter.format(date);
    }

    public static String longToNYTDateString(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false);
        return formatter.format(date);
    }

    public static long dateToLong(String date) {
        DateFormat formatter = getDateFormat();
        formatter.setLenient(false);
        long time = System.currentTimeMillis();
        try {
            time = formatter.parse(longToDateString(System.currentTimeMillis())).getTime(); //time at the begining of today
            if (date != null && !date.trim().equals("")) {
                time = formatter.parse(formatter.format(formatter.parse(date))).getTime(); //time at the begining of the date
            }
        } catch (ParseException ignored) {
        }
        return time;
    }

    public static int longToYear(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return Integer.valueOf(sdf.format(new Date(time)));
    }

    public static int longToMonth(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return Integer.valueOf(sdf.format(new Date(time)));
    }

    public static int longToDay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("d");
        return Integer.valueOf(sdf.format(new Date(time)));
    }

    public static long getYesterdayLong() {
        return getTodayLong() - DAY_IN_MILLI;
    }

    public static long getTomorrowLong() {
        return getTodayLong() + DAY_IN_MILLI;
    }
}
