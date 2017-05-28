package com.darkes.musicality.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;

import com.darkes.musicality.R;
import com.darkes.musicality.bpm.BpmActivity;
import com.darkes.musicality.metronome.MetronomeActivity;
import com.darkes.musicality.tuner.GuitarTunerActivity;

import java.util.Random;

/**
 * This class is a BroadcastReceiver that is triggered from an AlarmManager after a certain amount
 * of time if the user has not opened this app. It alerts the user to tune their guitar. This is useful to keep users engaged in
 * the application.
 */
public class NotificationPublishReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_TITLE1 = "Stay in tune!";
    public static final String NOTIFICATION_TEXT1 = "Make sure your guitar is in tune.";
    public static final String NOTIFICATION_TITLE2 = "Keep in tempo!";
    public static final String NOTIFICATION_TEXT2 = "Make sure you're in tempo when you play.";
    public static final String NOTIFICATION_TITLE3 = "What bpm is that in?";
    public static final String NOTIFICATION_TEXT3 = "Find out its bpm.";
    String [] notificationTitle = {NOTIFICATION_TITLE1, NOTIFICATION_TITLE2, NOTIFICATION_TITLE3};
    String [] notificationText = {NOTIFICATION_TEXT1, NOTIFICATION_TEXT2, NOTIFICATION_TEXT3};
    int [] drawable = {R.drawable.tuning_fork_white, R.drawable.metronome_white, R.drawable.bpm_white};

    public static final int REQUEST_CODE = 0;
    public static final int ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        int random = new Random().nextInt(3);
        builder.setContentTitle(notificationTitle[random]);
        builder.setContentText(notificationText[random]);
        builder.setSmallIcon(drawable[random]);
        builder.setColor(Color.parseColor("#0096a9"));
        builder.setAutoCancel(true);
        Intent i;
        switch (random){
            case 0:
                i = new Intent(context, GuitarTunerActivity.class);
                break;
            case 1:
                i = new Intent(context, MetronomeActivity.class);
                break;
            default:
                i = new Intent(context, BpmActivity.class);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, i, 0);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(ID, builder.build());
    }

}