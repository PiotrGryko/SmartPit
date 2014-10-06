package com.example.smartpit.cloud;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.smartpit.R;
import com.example.smartpit.widget.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by piotr on 06.04.14.
 *
 * This class manage action invoked after receiving GCM message
 *
 */
public class SmartPitGcmIntentService extends IntentService {




    /*
    OnMessageListener allaws to set custom action invoked when message arrives.
     */
    public static interface OnMessageListener
    {
        public void onReceiveGcm(Intent intent);
    }



    private String TAG = SmartPitGcmIntentService.class.getName();
    private static OnMessageListener listener;

    public static void setOnMessageListener(OnMessageListener l)
    {
        listener = l;
    }
    public SmartPitGcmIntentService() {
        super(SmartPitGcmIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.d(TAG, "on handle intent!");



        if(listener!=null)
            listener.onReceiveGcm(intent);



        SmartPitGcmReceiver.completeWakefulIntent(intent);


    }

}
