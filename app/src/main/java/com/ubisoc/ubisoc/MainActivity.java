package com.ubisoc.ubisoc;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ubisoc.ubisoc.fragments.EventsFragment;
import com.ubisoc.ubisoc.fragments.PrayerTimesFragment;
import com.ubisoc.ubisoc.fragments.TodayFragment;
import com.ubisoc.ubisoc.fragments.UbisocFragment;
import com.ubisoc.ubisoc.fragments.UbisocInterface;
import com.ubisoc.ubisoc.util.APIUtil;
import com.ubisoc.ubisoc.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements UbisocInterface {

    @BindView(R.id.mainPager)
    ViewPager mainPager;

    @BindView(R.id.mainTabs)
    TabLayout mainTabs;

    static PagerAdapter adapter;

    //Prayer data
    ArrayList<Day> thisMonth = new ArrayList<>();

    //The 3 tabs
    UbisocFragment[] fragments = new UbisocFragment[3];

    //The request code for the notification must be unique, so use this variable then increment it
    static int usedNotificationCode = 0;

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
        mainPager.setAdapter(adapter);

        //Set up tabs then go to "today" tab
        mainTabs.setupWithViewPager(mainPager);
        mainPager.setCurrentItem(1);

        //Load the data on a background thread
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
        t.start();

        //Android O and above needs a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel prayerTimeChannel = new NotificationChannel("prayer_time", "Prayer Times", NotificationManager.IMPORTANCE_HIGH);
            prayerTimeChannel.setDescription("Alerts for prayer times");
            prayerTimeChannel.enableLights(true);
            prayerTimeChannel.setLightColor(Color.RED);
            prayerTimeChannel.enableVibration(true);
            manager.createNotificationChannel(prayerTimeChannel);
        }
    }

    public void schedulePrayerNotification(String prayerName, long unixTime, String humanTime) {
        //We want to alert 5 mins before prayer
        unixTime -= 5 * 60 * 1000;
        if (unixTime < System.currentTimeMillis()) {
            return;
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setClass(this, AlarmReceiver.class);
        intent.putExtra(Keys.EXTRA_PRAYER_NAME, prayerName);
        intent.putExtra(Keys.EXTRA_PRAYER_TIME, humanTime);
        intent.putExtra("debug", humanTime + " " + prayerName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, usedNotificationCode++, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager manager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, unixTime, pendingIntent);
    }

    public void loadData() {
        String result = APIUtil.getPrayerTimesForMonth();
        setup(result);
    }

    public void setup(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);

            //Loop the days in the month, create a Day object for each one. Add to array of days
            JSONArray monthData = json.getJSONArray("data");
            for (int i = 0; i < monthData.length(); i++) {
                JSONObject dayData = monthData.getJSONObject(i);

                String time = dayData.getJSONObject("date").getString("readable");
                JSONObject timings = dayData.getJSONObject("timings");

                Day day = new Day(time, timings);
                thisMonth.add(day);

                //schedule notifications
                for (int prayer = 0; prayer < Prayers.prayers.length; prayer++) {
                    if (prayer == Prayers.PRAYER_SUNRISE) continue;

                    String prayerName = Prayers.prayers[prayer];
                    long unixTime = day.unixTimeForPrayer(prayer);
                    String humanTime = day.prayerTimeForPrayer(prayer);

                    schedulePrayerNotification(prayerName, unixTime, humanTime);

                }
            }
            //Save the json for cache
            SharedPreferences pref = getSharedPreferences(Keys.PREF, 0);
            pref.edit().putString(Keys.PRAYER_TIME_JSON, jsonString).apply();

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
        if (thisMonth.size() > index) {
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
                Toast.makeText(getBaseContext(), "Could not retrieve latest prayer times. Using last known data", Toast.LENGTH_SHORT).show();
                //then use the cache
                SharedPreferences pref = getSharedPreferences(Keys.PREF, 0);
                String json = pref.getString(Keys.PRAYER_TIME_JSON, null);
                if (json != null)
                    setup(json);
            }
        });
    }
}
