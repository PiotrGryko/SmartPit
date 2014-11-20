package com.example.smartpit.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.animation.Animation;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.interfaces.SmartPitChildFragmentInterface;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.Log;
import com.example.smartpit.widget.SmartPitAppHelper;

import java.util.Random;

public abstract class SmartPitFragment extends SherlockFragment implements
        SmartPitChildFragmentInterface {

    private String TAG = SmartPitFragment.class.getName();

    private SmartPitFragmentsInterface listener;

    private String fragmentTAG;

    public SmartPitFragment()
    {
        Log.d(TAG,"constructor called");
        fragmentTAG =this.toString();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        if (this.getParentFragment() != null && (this.getParentFragment() instanceof SmartPitFragmentsInterface))
            listener = (SmartPitFragmentsInterface) this.getParentFragment();


        else if (this.getSherlockActivity() instanceof SmartPitFragmentsInterface)
            listener = (SmartPitFragmentsInterface) this.getSherlockActivity();

        //   listener.setActionBarLabel(getLabel());


    }

    public boolean onBackPressed() {

        Log.d(TAG, "Smart pit fragment on back pressed");
        return false;
    }

    public String getFragmentTAG() {
        return fragmentTAG;
    }

    public void onResume() {
        super.onResume();

        //   if(listener!=null)
        //      listener.setActionBarLabel(getLabel());


        /*
        If SmartPitPagerFragment and nested fragment are used, resume focus only for currently visible tab

         */


        resumeFocus();
    }

    public void resumeFocus() {
        Log.d(TAG, "resume focus fragment");

        SmartPitAppHelper.getInstance(this.getSherlockActivity()).resumeFocus(this.getView(),
                this.getFragmentsListener());
    }

    public SmartPitFragmentsInterface getFragmentsListener() {
        return listener;
    }

    @Override
    public void stripView() {
        Log.d(TAG, "striping view ...");
        if (this.getView() != null) {
            SmartPitAppHelper.getInstance(this.getSherlockActivity()).stripViewGroup(this.getView(), false);

            System.gc();

        }

    }

    public void onDestroyView() {
        stripView();
        super.onDestroyView();
    }

    public abstract String getLabel();

    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "configuration changed!");
    }
/*
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (FragmentUtils.sDisableFragmentAnimations) {
            Animation a = new Animation() {};
            a.setDuration(0);
            return a;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }
*/
}
