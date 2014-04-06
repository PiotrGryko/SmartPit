package com.example.smartpit.schedule;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class SmartPitScheduledIntentService extends IntentService {

    private String TAG = SmartPitScheduledIntentService.class.getName();




    public static interface ScheduleTaskListener {
        public void onTask();
    }

    private static ScheduleTaskListener listener;

    public static void setTaskListener(ScheduleTaskListener l) {
        listener = l;
    }


    public SmartPitScheduledIntentService() {
        super(SmartPitScheduledIntentService.class.getName());
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        if (listener != null)
            listener.onTask();



    }
}
