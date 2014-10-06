package com.example.smartpit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TabHost;

import com.example.smartpit.fragment.SmartPitBaseFragment;
import com.example.smartpit.fragment.SmartPitFragment;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.SmartPitMenuLayout;
import com.example.smartpit.widget.SmartPitSlidingMenu;

import java.util.ArrayList;

/**
 * Created by piotr on 09.07.14.
 */
public abstract class SmartPitTabMenuActivity  extends SmartPitActivity implements TabHost.OnTabChangeListener {

    public String TAG = SmartPitTabMenuActivity.class.getName();

    private TabHost host;
    private ArrayList<SmartPitBaseFragment> fragmentsList;
   // private ArrayList<SmartPitFragment> backstackFragmentsList;


    private FragmentManager fm;
    private int fragmentsContainer;


    private SmartPitMenuLayout menuBase;
    private SmartPitSlidingMenu menu;

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


    public ArrayList<SmartPitBaseFragment> getBaseFragmentsList()
    {
        return fragmentsList;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.smart_tab_menu_activity);

        host = (TabHost) findViewById(android.R.id.tabhost);
        host.setup();
        //backstackFragmentsList = new ArrayList<SmartPitFragment>();
        fm = this.getSupportFragmentManager();




        menuBase = (SmartPitMenuLayout) this.findViewById(R.id.menu_base);
        menu = (SmartPitSlidingMenu) this.findViewById(R.id.menu);
        // menu.getContentLayout().addView(menuView);
        // menu.getContentLayout().setBackgroundColor(Color.WHITE);
        // menu.getContentLayout().setDuration(500);


        menuBase.setMenu(menu);
        menuBase.setVisibility(View.GONE);


        menuBase.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        menuBase.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        menuBase.setDuration(200);

    }


    public SmartPitSlidingMenu getMenu()
    {
        return menu;
    };



    public void showMenu() {

        if (menuBase.getVisibility() == View.GONE)
            menuBase.showMenuBase();
        else
            menuBase.hideMenuBase();

        menuBase.requestFocus();

    }

    public void initHost(ArrayList<SmartPitFragment> list) {


        fragmentsList = new ArrayList<SmartPitBaseFragment>();
        for(int i=0;i<list.size();i++)
        {
            SmartPitBaseFragment base = new SmartPitBaseFragment();
            base.setInitialFragment(list.get(i));
            fragmentsList.add(base);
        }

       // fragmentsList = list;


        TabHost.TabSpec spec = null;
        for (int i = 0; i < fragmentsList.size(); i++) {
            spec = this
                    .getHost()
                    .newTabSpec(Integer.toString(i))
                    .setIndicator(
                            createTabIndicator(this,
                                    i)
                    )
                    .setContent(new TabContent(this));
            this.getHost().addTab(spec);

        }

        host.setOnTabChangedListener(this);


        setFirstFragment(fragmentsList.get(0));


    }



    public void showTabWidget() {
        if (getHost().getTabWidget().getVisibility() != View.VISIBLE)
            getHost().getTabWidget().setVisibility(View.VISIBLE);
    }

    public void hideTabWidget() {

        if (getHost().getTabWidget().getVisibility() != View.GONE)
            getHost().getTabWidget().setVisibility(View.GONE);
    }

    public abstract View createTabIndicator(Context context, int index);


    public TabHost getHost() {
        return host;
    }

    public SmartPitFragmentsInterface getFragmentsListener() {
        return this;
    }


    public FragmentManager getManager() {

        return fm;
    }

    public Activity getSmartActivity() {
        return this;
    }


    @Override
    public int getTab() {
        return host.getCurrentTab();
    }

    @Override
    public void onTabChanged(String tabId) {

        this.switchTitleFragment(fragmentsList.get(Integer.parseInt(tabId)), true);

    }

}
