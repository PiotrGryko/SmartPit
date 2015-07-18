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

/**
 * class that adapts android.Fragment for SmartPit fragments managment. Each frament should extend this class.
 * Use getFragmentsListener().switchFragment(new SmartPitFragent(),true) for swich to next page.
 */

public abstract class SmartPitFragment extends Fragment implements
        SmartPitChildFragmentInterface {

    private String TAG = SmartPitFragment.class.getName();

    private SmartPitFragmentsInterface listener;

    private String fragmentTAG;

    public SmartPitFragment() {
        Log.d(TAG, "constructor called");
        fragmentTAG = this.toString();
    }

    /**
    invokes setActionbarLabel on listener (SmartPitActivity or SmartPitBaseFragment)
     */
    public void setActionbarLabel() {
        if (listener != null)
            listener.setActionBarLabel(getLabel());
    }


    /**
     * custom onCreate for basic initialization. Inits SmartPitFragmentsInterface and invokes setActionbarLabel.
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (this.getParentFragment() != null && (this.getParentFragment() instanceof SmartPitFragmentsInterface))
            listener = (SmartPitFragmentsInterface) this.getParentFragment();


        else if (this.getActivity() instanceof SmartPitFragmentsInterface)
            listener = (SmartPitFragmentsInterface) this.getActivity();

        setActionbarLabel();

        //   listener.setActionBarLabel(getLabel());


    }

    /**
     * Custom onBackPressed. Can be overriden to implement some custom logic. Return true if you want to consume back press event.
     * @return
     */
    public boolean onBackPressed() {

        return false;
    }

    /**
     * return custom fragment tag
     * @return String custom fragment tag
     */
    public String getFragmentTAG() {
        return fragmentTAG;
    }

    public void onResume() {
        super.onResume();

        resumeFocus();
    }

    /**
     * method that resumes focus on current fragment after screen dimm. Is incoked on fragments onResume() method.
     * It prevents nested fragments from losing focus and stop being interactive after turning of the screen.
     */
    public void resumeFocus() {
        Log.d(TAG, "resume focus fragment");

        SmartPitAppHelper.getInstance(this.getActivity()).resumeFocus(this.getView(),
                this.getFragmentsListener());
    }

    /**
     * returns current SmartPitFragmentsInterface (SmartPitActivity or SmartPitBaseFragment)
     * @return
     */
    public SmartPitFragmentsInterface getFragmentsListener() {
        return listener;
    }

    /**
     * strip view method for stripping all layout. By default is invoked inside onDestroyView().
     * Method clears all views from layout and removes bitmap for better memory managment.
     */
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
