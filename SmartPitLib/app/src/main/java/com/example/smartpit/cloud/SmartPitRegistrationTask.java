package com.example.smartpit.cloud;

import android.content.Context;
import android.os.AsyncTask;

import com.example.smartpit.widget.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by piotr on 06.04.14.
 */
public class SmartPitRegistrationTask extends AsyncTask<Object, String, String> {

    private GoogleCloudMessaging gcm;
    private Context context;
    private String senderId;
    private String regId;
    private String TAG = SmartPitRegistrationTask.class.getName();

    public SmartPitRegistrationTask(Context context,String senderId) {
        this.context = context;
        this.senderId = senderId;
    }

    @Override
    protected String doInBackground(Object[] params) {

        gcm = GoogleCloudMessaging.getInstance(context);
        try {
            regId = gcm.register(senderId);

           // gcm.se

            Log.d(TAG, "successfully registered " + regId);
        } catch (IOException e) {

            Log.d(TAG, "registration failed " + regId);

            e.printStackTrace();
        }

        return null;
    }

    public void onPostExecute(String result) {
        Log.d(TAG, "registration ended ");

    }
}
