package com.ubisoc.ubisoc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by hamzah on 08/01/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent toLaunch = new Intent(context, MainActivity.class);
        toLaunch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, Keys.PRAYER_ALERT_REQUEST_CODE, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, "prayer_time")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentTitle("Prayer Time")
                .setContentText("It is time for " + intent.getStringExtra(Keys.EXTRA_PRAYER_NAME) + " at " + intent.getStringExtra(Keys.EXTRA_PRAYER_TIME))
                .setAutoCancel(true).build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //We use the same ID so notification will be overriden when it's time for next prayer
        manager.notify(Keys.PRAYER_ALERT_REQUEST_CODE, notification);
    }

}