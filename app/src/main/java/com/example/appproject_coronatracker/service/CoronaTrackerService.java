package com.example.appproject_coronatracker.service;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.appproject_coronatracker.R;

public class CoronaTrackerService extends Service {

    private final IBinder binder = new LocalBinder();
    public static final int NOTIFICATION_ID = 1;
    private CharSequence CHANNEL_NAME = "WordLearnerService Channel";
    public static final String TAG = "wordlearnerservice";
    public static final String CHANNEL_ID = "approject_coronatracker_channelid";
    private boolean runAsForegroundService = true;
    private boolean started = false;


    public class LocalBinder extends Binder {
        public CoronaTrackerService getService() {
            return CoronaTrackerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!started) {
            Log.d(TAG, "Background service started ");
            started = true;

            if (runAsForegroundService) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel serviceChannel = new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH
                    );

                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(serviceChannel);
                }


                Intent notificationIntent = new Intent(this, ListActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                        .setContentTitle(getString(R.string.notificationtitle))
                        .setContentText(getString(R.string.notificationtext))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                startForeground(NOTIFICATION_ID, notification);

            }

        } else {
            Log.d(TAG, "Background service already started!");
        }


        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        started = false;
        Log.d(TAG, "Background service destroyed");
        super.onDestroy();
    }
}
