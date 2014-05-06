package com.example.smartpit.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.R;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.Log;

public class SmartPitBaseFragment extends SmartPitFragment implements
        SmartPitFragmentsInterface {

    private FragmentManager fm;
    private FragmentManager.OnBackStackChangedListener backstackListener;
    private ArrayList<SmartPitFragment> fragmentsList;

    private int position;
    private SmartPitFragment initialFragment;

    private String TAG = SmartPitBaseFragment.this.getClass().getName();

    public void setInitialFragment(SmartPitFragment initialFragment) {
        this.initialFragment = initialFragment;
    }

    public void setBackstackListener(FragmentManager.OnBackStackChangedListener listener) {
        this.backstackListener = listener;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentsList = new ArrayList<SmartPitFragment>();

        fm = this.getChildFragmentManager();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.smart_base_fragment, parent, false);

        initBase(initialFragment);
        return v;
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

    public void clearBackstack() {

        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void initBase(SmartPitFragment fragment) {

        this.setCurrentFragment(fragment, false);

        if (backstackListener != null)
            fm.addOnBackStackChangedListener(backstackListener);

        fm.beginTransaction().add(R.id.fragments_container, fragment)
                .commitAllowingStateLoss();

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
                .add(R.id.fragments_container, fragment).addToBackStack(null)
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
                .add(R.id.fragments_container, fragment)
                .commitAllowingStateLoss();

        setCurrentFragment(fragment, removePrevious);

    }

    @Override
    public void setActionBarLabel(String label) {

        if (this.getParentFragment() != null) {
            if (this.getParentFragment() instanceof SmartPitFragmentsInterface) {
                SmartPitFragmentsInterface listener = (SmartPitFragmentsInterface) this.getParentFragment();
                listener.setActionBarLabel(label);
            }
        } else if (this.getSherlockActivity() instanceof SmartPitFragmentsInterface) {
            SmartPitFragmentsInterface listener = (SmartPitFragmentsInterface) this.getSherlockActivity();
            listener.setActionBarLabel(label);

        }

    }

    public FragmentManager getManager() {

        return fm;
    }

    public int getTab() {

        return position;
    }

    @Override
    public Activity getSmartActivity() {
        // TODO Auto-generated method stub
        return this.getSherlockActivity();
    }

    public String getLabel()
    {
       return "";
    }

}
