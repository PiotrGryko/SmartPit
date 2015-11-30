package pl.gryko.smartpitlib.widget;


import java.math.BigDecimal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;

import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import android.util.Base64;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Collection of usefull static methods.
 */

public class SmartPitAppHelper {

    private static String TAG = SmartPitAppHelper.class.getName();


    /**
     * Checks if given String maches Email pattern
     * @param email String to check
     * @return true if argument is email String
     */
    public static boolean validateEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    /**
     * Shows view with fade animation
     * @param v View to show
     * @param duration Duration of fade animation
     * @return false if view was already visible, true otherwise
     */
    public static boolean showViewWithAnimation(View v, long duration) {
        if (v.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "view is already visible");
            return false;
        }

        Log.d(TAG, "show view with animation");
        v.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        //animation.setFillAfter(true);
        v.startAnimation(animation);
        return true;
    }

    /**
     * Hides view with fade animation
     * @param v View to show
     * @param duration Duration of fade animation
     * @return false if view was already gone, true otherwise
     */
    public static boolean hideViewWithAnimation(final View v, long duration) {


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
        animation.setDuration(duration);
        animation.setFillAfter(true);
        v.startAnimation(animation);

        return true;

    }

    /**
     * Toggle view with fade animation
     * @param v View to show
     * @param duration Duration of fade animation
     * @return false if view has been hidden, true otherwise
     */
    public static boolean toogleViewWithAnimation(View v, long duration) {
        if (v.getVisibility() == View.VISIBLE) {
            hideViewWithAnimation(v, duration);
            return false;
        } else {
            showViewWithAnimation(v, duration);
            return true;
        }
    }

    /**
     * Loads font from assets.
     * @param context Context
     * @param filename String name of font file ie. 'font.otf'
     * @return Typeface loaded from assets
     */
    public static Typeface loadTypeFaceFromAssets(Context context, String filename) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), filename);
        return face;
    }


    /**
     * Makes DecimalFormat with given separator and decimal places
     * @param separator char separator for decimal places
     * @param digits number of decimal places
     * @return NumberFormat
     */
    public static NumberFormat getDecimalFormat(char separator, int digits) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(digits);
        df.setMinimumFractionDigits(digits);

        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(separator);
        df.setDecimalFormatSymbols(custom);
        return df;
    }

    /**
     * Methods sets ListView height base on its childrens. Can be usefull in nested scrolling cases
     * @param listView ListView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
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


    /**
     * Checks internet connection
     * @param context Context
     * @return true if internet is available, false otherwise
     */
    public static boolean isConnected(Context context) {
        boolean isConnected = false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if ((wifi != null && wifi.isConnected())
                || (mobile != null && mobile.isConnected()))
            isConnected = true;
        else
            isConnected = false;

        return isConnected;
    }

    /**
     * shourtcut for shared preferecnce
     * @param context Context
     * @return SharedPreferences from context
     */
    public static SharedPreferences getPreferences(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref;
    }

    /**
     * Connverts double latitude or longitude to degree-minute-second
     * @param coordinate Coordinate to convert
     * @param df DecimalFormat for output formatting
     * @return String that respresents coordinate as DMS
     */
    public static String convertCoordinateToDMS(double coordinate, DecimalFormat df) {
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


    /**
     * method chechs if current device is tablet
     * @param context Context
     * @return true if tablet device, false otherwise
     */
    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }


    /**
     * Returns screen width
     * @param context Activity
     * @return int screen width
     */
    public static int getScreenWidth(Activity context) {
        WindowManager w = context.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
// since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
            } catch (Exception ignored) {
            }
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
            } catch (Exception ignored) {
            }
        return widthPixels;
    }

    /**
     * Returns screen height
     * @param context ACtivity
     * @return int screen height
     */
    public static int getScreenHeight(Activity context) {
        WindowManager w = context.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
// since SDK_INT = 1;
        int heightPixels = metrics.heightPixels;
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }


        return heightPixels;
    }


    /**
     * strips view group from drawables to recover memory
     * @param view View to strip
     * @param recycle true if bitmaps should be recycled, false otherwise
     */
    public static void stripView(View view, boolean recycle) {

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

    /**
     * Strips whole ViewGroup from drawables
     * @param v ViewGroup to strip
     * @param recycle true if bitmaps should be recycles, false otherwise
     */
    public static void stripViewGroup(View v, boolean recycle) {

        if (v != null) {
            if (v instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                    stripViewGroup(((ViewGroup) v).getChildAt(i), recycle);
                }

            } else
                stripView(v, recycle);
        }
    }


    /**
     * Check if current device has google play service available
     * @param activity Activity
     * @return true if google play services are available, false otherwise
     */
    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        9000).show();
            } else {
                pl.gryko.smartpitlib.widget.Log.i(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }


    /**
     * Methods deserializes json web token
     * @param tokenString String token to decode
     * @return Json  String builded from token
     */
    public static String deserializeJsonWebToken(String tokenString) {
        String[] pieces = splitTokenString(tokenString);
        String jwtPayloadSegment = pieces[1];
        JsonParser parser = new JsonParser();
        JsonElement payload = parser.parse(StringUtils.newStringUtf8(Base64.decode(jwtPayloadSegment, Base64.DEFAULT)));
        return payload.toString();
    }

    private static String[] splitTokenString(String tokenString) {
        String[] pieces = tokenString.split(Pattern.quote("."));
        if (pieces.length != 3) {
            throw new IllegalStateException("Expected JWT to have 3 segments separated by '"
                    + "." + "', but it has " + pieces.length + " segments");
        }
        return pieces;
    }

    /**
     * Returns device current ip address
     * @param context Context
     * @return String ip address
     */
    public static String getCurrentIpAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        return ip;
    }

    /**
     * Method prints facebook hash
     * @param context Context
     */
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
