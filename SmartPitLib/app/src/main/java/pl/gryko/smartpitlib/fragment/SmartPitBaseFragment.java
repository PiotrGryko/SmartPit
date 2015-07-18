package pl.gryko.smartpitlib.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.gryko.smartpitlib.R;
import pl.gryko.smartpitlib.interfaces.SmartPitFragmentsInterface;
import pl.gryko.smartpitlib.widget.Log;

/**
 *
 * SmartPitFragment that that is host for childs fragments. Contains own FragmentManager.
 * Implements SmartPitFragmentsInterface the same like SmartPitActivity. Interface forces
 * implementation of fragments managment methods. Each SmartPitBaseFragment has its own fragments backstack
 * so it can be used in example in ViewPager where each page has its own deep navigation.
 *
 *
 */


public class SmartPitBaseFragment extends SmartPitFragment implements
        SmartPitFragmentsInterface {

    private FragmentManager fm;
    private FragmentManager.OnBackStackChangedListener backstackListener;
    private ArrayList<SmartPitFragment> fragmentsList;

    private int position;
    private SmartPitFragment initialFragment;

    private String TAG = SmartPitBaseFragment.this.getClass().getName();

    /**
     * sets initial fragment. Have to be invoked at initialization.
     * @param initialFragment SmartPitFragment to be set as firstFragment
     */
    public void setInitialFragment(SmartPitFragment initialFragment) {
        this.initialFragment = initialFragment;
    }

    /**
     * sets backstack listener for childFragmentManager
     * @param listener
     */
    public void setBackstackListener(FragmentManager.OnBackStackChangedListener listener) {
        this.backstackListener = listener;
    }

    /**
     * Fragment onCreate method. Initializing fragments list and childfragmentmaneger.
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentsList = new ArrayList<SmartPitFragment>();

        fm = this.getChildFragmentManager();
    }


    /**
     * Wrapped on onActivityResult to pass result to child fragments.
     * @param requestCode int startActivity request code
     * @param resultCode int result code
     * @param data Intent that holds return data
     */
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on activity result base fragment");

        for (int i = 0; i < fragmentsList.size(); i++) {
            fragmentsList.get(i).onActivityResult(requestCode, resultCode, data);
        }

    }


    /**
     * onCreateView method that sets initial fragment. Have to be invoked by .super(inflater,parent,savedInstanceState) inside overriden method.
     * @param inflater
     * @param parent
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.smart_base_fragment, parent, false);

        initBase(initialFragment);
        return v;
    }



    public void resumeFocus() {
        super.resumeFocus();
        Log.d(TAG, "resume focus base fragment");

        if (fragmentsList != null && this.getCurrentFragment() != null)
            this.getCurrentFragment().resumeFocus();
        else if (initialFragment != null)
            initialFragment.resumeFocus();
    }


    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * clears backstack to initial fragment.
     */
    public void clearBackstack() {

        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    /**
    custom onBackPresssed implementation.  Returns true if local backstack is not empty.
     */
    public boolean onBackPressed() {
        boolean consumed = false;


        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            consumed = true;
            Log.d(TAG, "event consumed!");
        } else
            Log.d(TAG, "event not consumed!");

        return consumed;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * method invoked in onCreateView method. Sets fragments as initial fragment and current fragment for pages tracking.
     * @param fragment SmartPitFragment to be setted as initial fragment. By default incoked with initialFragment
     *                 setted by setInitialFragment(SmartPitFragment) method.
     */
    public void initBase(SmartPitFragment fragment) {

        if (fragment == null || fm == null)
            return;
        if (fragment.isAdded())
            return;


        this.setCurrentFragment(fragment, false);

        if (backstackListener != null)
            fm.addOnBackStackChangedListener(backstackListener);

        fm.beginTransaction().add(R.id.fragments_container, fragment)
                .commitAllowingStateLoss();

    }


    /**
     * sets current fragment for fragments tracking/
     * @param fragment fragment to be setted
     * @param removePrevious boolean false if you show fev the same fragments in a row.
     */
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

    /**
     * return currently added fragment
     * @return SmartPitFragment currently added fragment
     */
    @Override
    public SmartPitFragment getCurrentFragment() {

        if(initialFragment.isAdded())
            return initialFragment;


        for (int i = 0; i < fragmentsList.size(); i++) {
            if (fragmentsList.get(i).isAdded()) {

                return fragmentsList.get(i);
            }

        }

        return null;
    }

    /**
     * switch fragment and add transiction to backstack.
     * @param fragment SmartPitFragment to be set.
     * @param removePrevious false if  you show few the same fragments in a row
     */
    @Override
    public void switchFragment(SmartPitFragment fragment, boolean removePrevious) {

        if (this.getActivity() == null)
            return;
        if (fragment.isAdded())
            return;

        SmartPitFragment oldFragment = getCurrentFragment();
        if (oldFragment != null && fragment != null) {

            fm.beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in,
                            R.anim.alpha_out, R.anim.alpha_in,
                            R.anim.alpha_out).remove(oldFragment)
                    .add(R.id.fragments_container, fragment).addToBackStack(null)
                    .commitAllowingStateLoss();

            setCurrentFragment(fragment, removePrevious);

        }
    }

    /**
     * switch fragment without adding transiction to backstack.
     * @param fragment SmartPitFragment to be set.
     * @param removePrevious false if  you show few the same fragments in a row
     */
    @Override
    public void switchTitleFragment(SmartPitFragment fragment,
                                    boolean removePrevious) {
        if (this.getActivity() == null)
            return;
        if (fragment.isAdded())
            return;


        clearBackstack();

        SmartPitFragment oldFragment = getCurrentFragment();
        if (oldFragment != null && fragment != null) {

            fm.beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in,
                            R.anim.alpha_out, R.anim.alpha_in,
                            R.anim.alpha_out).remove(oldFragment)
                    .add(R.id.fragments_container, fragment)
                    .commitAllowingStateLoss();

            setCurrentFragment(fragment, removePrevious);
        }
    }

    /**
     * Inkoes setActionbarLabel in parent SmartPitFragmentsInterface. In example SmartPitActivity or parent SmartPitBaseFragment
     * @param label String to be passed to activity
     */
    @Override
    public void setActionBarLabel(String label) {

        if (this.getParentFragment() != null) {
            if (this.getParentFragment() instanceof SmartPitFragmentsInterface) {
                SmartPitFragmentsInterface listener = (SmartPitFragmentsInterface) this.getParentFragment();
                listener.setActionBarLabel(label);
            }
        } else if (this.getActivity() instanceof SmartPitFragmentsInterface) {
            SmartPitFragmentsInterface listener = (SmartPitFragmentsInterface) this.getActivity();
            listener.setActionBarLabel(label);

        }

    }


    /**
     * return currently used FragmentManager
     * @return FragmentManager
     */
    public FragmentManager getManager() {

        return fm;
    }

    public int getTab() {

        return position;
    }

    /**
     * return parent activity
     * @return SmartPitActivity parent activity
     */
    @Override
    public Activity getSmartActivity() {
        // TODO Auto-generated method stub
        return this.getActivity();
    }

    /**
     * return fragent label
     * @return
     */
    public String getLabel() {


        if (initialFragment != null)
            return initialFragment.getLabel();
        else return this.toString();

    }

}
