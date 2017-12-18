package com.ubisoc.ubisoc.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hamzah on 18/12/2017.
 * Handles calling the API
 */

public class APIUtil {

    public static class KeyValuePair {
        String key, value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Gets prayer times for the current month
     *
     * @return
     */
    public static String getPrayerTimesForMonth() {
        List<KeyValuePair> params = new ArrayList<>();

        params.add(new KeyValuePair("latitude", "52.4511745"));//UoB coordinates
        params.add(new KeyValuePair("longitude", "-1.9293285"));

        try {
            return sendHTTPRequest("http://api.aladhan.com/calendar", params);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Sends a HTTP Request
     *
     * @param URL    URL to send to
     * @param params GET parameters
     * @return The string from the server
     * @throws IOException
     */
    public static String sendHTTPRequest(String URL, List<KeyValuePair> params) throws IOException {
        String result;

        URL url = new URL(URL + getQuery(params));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.connect();
        result = inputStreamToString(conn.getInputStream());
        conn.disconnect();

        Util.log("Result from " + url + ": " + result);
        return result;
    }

    /**
     * Makes an array of {@link KeyValuePair} into a url string
     *
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getQuery(List<KeyValuePair> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (KeyValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.value, "UTF-8"));
        }
        return "?" + result.toString();
    }

    /**
     * Reads in a String from an input stream
     * @param is
     * @return
     */
    public static String inputStreamToString(InputStream is) {
        String line;
        Util.log(is.toString());

        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
                Util.log(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total.toString();
    }

}
