package com.example.smartpit.schedule;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.smartpit.widget.Log;


public class SmartPitScheduleDataReceiver extends BroadcastReceiver {


    private static String TAG = SmartPitScheduleDataReceiver.class.getName();
    private static int delay = 1000 * 30;

    private static AlarmManager am;
    private static PendingIntent pending;

    private static Intent customIntent;

    public static void setCustomIntent(Intent intent) {
        customIntent = intent;
    }

    public static void setDelay(int d) {
        delay = d;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (am == null) {
            // am.cancel(pending);


            am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent i = null;
            if (customIntent != null)
                i = customIntent;
            else
                i = new Intent(context, SmartPitScheduledIntentService.class);

            pending = PendingIntent.getService(context, 0, i,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 10);


           // am.set
            am.setInexactRepeating(AlarmManager.RTC, 0, delay,
                    pending);
            Log.d(TAG, "service started!!");

        }
    }

    public static void stopService() {

        //  Log.d(TAG,"service stopped!");
        if (am != null) {
            am.cancel(pending);
            am=null;
            Log.d(TAG, "service stopped!");
        }
    }

}
