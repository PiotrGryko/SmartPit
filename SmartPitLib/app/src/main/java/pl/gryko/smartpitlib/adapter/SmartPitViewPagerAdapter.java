package pl.gryko.smartpitlib.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import java.util.ArrayList;

import pl.gryko.smartpitlib.widget.Log;

/**
 * Created by piotr on 14.04.15.
 *
 * Adapter for ViewPager. Can be used in SmartPitPagerFragment instead of SmartPitPagerAdapter.
 * I prefer to hold views instead of fragments in view pager because it works much faster.
 */
public class SmartPitViewPagerAdapter extends PagerAdapter {

    public interface OnElementClickedListener {
        public void onClick(int position);
    }

    private String TAG = SmartPitImagesFlipperAdapter.class.getName();
    private ArrayList<View> list;
    private Context context;

    private OnElementClickedListener listener;

    /**
     * Constructor takes Context and ArrayList of views to be display in ViewPager
     *
     * @param context Context
     * @param list    ArrayList View  on views
     */
    public SmartPitViewPagerAdapter(Context context,
                                    ArrayList<View> list) {

        this.context = context;
        this.list = list;


    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    /**
     * removes view from ViewPager container.
     *
     * @param container ViewGroup container
     * @param position  position of element in ViewPager
     * @param object    Object from position
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);

    }

    public int getItemPosition(Object object) {
        if (list.contains(object))
            return PagerAdapter.POSITION_UNCHANGED;
        else
            return PagerAdapter.POSITION_NONE;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Method that can be overriden by custom implementation. In example it allaws to init widgets and logic of given layout
     *
     * @param v View from position
     */
    public void initView(View v) {
    }

    /**
     * method to init view. Adds initialized element to container ViewPager
     *
     * @param container ViewGroup container
     * @param position  position of current element
     * @return View to be displayed
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View row = list.get(position);

        initView(row);
        Log.d(TAG, "view initialized");
        ((ViewPager) container).addView(row);


        return row;

    }

}
