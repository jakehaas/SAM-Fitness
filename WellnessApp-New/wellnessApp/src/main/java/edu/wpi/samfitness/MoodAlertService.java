/**
 * MainFragment.java
 * Sam Fitness
 *
 * @version 1.0.0
 *
 * @author Jake Haas
 * @author Evan Safford
 * @author Nate Ford
 * @author Haley Andrews
 *
 * Copyright (c) 2014, 2015. Wellness-App-MQP. All Rights Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package edu.wpi.samfitness;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.wellnessapp.R;


public class MoodAlertService extends Service {
    public static int NOTIFY_RATE = 60000 * 60 * 4;
    private Timer timer;
    private boolean firstRun = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        timer = new Timer();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Mood Tracking Stopped", Toast.LENGTH_LONG).show();
        timer.cancel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Toast.makeText(this, "Mood Tracking Started", Toast.LENGTH_LONG).show();

        startMoodAlertService();

        return START_NOT_STICKY;
    }

    private void startMoodAlertService() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (firstRun) {
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.mood_circle)
                            .setContentTitle("Mood Service")
                            .setContentText("Don't forget to set your mood!");

                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);

                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    notificationBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    Notification notification = notificationBuilder.build();
                    notification.flags = Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(1, notification);
                } else {
                    firstRun = true;
                }
            }
        }, 0, NOTIFY_RATE);
    }
}
