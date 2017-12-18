package com.ubisoc.ubisoc.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ubisoc.ubisoc.R;

public class EventsFragment extends UbisocFragment {


    public EventsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void notifyDatasetChanged() {

    }
}
