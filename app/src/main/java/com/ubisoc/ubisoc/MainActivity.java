package com.ubisoc.ubisoc;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ubisoc.ubisoc.fragments.EventsFragment;
import com.ubisoc.ubisoc.fragments.PrayerTimesFragment;
import com.ubisoc.ubisoc.fragments.TodayFragment;
import com.ubisoc.ubisoc.fragments.UbisocFragment;
import com.ubisoc.ubisoc.fragments.UbisocInterface;
import com.ubisoc.ubisoc.util.APIUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements UbisocInterface {

    @BindView(R.id.mainPager)
    ViewPager mainPager;

    @BindView(R.id.mainTabs)
    PagerTabStrip mainTabs;

    static PagerAdapter adapter;

    //Prayer data
    ArrayList<Day> thisMonth = new ArrayList<>();

    //The 3 tabs
    UbisocFragment[] fragments = new UbisocFragment[3];

    private class MainPagerAdapter extends FragmentStatePagerAdapter {

        public MainPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragments[0] = new EventsFragment();
            fragments[1] = new TodayFragment();
            fragments[2] = new PrayerTimesFragment();

            for (UbisocFragment f : fragments) {
                f.setCallback(MainActivity.this);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

        private final String[] titles = {"Events", "Today", "Prayer Times"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Set up the UI
        adapter = new MainPagerAdapter(getSupportFragmentManager());

        mainTabs.setTabIndicatorColor(Color.WHITE);
        mainTabs.setTextColor(Color.WHITE);

        mainTabs.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        mainPager.setAdapter(adapter);

        android.support.v7.app.ActionBar ab = this.getSupportActionBar();

        mainPager.setCurrentItem(1);
        if (Build.VERSION.SDK_INT >= 21) {
            mainTabs.setElevation(ab.getElevation());
            ab.setElevation(0);
        }

        //Load the data on a background thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
        t.start();
    }

    public void loadData() {
        String result = APIUtil.getPrayerTimesForMonth();

        try {
            JSONObject json = new JSONObject(result);

            //Loop the days in the month, create a Day object for each one. Add to array of days
            JSONArray monthData = json.getJSONArray("data");
            for (int i = 0; i < monthData.length(); i++) {
                JSONObject dayData = monthData.getJSONObject(i);

                String time = dayData.getJSONObject("date").getString("timestamp");
                JSONObject timings = dayData.getJSONObject("timings");

                Day day = new Day(Long.parseLong(time), timings);
                thisMonth.add(day);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showErrorMessage();
        }


        //Refresh the fragments
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UbisocFragment f : fragments) {
                    f.notifyDatasetChanged();
                }
            }
        });

    }

    @Override
    public Day getToday() {
        Date d = new Date();

        int index = d.getDate() - 1;
        if (thisMonth.size() >= index) {
            Day today = thisMonth.get(index);
            return today;
        }
        return null;
    }

    @Override
    public ArrayList<Day> getMonth() {
        return thisMonth;
    }

    public void showErrorMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Error");
                b.setMessage("Could not retrieve prayer times, please check your internet connection");
                b.setPositiveButton("Cancel", null);
                b.show();
            }
        });
    }
}
