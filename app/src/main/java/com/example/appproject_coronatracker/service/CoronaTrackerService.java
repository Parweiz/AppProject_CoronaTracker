package com.example.appproject_coronatracker.service;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.models.Country;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;

public class CoronaTrackerService extends Service {

    private final IBinder binder = new LocalBinder();
    public static final String BROADCAST_BACKGROUND_SERIVE_ARRAYLIST = "com.example.appproject_coronatracker.BROADCAST_BACKGROUND_SERVICE_ARRAYLIST";
    public static final String ARRAY_LIST = "arraylist";
    public static final String TAG = "wordlearnerservice";
    public static final String CHANNEL_ID = "approject_coronatracker_channelid";
    public static final int NOTIFICATION_ID = 1;
    private CharSequence CHANNEL_NAME = "WordLearnerService Channel";

    private ArrayList<Country> mCountryArrayList = new ArrayList<>();
    private boolean runAsForegroundService = true;
    private boolean started = false;
    Context mContext;
    private ExecutorService execService;
    private RequestQueue queue;
    private Random random = new Random();


    public class LocalBinder extends Binder {
        public CoronaTrackerService getService() {
            return CoronaTrackerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
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

    public void volleyRequest(String url) {
        if (queue == null) {
            queue = Volley.newRequestQueue(mContext);
        }

        /*final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: " + response);
                        parseJson(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG, "That did not work!", error);
            }
        });*/

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MAIN", "That did not work!", error);
            }
        });

        queue.add(stringRequest);
    }

    private void parseJson(String json) {
        Gson gson = new GsonBuilder().create();

        Country country = gson.fromJson(json, Country.class);
        Log.d(TAG, "parseJson: " + country);
        if (country != null) {
            mCountryArrayList.add(country);

            new CreateCountriesAsyncTask().execute(country);
        }

    }

    public class CreateCountriesAsyncTask extends AsyncTask<Country, Void, Void> {

        @Override
        protected Void doInBackground(Country... countries) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            localBroadcastSender(mCountryArrayList);

        }
    }

    public void localBroadcastSender(ArrayList<Country> countries) {

        Log.d(TAG, "Using local broadcast to send arraylist ");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        intent.setAction(BROADCAST_BACKGROUND_SERIVE_ARRAYLIST);
        bundle.putSerializable(ARRAY_LIST, countries);
        intent.putExtra("Bundle", bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }
}

