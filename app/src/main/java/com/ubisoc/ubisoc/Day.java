package com.ubisoc.ubisoc;

import com.ubisoc.ubisoc.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by hamzah on 18/12/2017.
 * Contains the info for a day
 */

public class Day {

    //The date as dd MMM yyyy representing this day
    private String date;

    //prayers. F S D A M I
    private String[] prayerTimes;
    private String[] jamaahTimes;

    /**
     * @param date The date for the day as dd MMM yyyy
     * @param timings   The JSON object containing a key for each of Fajr Sunrise Dhuhr Asr Maghrib Isha
     * @throws JSONException Invalid object
     */
    public Day(String date, JSONObject timings) throws JSONException {
        //this is the json from the api https://aladhan.com/prayer-times-api#GetCalendar

        this.date = date;
        this.prayerTimes = new String[6];
        this.jamaahTimes = new String[6];

        //Read in each prayer
        prayerTimes[0] = getTimeFromJSON(timings, "Fajr");
        prayerTimes[1] = getTimeFromJSON(timings, "Sunrise");
        prayerTimes[2] = getTimeFromJSON(timings, "Dhuhr");
        prayerTimes[3] = getTimeFromJSON(timings, "Asr");
        prayerTimes[4] = getTimeFromJSON(timings, "Maghrib");
        prayerTimes[5] = getTimeFromJSON(timings, "Isha");
    }

    private String getTimeFromJSON(JSONObject json, String key) throws JSONException {
        //The API returns each time with "(GMT)" at the end, remove this
        return json.getString(key).replaceAll("\\(GMT\\)", "").trim();
    }

    /**
     * Returns the time for a prayer
     * @param prayer index of prayer name in [fajr, sunrise, dhuhr, asr, maghrib, isha]
     * @return The time
     */
    public String prayerTimeForPrayer(int prayer) {
        return prayerTimes[prayer];
    }

    /**
     * Returns the time for a prayer
     * @param prayer index of prayer name in [fajr, sunrise, dhuhr, asr, maghrib, isha]
     * @return The time
     */
    public String jamaahTimeForPrayer(int prayer) {
        return prayerTimes[prayer];
    }

    public long unixTimeForPrayer(int prayer){
        String time = prayerTimeForPrayer(prayer);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        try {
            long unixTime = sdf.parse(date + " " + time).getTime();
            Util.log(date + " " + time + " = " + unixTime);
            return unixTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }


}
