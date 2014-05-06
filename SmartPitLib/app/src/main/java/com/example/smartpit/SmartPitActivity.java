package com.example.smartpit;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.smartpit.bitmaps.SmartPitBitmapCache;
import com.example.smartpit.cloud.SmartPitGcmIntentService;
import com.example.smartpit.fragment.SmartPitFragment;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.cloud.SmartPitRegistrationTask;
import com.example.smartpit.widget.SmartPitAppHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SmartPitActivity extends SherlockFragmentActivity implements
        SmartPitFragmentsInterface {

    private String TAG = SmartPitActivity.class.getName();

    private ArrayList<SmartPitFragment> fragmentsList;
    private FragmentManager fm;
    private static ImageLoader mImageLoader;
    private GoogleCloudMessaging gcm;

    private View customActionbarView;
    private TextView customActionbarLabel;

    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_activity);


        fm = this.getSupportFragmentManager();


        fragmentsList = new ArrayList<SmartPitFragment>();

        mImageLoader = new ImageLoader(Volley.newRequestQueue(this),
                SmartPitBitmapCache.getInstance(this));

    }

    public void initActionbar(Drawable background, View customView, TextView label) {
        this.customActionbarView=customView;
        this.customActionbarLabel=label;
        ab = this.getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(customView, new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));
        ab.setBackgroundDrawable(background);

    }

    public ActionBar getSmartActionBar() {
        return ab;
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void setFirstFragment(SmartPitFragment fragment) {

        this.setCurrentFragment(fragment, false);

        fm.beginTransaction().add(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();

    }

    public FragmentManager getManager() {
        return fm;
    }

    @Override
    public Activity getSmartActivity() {
        return this;
    }

    // ///////////this method add fragment to fragments list.
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

        if(customActionbarView==null)
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


    @Override
    public int getTab() {
        return -1;
    }

}
