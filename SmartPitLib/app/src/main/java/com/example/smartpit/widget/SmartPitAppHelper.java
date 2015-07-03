package com.example.smartpit.widget;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import android.util.Base64;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.smartpit.SmartPitActivity;
import com.example.smartpit.bitmaps.SmartPitImageLoader;
import com.example.smartpit.bitmaps.SmartPitImagesListener;
import com.example.smartpit.fragment.SmartPitBaseFragment;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.schedule.SmartPitScheduleDataReceiver;
import com.example.smartpit.schedule.SmartPitScheduledIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SmartPitAppHelper {

    private static String TAG = SmartPitAppHelper.class.getName();

    private static Context context;
    private static ConnectivityManager cm;
    private static DecimalFormat df;
    private static SharedPreferences pref;
    private static SmartPitAppHelper instance;

    public SmartPitAppHelper(Context con) {
        context = con;
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(custom);

        pref = PreferenceManager.getDefaultSharedPreferences(context);

    }


    public static SmartPitAppHelper getInstance(Context c) {
        if (instance == null)
            instance = new SmartPitAppHelper(c);
        return instance;
    }

    public boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    public boolean showViewWithAnimation(View v) {
        if (v.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "view is already visible");
            return false;
        }

        Log.d(TAG, "show view with animation");
        v.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300 * 1);
        //animation.setFillAfter(true);
        v.startAnimation(animation);
        return true;
    }

    public Typeface loadTypeFaceFromAssets(String filename) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), filename);
        return face;
    }

    public boolean toogleViewWithAnimation(View v) {
        if (v.getVisibility() == View.VISIBLE) {
            hideViewWithAnimation(v);
            return false;
        } else {
            showViewWithAnimation(v);
            return true;
        }
    }

    public boolean hideViewWithAnimation(final View v) {


        if (v.getVisibility() == View.GONE) {
            Log.d(TAG, "view is already gone");
            return false;
        }
        Log.d(TAG, "hide view with animation");
        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(300 * 1);
        animation.setFillAfter(true);
        v.startAnimation(animation);

        return true;

    }

    public NumberFormat getDecimalFormat() {
        return df;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = 2 * totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        Log.d(TAG, "setting list view height " + params.height);
        listView.setLayoutParams(params);
    }


    public void setImage(SmartImageView imageView,
                         final String url, final int width, final int height) {


        final SmartPitImagesListener li = new SmartPitImagesListener(context, url,
                imageView);

        Log.d(SmartPitImageLoader.class.getName(), "app helper get");
        SmartPitActivity.getImageLoader()
                .get(url, li, width, height);

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

    public void resumeFocus(final View view,
                            final SmartPitFragmentsInterface listener) {

        if (view == null)
            return;


        Log.d(TAG, "resume focus " + view.toString());

        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "focus changed! " + Boolean.toString(hasFocus));
//                if(!hasFocus)
                //                  view.requestFocus();
                // view.re
            }
        });


        view.setFocusableInTouchMode(true);

        view.requestFocus();
        view.requestFocusFromTouch();
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

                if (event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d(TAG, "back pressed " + listener.toString());
                    if (listener == null)
                        return true;

                    if (!(listener.getCurrentFragment() instanceof SmartPitBaseFragment))
                        if (listener.getCurrentFragment().onBackPressed())
                            return true;

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
            // Log.d(TAG, "clearing view " + view.toString());
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


    public String deserialize(String tokenString) {
        String[] pieces = splitTokenString(tokenString);
        String jwtPayloadSegment = pieces[1];
        JsonParser parser = new JsonParser();
        JsonElement payload = parser.parse(StringUtils.newStringUtf8(Base64.decode(jwtPayloadSegment, Base64.DEFAULT)));
        return payload.toString();
    }

    private String[] splitTokenString(String tokenString) {
        String[] pieces = tokenString.split(Pattern.quote("."));
        if (pieces.length != 3) {
            throw new IllegalStateException("Expected JWT to have 3 segments separated by '"
                    + "." + "', but it has " + pieces.length + " segments");
        }
        return pieces;
    }

    public String getCurrentIpAddress() {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        return ip;
    }

    public static void printFacebookHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                Toast.makeText(context.getApplicationContext(), sign, Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context.getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

        } catch (NoSuchAlgorithmException e) {

            Toast.makeText(context.getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

        }
    }


}
