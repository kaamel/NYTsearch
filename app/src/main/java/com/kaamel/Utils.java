package com.kaamel;

/**
 * Created by kaamel on 9/21/17.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public static String localNytTimeToLong (String sTime) {
        if (sTime == null)
            return "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        formatter.setLenient(false);
        long time = 0;
        try {
            time = formatter.parse(sTime).getTime();
            DateFormat sdtf = SimpleDateFormat.getDateTimeInstance();
            sdtf.setTimeZone(TimeZone.getDefault());
            return  sdtf.format(new Date(time)) + " " + sdtf.getTimeZone().getDisplayName(false, TimeZone.SHORT);
        } catch (ParseException ignored) {
            return "";
        }
    }

    public static String longToDateStringLongFormat(long time) {
        Date date = new Date(time);
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
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

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public static long getYesterdayLong() {
        return getTodayLong() - DAY_IN_MILLI;
    }

    public static long getTomorrowLong() {
        return getTodayLong() + DAY_IN_MILLI;
    }
}
