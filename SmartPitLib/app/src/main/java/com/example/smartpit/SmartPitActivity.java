package com.example.smartpit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.smartpit.bitmaps.SmartPitBitmapCache;
import com.example.smartpit.bitmaps.SmartPitImageLoader;
import com.example.smartpit.cloud.SmartPitGcmIntentService;
import com.example.smartpit.facebook.SmartFacebookHelper;
import com.example.smartpit.fragment.SmartPitFragment;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.cloud.SmartPitRegistrationTask;
import com.example.smartpit.widget.SmartPitAppHelper;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SmartPitActivity extends SherlockFragmentActivity implements
        SmartPitFragmentsInterface {


    private UiLifecycleHelper uiHelper;
    private SmartFacebookHelper.OnActivityResultInterface uiHelperListener;
  //  private Session.StatusCallback sessionCallback;

    private String TAG = SmartPitActivity.class.getName();

    private ArrayList<SmartPitFragment> fragmentsList;
    private FragmentManager fm;
    private static SmartPitImageLoader mImageLoader;
    private GoogleCloudMessaging gcm;

    private View customActionbarView;
    private TextView customActionbarLabel;

    private ActionBar ab;



    public ActionBar getSmartActionBar() {
        return ab;
    }

    public static SmartPitImageLoader getImageLoader() {
        return mImageLoader;
    }


    public FragmentManager getManager() {
        return fm;
    }

    @Override
    public Activity getSmartActivity() {
        return this;
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (uiHelper != null)
            uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (uiHelper != null)

            uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (uiHelper != null)

            uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (uiHelper != null)

            uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (uiHelper != null)
            uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
                @Override
                public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                    if (uiHelperListener != null)
                        uiHelperListener.onError(pendingCall, error, data);
                }

                @Override
                public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {

                    if (uiHelperListener != null)
                        uiHelperListener.onComplete(pendingCall, data);


                }
            });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_activity);


        fm = this.getSupportFragmentManager();


        fragmentsList = new ArrayList<SmartPitFragment>();

        mImageLoader = new SmartPitImageLoader(Volley.newRequestQueue(this),
                SmartPitBitmapCache.getInstance(this));

    }


    public void initFacebook(Bundle savedInstanceState, Session.StatusCallback callback, SmartFacebookHelper.OnActivityResultInterface listener) {


        this.uiHelperListener = listener;
        uiHelper = new UiLifecycleHelper(this, callback);
        //sessionCallback = callback;

        uiHelper.onCreate(savedInstanceState);

        SmartFacebookHelper.init(this, uiHelper);

    }

    public void initActionbar(Drawable background, View customView, TextView label) {
        this.customActionbarView = customView;
        this.customActionbarLabel = label;
        ab = this.getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(customView, new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));
        ab.setBackgroundDrawable(background);

    }

    public void setFirstFragment(SmartPitFragment fragment) {

        this.setCurrentFragment(fragment, false);

        fm.beginTransaction().add(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();

    }

    // ///////////this method adds fragment to fragments list.
    // ////////// it replaces dupes to avoid fragments arguments issues
    @Override
    public void setCurrentFragment(SmartPitFragment fragment,
                                   boolean removePrevious) {

        if (removePrevious) {
            for (int i = 0; i < fragmentsList.size(); i++) {
                if (fragment.getClass() == fragmentsList.get(i).getClass()) {
                    fragmentsList.remove(i);
                    fragmentsList.add(fragment);

                    return;
                }

            }
        }
        Log.d(TAG, "new Fragment added to list");
        fragmentsList.add(fragment);
    }

    // /////return currently added fragment
    @Override
    public SmartPitFragment getCurrentFragment() {

        for (int i = 0; i < fragmentsList.size(); i++) {
            if (fragmentsList.get(i).isAdded()) {
                // Log.d(TAG, "setted currentFragment");
                return fragmentsList.get(i);
            }

        }

        return null;
    }

    //
    // ////////////////////replace current fragment with argument fragment,
    // /////////////// transition with in/out animations added to backstack
    @Override
    public void switchFragment(SmartPitFragment fragment, boolean removePrevious) {
        SmartPitFragment oldFragment = getCurrentFragment();

        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left, android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right).remove(oldFragment)
                .add(R.id.fragment_container, fragment).addToBackStack(null)
                .commitAllowingStateLoss();

        setCurrentFragment(fragment, removePrevious);
    }

    // //////////method switch title fragment, transition with in animation not
    // added to backstack
    @Override
    public void switchTitleFragment(SmartPitFragment fragment,
                                    boolean removePrevious) {
        SmartPitFragment oldFragment = getCurrentFragment();

        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left, android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right).remove(oldFragment)
                .add(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();

        setCurrentFragment(fragment, removePrevious);

    }

    @Override
    public void setActionBarLabel(String text) {

        if (customActionbarView == null)
            return;

        customActionbarLabel.setText(text);


    }


    public void initGcmService(String senderId, SmartPitGcmIntentService.OnMessageListener listener) {
        if (SmartPitAppHelper.getInstance(this).checkPlayServices(this)) {


            SmartPitGcmIntentService.setOnMessageListener(listener);
            new SmartPitRegistrationTask(this, senderId).execute();

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
                @Override
                public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                    Log.e("Activity", String.format("Error: %s", error.toString()));
                }

                @Override
                public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                    Log.i("Activity", "Success!");
                }
            });
        }
    */


    @Override
    public int getTab() {
        return -1;
    }

}
