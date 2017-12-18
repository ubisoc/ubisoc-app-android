package com.ubisoc.ubisoc.util;

import android.util.Log;

import java.util.Date;

/**
 * Created by hamzah on 18/12/2017.
 * Some handy util methods
 */

public class Util {

    /**
     * Formats a Date into human readable form eg "18th December 2017"
     *
     * @param d A Date
     * @return The human readable form
     */
    public static String getHumanDate(Date d) {
        //Get the current date
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December"};


        int date = d.getDate();
        int month = d.getMonth();
        int day = d.getDay();

        String todaysDate = days[day] + " " + date + suffixForDay(date) + " " + months[month];

        return todaysDate;
    }


    /**
     * Get the suffix for the date
     *
     * @param date The day in month
     * @return st nd rd th according to date
     */
    public static String suffixForDay(int date) {
        int secondDigit = date % 10;
        int firstDigit = (date - secondDigit) / 10;
        if (firstDigit == 1) return "th";
        if (secondDigit == 1) return "st";
        if (secondDigit == 2) return "nd";
        if (secondDigit == 3) return "rd";
        return "th";
    }

    /**
     * Log to console
     * @param s What to log
     */
    public static void log(String s) {
        Log.d("UBISOC", s);
    }

}
