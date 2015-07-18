package pl.gryko.smartpitlib.fragment;

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

import java.util.ArrayList;
import java.util.HashMap;

import pl.gryko.smartpitlib.interfaces.SmartPitFragmentsInterface;

/**
 * Created by piotr on 08.04.14.
 *
 * DEPRECATED consider using SmartPitPagerFragment instead.
 */
public abstract class SmartPitTabsFragment extends SmartPitFragment implements SmartPitFragmentsInterface, TabHost.OnTabChangeListener {

    public String TAG = SmartPitTabsFragment.class.getName();

    private TabHost host;
    private ArrayList<SmartPitFragment> fragmentsList;
    private HashMap<String, SmartPitFragment> fragmentsMap;


    private FragmentManager fm;
    private int fragmentsContainer;
    private String currentFragment="";


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


    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "on save instance state");
    }

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        Log.d(TAG, "on restore instance state");
    }

    private void populateIndicators() {
        TabHost.TabSpec spec = null;

        for (int i = 0; i < fragmentsList.size(); i++) {
            spec = this
                    .getHost()
                    .newTabSpec(Integer.toString(i))
                    .setIndicator(
                            createTabIndicator(this.getActivity(),
                                    i)
                    )
                    .setContent(new TabContent(this.getActivity()));
            this.getHost().addTab(spec);




        }

        for(int i=0;i<fragmentsList.size();i++)
        {
            if(currentFragment.equals(fragmentsList.get(i).getFragmentTAG())) {
                this.getHost().setCurrentTab(i);
                break;
            }
        }

        this.getHost().setOnTabChangedListener(this);




    }

    public void initHost(View v, int fragmentsContainerId, ArrayList<SmartPitFragment> list) {

        this.fragmentsContainer = fragmentsContainerId;
        host = (TabHost) v.findViewById(android.R.id.tabhost);
        host.setup();


        if (fragmentsList != null && fragmentsMap != null) {
            populateIndicators();
            return;
        }



        fragmentsList = list;
        fragmentsMap = new HashMap<String, SmartPitFragment>();
        for (int i = 0; i < fragmentsList.size(); i++) {
            fragmentsMap.put(fragmentsList.get(i).getFragmentTAG(), fragmentsList.get(i));
            Log.d(TAG, "added fragment to map " + fragmentsList.get(i).getFragmentTAG());
        }
        fm = this.getChildFragmentManager();


        populateIndicators();


        fm.beginTransaction().add(fragmentsContainerId, fragmentsList.get(0))
                .commitAllowingStateLoss();


        Log.d(TAG, "initial view " + fragmentsList.get(0).getFragmentTAG());
        setCurrentFragment(fragmentsList.get(0), true);


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

        currentFragment = fragment.getFragmentTAG();
    }

    // /////return currently added fragment
    @Override
    public SmartPitFragment getCurrentFragment() {

        Log.d(TAG, "get current fragment " + currentFragment);
        Log.d(TAG, "fragments map  " + fragmentsMap.get(currentFragment).getFragmentTAG());

        return fragmentsMap.get(currentFragment);
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

        setCurrentFragment(fragment, removePrevious);


    }

    public void switchFragment(SmartPitFragment fragment,
                               boolean removePrevious) {
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
        return this.getActivity();
    }


    @Override
    public int getTab() {
        return host.getCurrentTab();
    }

    @Override
    public void onTabChanged(String tabId) {

        this.switchTitleFragment(fragmentsList.get(Integer.parseInt(tabId)), true);

    }

    public boolean onBackPressed() {
        return fragmentsList.get(host.getCurrentTab()).onBackPressed();
    }
}
