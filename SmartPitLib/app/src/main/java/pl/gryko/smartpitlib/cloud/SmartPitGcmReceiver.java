package pl.gryko.smartpitlib.cloud;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;



/**
 * Created by piotr on 06.04.14.
 *
 * Receiver registered for reveing gcm messages. Start SmartPitIntentService each time new message arrives.
 *
 */
public class SmartPitGcmReceiver extends WakefulBroadcastReceiver{

    private String TAG = SmartPitGcmReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {



        ComponentName comp = new ComponentName(context.getPackageName(),
                SmartPitGcmIntentService.class.getName());


         startWakefulService(context, (intent.setComponent(comp)));
         setResultCode(Activity.RESULT_OK);

    }
}
