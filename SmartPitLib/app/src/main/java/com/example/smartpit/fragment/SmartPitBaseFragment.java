package com.example.smartpit.fragment;

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


    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on activity result base fragment");

        for (int i = 0; i < fragmentsList.size(); i++) {
            fragmentsList.get(i).onActivityResult(requestCode, resultCode, data);
        }

    }


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

    public void clearBackstack() {

        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

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

        if(initialFragment.isAdded())
            return initialFragment;


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

    // //////////method switch title fragment, transition with in animation not
    // added to backstack
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

    public FragmentManager getManager() {

        return fm;
    }

    public int getTab() {

        return position;
    }

    @Override
    public Activity getSmartActivity() {
        // TODO Auto-generated method stub
        return this.getActivity();
    }

    public String getLabel() {


        if (initialFragment != null)
            return initialFragment.getLabel();
        else return this.toString();

    }

}
