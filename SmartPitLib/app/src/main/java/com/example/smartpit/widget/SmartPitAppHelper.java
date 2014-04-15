package com.example.smartpit.widget;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.smartpit.SmartPitActivity;
import com.example.smartpit.bitmaps.SmartPitImagesListener;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.schedule.SmartPitScheduleDataReceiver;
import com.example.smartpit.schedule.SmartPitScheduledIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SmartPitAppHelper {

    private static String TAG = SmartPitAppHelper.class.getName();

    private static Context context;
    private static ConnectivityManager cm;
    private static DecimalFormat df;
    private static SharedPreferences pref;
    private static SmartPitAppHelper instance;

    public  SmartPitAppHelper(Context con) {
        context = con;
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);


        DecimalFormatSymbols custom=new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
     //   custom.setg
        df.setDecimalFormatSymbols(custom);
        df.setGroupingUsed(false);
        pref = PreferenceManager.getDefaultSharedPreferences(context);

    }


    public static SmartPitAppHelper getInstance(Context c) {
        if (instance == null)
            instance = new SmartPitAppHelper(c);
        return instance;
    }

    public NumberFormat getDecimalFormat()
    {
        return df;
    }


    public void setImage(SmartImageView imageView,
                         String url, int width, int height) {



        SmartPitImagesListener li = new SmartPitImagesListener(context, url,
                imageView);
        imageView.setImageBitmap(SmartPitActivity.getImageLoader()
                .get(url, li, width, height).getBitmap());

    }

    public boolean isConnected() {
        boolean isConnected = false;

        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if ((wifi != null && wifi.isConnected())
                || (mobile != null && mobile.isConnected()))
            isConnected = true;
        else
            isConnected = false;

        return isConnected;
    }

    public SharedPreferences getPreferences() {
        return pref;
    }

    public String convertDecimalToDMS(double coordinate) {
        String result;

        BigDecimal bd = new BigDecimal(coordinate);
        BigDecimal fracDeg = bd.subtract(new BigDecimal(bd.toBigInteger()));

        double latMinutes = fracDeg.doubleValue() * 60;

        bd = new BigDecimal(latMinutes);
        BigDecimal fracMin = bd.subtract(new BigDecimal(bd.toBigInteger()));

        double latSeconds = fracMin.doubleValue() * 60;

        result = df.format(Math.floor(coordinate)) + Html.fromHtml("&deg")
                + df.format(Math.floor(latMinutes)) + "'"
                + df.format(Math.floor(latSeconds));

        return result;

    }

    public void saveDataToCache(final String data, final String filename) {

        new Thread() {
            public void run() {

                File f = new File(
                        context.getApplicationContext().getCacheDir(), filename);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(f);
                    fos.write(data.getBytes());
                    fos.flush();
                    fos.close();

                } catch (Throwable t) {
                    // TODO Auto-generated catch block
                    Log.d(TAG,
                            "error while saving data to cache " + t.toString());

                }

                Log.d(TAG, "data saved to cache!");

            }
        }.start();

    }

    public String loadDataFromCache(String filename) {
        File f = new File(context.getApplicationContext().getCacheDir(),
                filename);
        if (!f.exists()) {
            Log.d(TAG, "can`t load data from cache, file doesn`t exits");

            return "";
        }

        StringBuffer data = new StringBuffer();
        String line;
        try {
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);

            while ((line = dis.readLine()) != null) {
                data.append(line);
            }
            dis.close();
            fis.close();

        } catch (Throwable t) {
            // TODO Auto-generated catch block
            Log.d(TAG, "can`t load data from cache, error while reading data");
            return "";
        }
        Log.d(TAG, "data loaded from cache");

        return data.toString();

    }

    public boolean isTablet() {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public void resumeFocus(View view,
                            final SmartPitFragmentsInterface listener) {

        Log.d(TAG, "resume focus " + view.toString());

        view.setFocusableInTouchMode(true);

        view.requestFocus();
        view.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_BACK)
                    return false;

                Log.d("FragmentBase", "back pressed");
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    listener.getManager().popBackStack();
                    if (listener.getManager().getBackStackEntryCount() == 0)
                        listener.getSmartActivity().onBackPressed();
                }
                return true;
            }
        });
    }

    public int getScreenWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }


    public void stripView(View view, boolean recycle) {

        if (view != null) {

            try {
                if (view instanceof ImageView) {
                    if (((ImageView) view).getDrawable() instanceof BitmapDrawable
                            && recycle) {
                        ((BitmapDrawable) ((ImageView) view).getDrawable())
                                .getBitmap().recycle();
                    }
                    if (((ImageView) view).getDrawable() != null)
                        ((ImageView) view).getDrawable().setCallback(null);
                    ((ImageView) view).setImageDrawable(null);
                }
                view.getResources().flushLayoutCache();
                view.destroyDrawingCache();
            } catch (Throwable t) {
                Log.d(TAG, "strip view error catched");
            }
            Log.d(TAG, "clearing view " + view.toString());
            view = null;
        }
    }

    public void stripViewGroup(View v, boolean recycle) {

        if (v != null) {
            if (v instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                    stripViewGroup(((ViewGroup) v).getChildAt(i), recycle);
                }

            } else
                stripView(v, recycle);
        }
    }


    public void initScheduledService(int delay, SmartPitScheduledIntentService.ScheduleTaskListener listener) {

        SmartPitScheduledIntentService.setTaskListener(listener);
        SmartPitScheduleDataReceiver.setDelay(delay);
        context.sendBroadcast(new Intent(context, SmartPitScheduleDataReceiver.class));

    }

    public void stopScheduledService() {
        SmartPitScheduleDataReceiver.stopService();
    }

    public boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        9000).show();
            } else {
                Log.i(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }


}
