package pl.gryko.smartpitlib.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;



import java.util.Random;

import pl.gryko.smartpitlib.interfaces.SmartPitChildFragmentInterface;
import pl.gryko.smartpitlib.interfaces.SmartPitFragmentsInterface;
import pl.gryko.smartpitlib.widget.Log;
import pl.gryko.smartpitlib.widget.SmartPitAppHelper;

public abstract class SmartPitFragment extends Fragment implements
        SmartPitChildFragmentInterface {

    private String TAG = SmartPitFragment.class.getName();

    private SmartPitFragmentsInterface listener;

    private String fragmentTAG;

    public SmartPitFragment() {
        Log.d(TAG, "constructor called");
        fragmentTAG = this.toString();
    }

    public void setActionbarLabel() {
        if (listener != null)
            listener.setActionBarLabel(getLabel());
    }




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (this.getParentFragment() != null && (this.getParentFragment() instanceof SmartPitFragmentsInterface))
            listener = (SmartPitFragmentsInterface) this.getParentFragment();


        else if (this.getActivity() instanceof SmartPitFragmentsInterface)
            listener = (SmartPitFragmentsInterface) this.getActivity();

        setActionbarLabel();

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

        SmartPitAppHelper.getInstance(this.getActivity()).resumeFocus(this.getView(),
                this.getFragmentsListener());
    }

    public SmartPitFragmentsInterface getFragmentsListener() {
        return listener;
    }

    @Override
    public void stripView() {
        Log.d(TAG, "striping view ...");
        if (this.getView() != null) {
            SmartPitAppHelper.getInstance(this.getActivity()).stripViewGroup(this.getView(), false);

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
