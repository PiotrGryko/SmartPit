package com.example.smartpit.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TabHost;

import com.example.smartpit.adapter.SmartPitPagerAdapter;
import com.example.smartpit.widget.Log;
import com.example.smartpit.widget.SmartPitAppHelper;
import com.example.smartpit.widget.SmartPitTabHostIndicator;

public abstract class SmartPitPagerFragment extends SmartPitFragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    private boolean flag = false;
    private String TAG = SmartPitPagerFragment.class.getName();
    private SmartPitTabHostIndicator movingIndicator;

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

    public SmartPitTabHostIndicator initMovingIndicator(final View parent, int indicatorId,final  View indicatorView) {
        movingIndicator = (SmartPitTabHostIndicator) parent.findViewById(indicatorId);


                movingIndicator.initView(SmartPitPagerFragment.this.getHost(), indicatorView);





        return movingIndicator;

    }

    private SmartPitPagerAdapter pagerAdapter;

    private TabHost host;
    private ViewPager viewPager;
    private ArrayList<SmartPitBaseFragment> fragmentsList;

    public abstract View createTabIndicator(Context context, int index);


    public ArrayList<SmartPitBaseFragment> getBaseFragmentsList() {
        return fragmentsList;
    }

    public void setPagerAndHost(View v, int pagerId, ArrayList<SmartPitFragment> list) {

        fragmentsList = new ArrayList<SmartPitBaseFragment>();
        for (int i = 0; i < list.size(); i++) {
            SmartPitBaseFragment base = new SmartPitBaseFragment();
            base.setInitialFragment(list.get(i));
            fragmentsList.add(base);
        }
        viewPager = (ViewPager) v.findViewById(pagerId);
        host = (TabHost) v.findViewById(android.R.id.tabhost);
        host.setup();

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

        setAdapter();


    }

    public TabHost getHost() {
        return host;
    }


    public ViewPager getPager() {
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

            SmartPitPagerFragment.this.getHost().setCurrentTab(SmartPitPagerFragment.this.getPager().getCurrentItem());
            if(movingIndicator!=null)
                movingIndicator.setCurrentTab(SmartPitPagerFragment.this.getPager().getCurrentItem());

            SmartPitPagerFragment.this.getPager().setOnPageChangeListener(SmartPitPagerFragment.this);
            SmartPitPagerFragment.this.getHost().setOnTabChangedListener(SmartPitPagerFragment.this);

        }

    }

    public void resumeFocus() {
        if (getHost() != null) {
            if (this.getFragmentsListener().getTab() == getHost().getCurrentTab()) {
                SmartPitAppHelper.getInstance(this.getSherlockActivity()).resumeFocus(this.getView(),
                        this.getFragmentsListener());

            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "on page scrolled position " + position + "  " + positionOffset + "  " + positionOffsetPixels);
       // customOnPageScrolled(position, positionOffset, positionOffsetPixels);

        if (movingIndicator != null) {
            movingIndicator.updateChildren(position,positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {



        if (!flag) {
            flag = true;

            this.getHost().setCurrentTab(position);
        } else
            flag = false;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String tabId) {

        if (!flag) {
            flag = true;
            this.getPager().setCurrentItem(Integer.parseInt(tabId));


        } else
            flag = false;

        //tabSelected = true;
        /// pageSelected=false;
    }

    public boolean onBackPressed()
    {
        return fragmentsList.get(this.getPager().getCurrentItem()).onBackPressed();
    }

}
