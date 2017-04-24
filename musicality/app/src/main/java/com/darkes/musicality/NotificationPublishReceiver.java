package com.darkes.musicality;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/*
 * Copyright 2016 chRyNaN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by chRyNaN on 1/27/2016. This class is a BroadcastReceiver that is triggered from an AlarmManager after a certain amount
 * of time if the user has not opened this app. It alerts the user to tune their guitar. This is useful to keep users engaged in
 * the application.
 */
public class NotificationPublishReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_TITLE = "Stay in tune!";
    public static final String NOTIFICATION_TEXT = "Make sure your guitar is in tune.";
    public static final int REQUEST_CODE = 0;
    public static final int ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(NOTIFICATION_TITLE);
        builder.setContentText(NOTIFICATION_TEXT);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        Intent i = new Intent(context, TunerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, i, 0);
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(ID, builder.build());
    }

}