package com.ubisoc.ubisoc.fragments;

import com.ubisoc.ubisoc.Day;

import java.util.ArrayList;

/**
 * Created by hamzah on 18/12/2017.
 * Allows the tabs to get info from the activity
 */

public interface UbisocInterface {

    public Day getToday();

    public ArrayList<Day> getMonth();
}
