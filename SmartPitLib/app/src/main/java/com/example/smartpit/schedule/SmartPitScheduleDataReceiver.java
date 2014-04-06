package com.example.smartpit.schedule;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SmartPitScheduleDataReceiver extends BroadcastReceiver {

    private String TAG = SmartPitScheduleDataReceiver.class.getName();
    private static int delay = 1000 * 30;
    private Calendar cal;
    private static AlarmManager am;
    private static PendingIntent pending;

    public static void setDelay(int d) {
        delay = d;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (am != null)
            am.cancel(pending);

        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, SmartPitScheduledIntentService.class);

        pending = PendingIntent.getService(context, 0, i,
                PendingIntent.FLAG_CANCEL_CURRENT);

        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);




        am.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), delay,
                pending);

    }

    public static void stopService() {
        if (am != null)
            am.cancel(pending);
    }

}
