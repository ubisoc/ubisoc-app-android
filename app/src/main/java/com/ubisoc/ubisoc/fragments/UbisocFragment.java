package com.ubisoc.ubisoc.fragments;


import android.support.v4.app.Fragment;

/**
 * Created by hamzah on 18/12/2017.
 * Provides a common base for the 3 tabs
 */

public abstract class UbisocFragment extends Fragment {

    UbisocInterface callback;

    public void setCallback(UbisocInterface callback) {
        this.callback = callback;
    }

    public abstract void notifyDatasetChanged();

}
