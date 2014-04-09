package com.example.smartpit.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;

import com.example.smartpit.adapter.SmartPitPagerAdapter;

public abstract class SmartPitPagerFragment extends SmartPitFragment {

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

    private static TabHost host;
    private static ViewPager viewPager;
    private ArrayList<SmartPitFragment> fragmentsList;

    public abstract View createTabIndicator(Context context, int index);


    public void setPagerAndHost(View v,  int pagerId, ArrayList<SmartPitFragment> fragmentsList) {

        this.fragmentsList = fragmentsList;

        viewPager = (ViewPager) v.findViewById(pagerId);
        host = (TabHost) v.findViewById(android.R.id.tabhost);
        host.setup();

        TabHost.TabSpec spec = null;
        for(int i=0;i<fragmentsList.size();i++)
        {
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

        setAdapter();
    }

    public static TabHost getHost() {
        return host;
    }


    public static ViewPager getPager() {
        return viewPager;
    }

    private void setAdapter() {

        pagerAdapter = new SmartPitPagerAdapter(this.getSherlockActivity()
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


}
