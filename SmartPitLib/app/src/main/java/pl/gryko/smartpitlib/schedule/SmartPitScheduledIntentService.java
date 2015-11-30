package pl.gryko.smartpitlib.schedule;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


/**
 * Implementation of intent service that can perform custom operations inside ScheduleTaskListener.
 * IntentService can be easy started by invoking SmartPitAppHelper.initScheduledService with passed ScheduledTaskListener.
 * In example can be used for easy setup feching new API orders each 60 seconds.
 */

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

    public static void initScheduledService(Context context, int delay, SmartPitScheduledIntentService.ScheduleTaskListener listener) {

        SmartPitScheduledIntentService.setTaskListener(listener);
        SmartPitScheduleDataReceiver.setDelay(delay);
        context.sendBroadcast(new Intent(context, SmartPitScheduleDataReceiver.class));

    }

    public static void stopScheduledService() {
        SmartPitScheduleDataReceiver.stopService();
    }


}
