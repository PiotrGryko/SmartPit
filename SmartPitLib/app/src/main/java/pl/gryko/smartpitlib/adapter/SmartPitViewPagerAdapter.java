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
 *
 *
 */
public class SmartPitViewPagerAdapter extends PagerAdapter {

    public interface OnElementClickedListener {
        public void onClick(int position);
    }

    private String TAG = SmartPitImagesFlipperAdapter.class.getName();
    private ArrayList<View> list;
    private Context context;

    private OnElementClickedListener listener;

    public SmartPitViewPagerAdapter(Context context,
                                    ArrayList<View> list) {

        this.context = context;
        this.list = list;


    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(list.get(position));

    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void initView(View v) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View row = list.get(position);

        initView(row);
        Log.d(TAG, "view initialized");
        ((ViewPager) container).addView(row);


        return row;

    }

}
