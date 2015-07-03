package pl.gryko.smartpitlib.cloud;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import pl.gryko.smartpitlib.widget.Log;

/**
 * Created by piotr on 06.04.14.
 */
public class SmartPitRegistrationTask extends AsyncTask<Object, String, String> {

    public interface OnRegistrationListener {
        public void onRegistered(String regID);

        public void onRegistrationFailed(String regID);

    }

    private GoogleCloudMessaging gcm;
    private Context context;
    private String senderId;
    private String regId;
    private String TAG = SmartPitRegistrationTask.class.getName();
    private OnRegistrationListener listener;

    public SmartPitRegistrationTask(Context context, String senderId, OnRegistrationListener listener) {
        this.context = context;
        this.senderId = senderId;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Object[] params) {

        gcm = GoogleCloudMessaging.getInstance(context);
        try {
            regId = gcm.register(senderId);

            // gcm.se
            listener.onRegistered(regId);
            Log.d(TAG, "successfully registered " + regId);
        } catch (IOException e) {

            Log.d(TAG, "registration failed "+e.toString()+" "  + regId);
            listener.onRegistrationFailed(regId);
            e.printStackTrace();
        }

        return null;
    }

    public void onPostExecute(String result) {
        Log.d(TAG, "registration ended ");

    }
}
