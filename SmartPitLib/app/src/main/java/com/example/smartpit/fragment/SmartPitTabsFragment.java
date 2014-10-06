package com.example.smartpit.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.smartpit.R;
import com.example.smartpit.cloud.SmartPitGcmIntentService;
import com.example.smartpit.cloud.SmartPitRegistrationTask;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.SmartPitAppHelper;

import java.util.ArrayList;

/**
 * Created by piotr on 08.04.14.
 */
public abstract class SmartPitTabsFragment extends SmartPitFragment implements SmartPitFragmentsInterface, TabHost.OnTabChangeListener {

    public String TAG = SmartPitTabsFragment.class.getName();

    private TabHost host;
    private ArrayList<SmartPitFragment> fragmentsList;
    private ArrayList<SmartPitFragment> backstackFragmentsList;


    private FragmentManager fm;
    private int fragmentsContainer;


    class TabContent implements TabHost.TabContentFactory {
        private Context context;

        public TabContent(Context context) {
            this.context = context;
        }

        @Override
        public View createTabContent(String tag) {
            return new View(context);

        }
    }


    public void initHost(View v, int fragmentsContainerId, ArrayList<SmartPitFragment> list) {
        this.fragmentsContainer = fragmentsContainerId;
        host = (TabHost) v.findViewById(android.R.id.tabhost);
        host.setup();


        fragmentsList = list;
        backstackFragmentsList = new ArrayList<SmartPitFragment>();
        fm = this.getChildFragmentManager();


        TabHost.TabSpec spec = null;
        for (int i = 0; i < fragmentsList.size(); i++) {
            spec = this
                    .getHost()
                    .newTabSpec(Integer.toString(i))
                    .setIndicator(
                            createTabIndicator(this.getSherlockActivity(),
                                    i)
                    )
                    .setContent(new TabContent(this.getSherlockActivity()));
            this.getHost().addTab(spec);

        }

        host.setOnTabChangedListener(this);

        fm.beginTransaction().add(fragmentsContainerId, fragmentsList.get(0))
                .commitAllowingStateLoss();


    }

    public abstract View createTabIndicator(Context context, int index);


    public TabHost getHost() {
        return host;
    }


    /*
    this method allaws to add new childrens to fragmentsList.  Second param = true required

     */

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

            for (int i = 0; i < backstackFragmentsList.size(); i++) {
                if (fragment.getClass() == backstackFragmentsList.get(i).getClass()) {
                    backstackFragmentsList.remove(i);
                    backstackFragmentsList.add(fragment);

                    return;
                }

            }
        }
        com.example.smartpit.widget.Log.d(TAG, "new Fragment added to list");
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

        for (int i = 0; i < backstackFragmentsList.size(); i++) {
            if (backstackFragmentsList.get(i).isAdded()) {
                // Log.d(TAG, "setted currentFragment");
                return backstackFragmentsList.get(i);
            }

        }

        return null;
    }


    // //////////method switch title fragment, transition with in animation not
    // added to backstack

    public void switchTitleFragment(SmartPitFragment fragment,
                                    boolean removePrevious) {
        SmartPitFragment oldFragment = getCurrentFragment();

        fm.beginTransaction()
                .remove(oldFragment)
                .add(fragmentsContainer, fragment)
                .commitAllowingStateLoss();

      //  setCurrentFragment(fragment,removePrevious);


    }

    public void switchFragment(SmartPitFragment fragment,
                               boolean removePrevious) {
        SmartPitFragment oldFragment = getCurrentFragment();

        fm.beginTransaction().setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
                .remove(oldFragment)
                .add(fragmentsContainer, fragment).addToBackStack(null)
                .commitAllowingStateLoss();

        setCurrentFragment(fragment,removePrevious);


    }

    @Override
    public void setActionBarLabel(String label) {


    }

    public SmartPitFragmentsInterface getFragmentsListener() {
        return this;
    }


    public FragmentManager getManager() {

        return fm;
    }

    public Activity getSmartActivity() {
        return this.getSherlockActivity();
    }


    @Override
    public int getTab() {
        return host.getCurrentTab();
    }

    @Override
    public void onTabChanged(String tabId) {

        this.switchTitleFragment(fragmentsList.get(Integer.parseInt(tabId)), true);

    }

    public boolean onBackPressed()
    {
        return fragmentsList.get(host.getCurrentTab()).onBackPressed();
    }
}
