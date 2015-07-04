package pl.gryko.smartpitlib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TabHost;



import java.util.ArrayList;

import pl.gryko.smartpitlib.fragment.SmartPitBaseFragment;
import pl.gryko.smartpitlib.fragment.SmartPitFragment;
import pl.gryko.smartpitlib.interfaces.SmartPitFragmentsInterface;

/**
 * Created by piotr on 08.07.14.
 */
public abstract class SmartPitTabActivity extends SmartPitActivity implements TabHost.OnTabChangeListener {

    public String TAG = SmartPitActivity.class.getName();

    private TabHost host;
    private ArrayList<SmartPitBaseFragment> fragmentsList;



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

    public void showTabWidget() {
        if (getHost().getTabWidget().getVisibility() != View.VISIBLE)
            getHost().getTabWidget().setVisibility(View.VISIBLE);
    }

    public void hideTabWidget() {

        if (getHost().getTabWidget().getVisibility() != View.GONE)
            getHost().getTabWidget().setVisibility(View.GONE);
    }


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.smart_tab_activity);

        host = (TabHost)findViewById(android.R.id.tabhost);
        host.setup();
       // backstackFragmentsList = new ArrayList<SmartPitFragment>();
        fm = this.getSupportFragmentManager();




    }



    public void  initHost(ArrayList<SmartPitFragment> list) {




        fragmentsList = new ArrayList<SmartPitBaseFragment>();
        for(int i=0;i<list.size();i++)
        {
            SmartPitBaseFragment base = new SmartPitBaseFragment();
            base.setInitialFragment(list.get(i));
            fragmentsList.add(base);
        }



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