package com.ubisoc.ubisoc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Widget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

            Day today = null;
            int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            String jsonString = context.getSharedPreferences(Keys.PREF, 0).getString(Keys.PRAYER_TIME_JSON, null);
            if (jsonString != null) {
                try {
                    JSONObject json = new JSONObject(jsonString);

                    //Loop the days in the month, create a Day object for each one. Add to array of days
                    JSONArray monthData = json.getJSONArray("data");

                    JSONObject dayData = monthData.getJSONObject(date - 1);

                    String time = dayData.getJSONObject("date").getString("readable");
                    JSONObject timings = dayData.getJSONObject("timings");

                    today = new Day(time, timings);

                } catch (JSONException e) {
                    e.printStackTrace();

                }

                remoteViews.setTextViewText(R.id.startFajr, today.prayerTimeForPrayer(Prayers.PRAYER_FAJR));
                remoteViews.setTextViewText(R.id.startSR, today.prayerTimeForPrayer(Prayers.PRAYER_SUNRISE));
                remoteViews.setTextViewText(R.id.startZuhr, today.prayerTimeForPrayer(Prayers.PRAYER_ZUHR));
                remoteViews.setTextViewText(R.id.startAsr, today.prayerTimeForPrayer(Prayers.PRAYER_ASR));
                remoteViews.setTextViewText(R.id.startMaghrib, today.prayerTimeForPrayer(Prayers.PRAYER_MAGHRIB));
                remoteViews.setTextViewText(R.id.startIsha, today.prayerTimeForPrayer(Prayers.PRAYER_ISHA));
            }


            Intent onClickIntent = new Intent(context, MainActivity.class);
            onClickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent onClickPendingIntent = PendingIntent.getActivity(context, 1, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widgetRoot, onClickPendingIntent);

            Intent intent = new Intent(context, Widget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(remoteViews.getLayoutId(), pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

        }
    }
}