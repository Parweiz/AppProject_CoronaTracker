package com.example.appproject_coronatracker.service;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.appproject_coronatracker.R;
import com.example.appproject_coronatracker.activities.LoginActivity;
import com.example.appproject_coronatracker.models.Country;
import com.example.appproject_coronatracker.models.CountryInfo;
import com.example.appproject_coronatracker.models.CountryParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoronaTrackerService extends Service {

    private final IBinder binder = new LocalBinder();
    public static final String BROADCAST_BACKGROUND_SERIVE_ARRAYLIST = "com.example.appproject_coronatracker.BROADCAST_BACKGROUND_SERVICE_ARRAYLIST";
    public static final String ARRAY_LIST = "arraylist";
    public static final String TAG = "wordlearnerservice";
    public static final String CHANNEL_ID = "approject_coronatracker_channelid";
    public static final int NOTIFICATION_ID = 1;
    private CharSequence CHANNEL_NAME = "WordLearnerService Channel";

    private ArrayList<CountryParcelable> mCountryArrayList = new ArrayList<>();
    private List<String> mStringArrayList = new ArrayList<>();
    private boolean runAsForegroundService = true;
    private boolean started = false;
    Context mContext;
    private ExecutorService execService;
    private RequestQueue queue;
    private Random random = new Random();

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference notificationsRef = firestore.document("notifications/z9cotEfKEd6epuh71k2n");



    public class LocalBinder extends Binder {
        public CoronaTrackerService getService() {
            return CoronaTrackerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

       // addDataForNotifcation();
        getDatafromFirestore();
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

                Intent notificationIntent = new Intent(this, LoginActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                        .setContentTitle(getString(R.string.notificationtitle))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getString(R.string.advice1)))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                startForeground(NOTIFICATION_ID, notification);


            }
            // Sleep time for 2 mins
            recursiveSleepWork(10000L);

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

    public void addCountry(String word) {

        String server_url = "https://corona.lmao.ninja/v2/countries/";
        volleyRequest(server_url + word);
    }

    public void volleyRequest(String url) {
        if (queue == null) {
            queue = Volley.newRequestQueue(mContext);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That did not work!", error);
            }
        });

        queue.add(stringRequest);
    }

    private void parseJson(String json) {
        Gson gson = new GsonBuilder().create();

        CountryParcelable country = gson.fromJson(json, CountryParcelable.class);
        Log.d(TAG, "parseJson: " + country);
        if (country != null) {
            mCountryArrayList.add(country);

            new CreateCountriesAsyncTask().execute(country);
        }

    }

    public class CreateCountriesAsyncTask extends AsyncTask<CountryParcelable, Void, Void> {

        @Override
        protected Void doInBackground(CountryParcelable... countryParcelables) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            localBroadcastSender(mCountryArrayList);
        }
    }


    public void localBroadcastSender(ArrayList<CountryParcelable> countries) {

        Log.d(TAG, "Using local broadcast to send arraylist ");

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        intent.setAction(BROADCAST_BACKGROUND_SERIVE_ARRAYLIST);
        bundle.putSerializable(ARRAY_LIST, countries);
        intent.putExtra("Bundle", bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


    }

    private void recursiveSleepWork(final Long sleepTime) {
        if (execService == null) {
            execService = Executors.newSingleThreadExecutor();
        }

        execService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Task started");
                    if (mStringArrayList.size() > 0) {
                       getRandomAdvice(mStringArrayList);
                    }
                    Log.d(TAG, "Task completed - Will now sleep for 2 mins");
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (started) {
                    recursiveSleepWork(sleepTime);
                }
            }
        });
    }

    private void getDatafromFirestore() {

        notificationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                       String advice1 = document.getString("advice1");
                        String advice2 = document.getString("advice2");
                        String advice3 = document.getString("advice3");
                        String advice4 = document.getString("advice4");
                        String advice5 = document.getString("advice5");
                        String advices = advice1 + ";" + advice2 + ";" + advice3 + ";" + advice4 + ";" + advice5;

                        mStringArrayList = new ArrayList<>(Arrays.asList(advices.split(";")));

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    private void addDataForNotifcation() {

        Map<String, Object> data = new HashMap<>();
        data.put("advice1", getString(R.string.advice1));
        data.put("advice2", getString(R.string.advice2) );
        data.put("advice3", getString(R.string.advice3));
        data.put("advice4", getString(R.string.advice4));
        data.put("advice5", getString(R.string.advice5));

        firestore.collection("notifications")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void getRandomAdvice(List<String> list) {

        int index = random.nextInt(list.size());
        Log.d(TAG, "index: " + index + ", advice: " + list.get(index));

        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_announcement_black_24dp)
                .setContentTitle(getString(R.string.notificationtitle))
                //.setContentText(list.get(index))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(list.get(index)))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        startForeground(NOTIFICATION_ID, notification);

    }

}

