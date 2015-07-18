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


/**
 *
 * SmartPitFragment with wrapped ViewPager and TabHost. Custom implementation have to implement abstract method createTabIndicator(int position)
 * and return view to display it on TabWidget. Class have also wrapped SmartPitTabHostIndicator. This class provides smooth moving TabWidget indicator that
 * moves smoothly with ViewPager pages.
 *
 * minimal sample:
 *
 * public class MyFragment extends SmartPitPagerFragment
 * {
 *
 *     public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanecState)
 *     {
 *         View v = inflater.inflate(R.layout.smart_pager_fragment, parent, false)
 *
 *         ArrayList<SmartPitFragment> fragments_list = new ArrayList<SmartPitFragment>();
 *         fragments_list.add(new SmartPitFragment);
 *         fragments_list.add(new SmartPitFragment);
 *
 *         //method setFragmentsPager() will set SmartPitPagerAdapter for ViewPager. SmartPitFragments from the list will
 *         //be wrapped inside SmartPitBaseFragment. Because of that, each page of default implementation have own backstack.
 *         this.setFragmentsPager(v, R.id.pager,fragments_list)
 *
 *         //instead of setting FragmentsAdapter you can set ViewsAdapter that holds only views and works much faster
 *         ArrayList<View> views_list = new ArrayList<View>();
 *         viewsList.addView(new LinearLayout(this.getActivity());
 *         viewsList.addView(new LinearLayout(this.getActivity());
 *         this.setViewsPager(v,R.id.pager,views_list);
 *
 *
 *         return v;
 *     }
 *     public View createTabIndicator(Context context, int position)
 *     {
 *         TextView tv = new TextView(context);
 *         tv.setText(Integer.toString(position));
 *         tv.setGravity(Gravity.CENTER)
 *         return tv;
 *     }
 *
 * }
 */



public abstract class SmartPitPagerFragment extends SmartPitFragment implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {


    /**
     *
     * Custom swipe listener that can be setted for SmartPitPagerFragment by invoking setOnSwipleListener(OnSwipeListener listener).
     * Listener will receive callbacks on onSwipeRight(double movement, int position) and onSwipeLeft(double movement, int position) based on
     * ViewPager swipe direction.
     *
     */
    public static interface OnSwipeListener {

        public void onSwipeRight(double movement, int position);

        public void onSwipeLeft(double movement, int position);

    }

    private boolean flag = false;
    private String TAG = SmartPitPagerFragment.class.getName();
    private SmartPitTabHostIndicator movingIndicator;

    private boolean isMovingRight;
    private boolean isMovingLeft;
    private OnSwipeListener onSwipeListener;

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

    /**
     *
     * this method inits SmartPitTabHost indicator that moves smoothly with ViewPager swipes.
     *
     * @param parent View returned from onCreateView() method
     * @param indicatorId id of SmartPitTabHostIndicator in layout. In default implementation it is R.id.smart_indicator
     * @param indicatorView View of indicator. It can be empty layout with setted background color. Indicator will measure this view to fit needs.
     * @param wrapChildrens boolean. true if TabWidget elements width have to be WRAP_CONTENT. False if TabWidget elements should be scalled normallly
     * @return SmartPitTabHostIndicator implemenetation.
     */
    public SmartPitTabHostIndicator initMovingIndicator(final View parent, int indicatorId, final View indicatorView, boolean wrapChildrens) {
       // if(movingIndicator!=null)
       //     return movingIndicator;

        movingIndicator = (SmartPitTabHostIndicator) parent.findViewById(indicatorId);


        movingIndicator.initView(SmartPitPagerFragment.this.getHost(), indicatorView, wrapChildrens);


        return movingIndicator;

    }

    /**
     * Sets OnSwipeListener for ViewPager.
     * @param listener
     */
    public void setOnSwipeListener(OnSwipeListener listener)
    {
        this.onSwipeListener = listener;
    }

    private SmartPitPagerAdapter pagerAdapter;

    private SmartPitViewPagerAdapter viewsAdapter;

    private TabHost host;
    private ViewPager viewPager;
    private ArrayList<SmartPitBaseFragment> fragmentsList;

    private ArrayList<View> viewsList;

    /**
     * Each child class has to implement this method for correct ViewPager and TabHost initialization.
     * @param context Context
     * @param index index of tab
     * @return View to be displayed on TabWidget
     */
    public abstract View createTabIndicator(Context context, int index);


    /**
     * returns given ArrayList<SmartPitFragment> list given in setFragmentsPager() method. But wrapped inside SmartPitBaseFragment
     * @return ArrayList<SmartPitBaseFragment> list of pager SmartPitFragments wrapped inside SmartPitBaseFragment
     */
    public ArrayList<SmartPitBaseFragment> getBaseFragmentsList() {
        return fragmentsList;
    }

    /**
     * Method that initialize ViewPager and TabHost with given SmartPitFragment list.
     * Each fragment will be wrapped inside SmartPitBaseFragment and have own backstack
     * @param v View return in onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
     * @param pagerId ViewPager id from layout. R.id.pager in default layout implementation
     * @param list ArrayList<SmartPitFragment> of fragments that will be display on ViewPager pages.
     */
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

    /**
     * Method that initialize ViewPager and TabHost with given View list.
     * Each fragment will be wrapped inside SmartPitBaseFragment and have own backstack
     * @param v View return in onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
     * @param pagerId ViewPager id from layout. R.id.pager in default layout implementation
     * @param list ArrayList<View> of views that will be displayed on ViewPager pages.
     */
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

    /**
     * return initialized TabHost
     * @return TabHost
     */
    public TabHost getHost() {
        return host;
    }

    /**
     * return initialized ViewPager
     * @return ViewPAger
     */
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

    /**
     * Can be overriden for perform custom action after setting ViewPager adapter.
     */
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

    /**
     * resumes focus on currently visible ViewPager pager. It provides losing focus and interactivity on pager after screen dim.
     */
    public void resumeFocus() {
        if (getHost() != null) {
            if (this.getFragmentsListener().getTab() == getHost().getCurrentTab()) {
                SmartPitAppHelper.getInstance(this.getActivity()).resumeFocus(this.getView(),
                        this.getFragmentsListener());

            }
        }
    }


    /**
     * implementation of ViewPager.OnPageChangeListener
     * @param position int position of  flipped page
     * @param movement float movement in range 0.0 - 1.0. Indicates percent of page swipe.
     * @param positionOffsetPixels Swipe offset in pixels
     */
    @Override
    public void onPageScrolled(int position, float movement, int positionOffsetPixels) {
        Log.d(TAG, "on page scrolled position " + position + "  " + movement + "  " + positionOffsetPixels);
        // customOnPageScrolled(position, positionOffset, positionOffsetPixels);

        if (movingIndicator != null) {
            movingIndicator.updateChildren(position, movement);
        }


        if (onSwipeListener != null) {
            if (position == this.getHost().getCurrentTab() && movement > 0 && !isMovingLeft && !isMovingRight) {
                isMovingRight = true;
                isMovingLeft = false;


            } else if (movement > 0 && !isMovingLeft && !isMovingRight) {
                isMovingLeft = true;
                isMovingRight = false;
                // currentMargins = -currentMargins;


            } else if (movement == 0) {
                isMovingLeft = false;
                isMovingRight = false;
            }

            if (isMovingRight)
                onSwipeListener.onSwipeRight(movement, position);
            else if (isMovingLeft)
                onSwipeListener.onSwipeLeft(movement, position);
        }
    }

    /**
     * custom implementation of ViewPager.OnPageChangeListener method. Cooperates with onTabChanged. As result changing TabHost tab flips ViewPager page
     * and changing ViewPager page changes TabHost tab.
     * @param position int position of selectedPage
     */
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

    /**
     * custom implementation of TabHost.OnTabChangeListener. Cooperates with onPageSelected. As result changing TabHost tab flipps ViewPager page
     * and changing ViewPager page changes TabHost tab.
     * @param tabId String indicated tabId
     */
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

    /**
     * back pressed implementation. Returns onBackPressed of current page
     * @return omBackPressed on SmartPitFragment page or false if views adapter is setted.
     */
    public boolean onBackPressed() {
        if (fragmentsList != null)
            return fragmentsList.get(this.getPager().getCurrentItem()).onBackPressed();
        else
            return false;
    }

}
