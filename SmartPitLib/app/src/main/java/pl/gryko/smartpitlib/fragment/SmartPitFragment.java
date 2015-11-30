package pl.gryko.smartpitlib.fragment;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
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
 * Use getFragmentsListener().switchFragment(new SmartPitFragent(),true) for switch to next page.
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
     * @param savedInstanceState Fragment savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (this.getParentFragment() != null && (this.getParentFragment() instanceof SmartPitFragmentsInterface))
            listener = (SmartPitFragmentsInterface) this.getParentFragment();


        else if (this.getActivity() instanceof SmartPitFragmentsInterface)
            listener = (SmartPitFragmentsInterface) this.getActivity();


        //   listener.setActionBarLabel(getLabel());


    }

    /**
     * Custom onBackPressed. Can be overriden to implement some custom logic. Return true if you want to consume back press event.
     * @return true if event is consumed, false otherwise
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

    public void onStart()
    {
        super.onStart();
        setActionbarLabel();

    }

    /**
     * method that resumes focus on current fragment after screen dimm. Is incoked on fragments onResume() method.
     * It prevents nested fragments from losing focus and stop being interactive after turning of the screen.
     */
    public void resumeFocus() {

        resumeFocus(this.getView(),
                this.getFragmentsListener());
    }

    /**
     * returns current SmartPitFragmentsInterface (SmartPitActivity or SmartPitBaseFragment)
     * @return SmartPitFragmentsInterface listener used for managing fragments
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
        if (this.getView() != null) {
            SmartPitAppHelper.stripViewGroup(this.getView(), false);

            System.gc();

        }

    }

    public void onDestroyView() {
        stripView();
        super.onDestroyView();
    }

    /**
     * return fragment label. This string will be returned in setActionbarLabel(String label).
     * Can be used for display custom text for each fragment on actionbar.
     * @return String label
     */
    public abstract String getLabel();

    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "configuration changed!");
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
