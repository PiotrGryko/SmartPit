package pl.gryko.smartpitlib;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;



import java.util.ArrayList;

import pl.gryko.smartpitlib.adapter.SmartPitPagerAdapter;
import pl.gryko.smartpitlib.fragment.SmartPitBaseFragment;
import pl.gryko.smartpitlib.fragment.SmartPitFragment;

/**
 * Created by piotr on 29.09.14.
 *
 * DEPRECATED use SmartPitActivity with SmartPitPagerFragment instead
 */
public abstract class SmartPitPagerActivity  extends SmartPitActivity {

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



    private SmartPitPagerAdapter pagerAdapter;

    private  TabHost host;
    private ViewPager viewPager;
    private ArrayList<SmartPitBaseFragment> fragmentsList;

    public abstract View createTabIndicator(Context context, int index);


    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.smart_pit_pager_activity);

        viewPager = (ViewPager)findViewById(R.id.pager);
        host = (TabHost)findViewById(android.R.id.tabhost);
        host.setup();

    }

    public void setPagerAndHost(ArrayList<SmartPitFragment> list) {



        fragmentsList = new ArrayList<SmartPitBaseFragment>();
        for(int i=0;i<list.size();i++)
        {
            SmartPitBaseFragment base = new SmartPitBaseFragment();
            base.setInitialFragment(list.get(i));
            fragmentsList.add(base);
        }

        TabHost.TabSpec spec = null;
        for(int i=0;i<fragmentsList.size();i++)
        {
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

        setAdapter();
    }



    public void showTabWidget() {
        if (getHost().getTabWidget().getVisibility() != View.VISIBLE)
            getHost().getTabWidget().setVisibility(View.VISIBLE);
    }

    public void hideTabWidget() {

        if (getHost().getTabWidget().getVisibility() != View.GONE)
            getHost().getTabWidget().setVisibility(View.GONE);
    }


    public  TabHost getHost() {
        return host;
    }


    public  ViewPager getPager() {
        return viewPager;
    }

    private void setAdapter() {

        pagerAdapter = new SmartPitPagerAdapter(this
                .getSupportFragmentManager(), fragmentsList);

        new SetAdapterTask().execute();
    }

    private class SetAdapterTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            return null;

        }

        protected void onPostExecute(Void result) {
            viewPager.setAdapter(pagerAdapter);
        }

    }
/*
    public void resumeFocus()
    {
        if(getHost()!=null)
        {
            if(this.getTab()==getHost().getCurrentTab())
            {
                SmartPitAppHelper.getInstance(this.getSherlockActivity()).resumeFocus(this.getView(),
                        this.getFragmentsListener());

            }
        }
    }
    */



}
