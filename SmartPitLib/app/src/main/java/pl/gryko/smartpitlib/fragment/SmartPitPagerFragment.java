package pl.gryko.smartpitlib.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TabHost;

import pl.gryko.smartpitlib.adapter.SmartPitPagerAdapter;
import pl.gryko.smartpitlib.adapter.SmartPitViewPagerAdapter;
import pl.gryko.smartpitlib.widget.Log;
import pl.gryko.smartpitlib.widget.SmartPitAppHelper;
import pl.gryko.smartpitlib.widget.SmartPitTabHostIndicator;


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

    public SmartPitTabHostIndicator initMovingIndicator(final View parent, int indicatorId, final View indicatorView, boolean wrapChildrens) {
       // if(movingIndicator!=null)
       //     return movingIndicator;

        movingIndicator = (SmartPitTabHostIndicator) parent.findViewById(indicatorId);


        movingIndicator.initView(SmartPitPagerFragment.this.getHost(), indicatorView, wrapChildrens);


        return movingIndicator;

    }

    private SmartPitPagerAdapter pagerAdapter;

    private SmartPitViewPagerAdapter viewsAdapter;

    private TabHost host;
    private ViewPager viewPager;
    private ArrayList<SmartPitBaseFragment> fragmentsList;

    private ArrayList<View> viewsList;

    public abstract View createTabIndicator(Context context, int index);


    public ArrayList<SmartPitBaseFragment> getBaseFragmentsList() {
        return fragmentsList;
    }

    public void setFragmentsPager(View v, int pagerId, ArrayList<SmartPitFragment> list) {

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
                            createTabIndicator(this.getActivity(),
                                    i)
                    )
                    .setContent(new TabContent(this.getActivity()));
            this.getHost().addTab(spec);
        }

        setFragmentsAdapter();


    }


    public void setViewsPager(View v, int pagerId, ArrayList<View> list) {

        viewsList = list;
        viewPager = (ViewPager) v.findViewById(pagerId);
        host = (TabHost) v.findViewById(android.R.id.tabhost);
        host.setup();
        host.getTabWidget().removeAllViews();

        TabHost.TabSpec spec = null;
        for (int i = 0; i < viewsList.size(); i++) {
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

        setViewsAdapter();


    }

    public TabHost getHost() {
        return host;
    }


    public ViewPager getPager() {
        return viewPager;
    }

    private void setViewsAdapter() {
        viewsAdapter = new SmartPitViewPagerAdapter(this.getActivity(), viewsList);
        viewPager.setAdapter(viewsAdapter);

        SmartPitPagerFragment.this.getPager().setOnPageChangeListener(SmartPitPagerFragment.this);
        SmartPitPagerFragment.this.getHost().setOnTabChangedListener(SmartPitPagerFragment.this);

        onAdapterSetted();

    }


    private void setFragmentsAdapter() {

        pagerAdapter = new SmartPitPagerAdapter(this.getActivity()
                .getSupportFragmentManager(), fragmentsList);

        new SetAdapterTask().execute();
    }

    public void onAdapterSetted() {
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
            if (movingIndicator != null)
                movingIndicator.setCurrentTab(SmartPitPagerFragment.this.getPager().getCurrentItem());

            SmartPitPagerFragment.this.getPager().setOnPageChangeListener(SmartPitPagerFragment.this);
            SmartPitPagerFragment.this.getHost().setOnTabChangedListener(SmartPitPagerFragment.this);

            onAdapterSetted();
        }

    }

    public void resumeFocus() {
        if (getHost() != null) {
            if (this.getFragmentsListener().getTab() == getHost().getCurrentTab()) {
                SmartPitAppHelper.getInstance(this.getActivity()).resumeFocus(this.getView(),
                        this.getFragmentsListener());

            }
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.d(TAG, "on page scrolled position " + position + "  " + positionOffset + "  " + positionOffsetPixels);
        // customOnPageScrolled(position, positionOffset, positionOffsetPixels);

        if (movingIndicator != null) {
            movingIndicator.updateChildren(position, positionOffset);
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

    public boolean onBackPressed() {
        if (fragmentsList != null)
            return fragmentsList.get(this.getPager().getCurrentItem()).onBackPressed();
        else
            return false;
    }

}
