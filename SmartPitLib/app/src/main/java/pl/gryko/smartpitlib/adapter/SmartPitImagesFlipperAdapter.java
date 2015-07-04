package pl.gryko.smartpitlib.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;

import pl.gryko.smartpitlib.widget.SmartImageView;
import pl.gryko.smartpitlib.widget.SmartPitAppHelper;


/*

Flipping images gallery. Simple ViewPager adapter witch images.
Constructor parameters:
context, list of images urls, required width and height of image.
Images are loaded asynchronously. While data are feched, default android progressBar is visible.

 */

public class SmartPitImagesFlipperAdapter extends PagerAdapter {

    public interface OnElementClickedListener {
        public void onClick(int position);
    }

    private String TAG = SmartPitImagesFlipperAdapter.class.getName();
    private ArrayList<String> list;
    private Context context;
    private int width;
    private int height;

    int pos;
    private OnElementClickedListener listener;

    public SmartPitImagesFlipperAdapter(Context context,
                                        ArrayList<String> list, int reqWidth, int reqHeight) {

        this.context = context;
        this.list = list;
        this.width = reqWidth;
        this.height = reqHeight;

    }

    public void setOnelementClickListener(OnElementClickedListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((SmartImageView) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((SmartImageView) object);

        SmartPitAppHelper.getInstance(context).stripViewGroup((SmartImageView) object, false);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View row = null;

        if (position != 0)
            position = position % list.size();

        final int newPos = position;
        pos = newPos;

        SmartImageView imageView;

        imageView = new SmartImageView(context);
        imageView.setMode(SmartImageView.Mode.NORMAL.ordinal());
        imageView.getImageView().setScaleType(ScaleType.CENTER_CROP);
        imageView.getImageView().setAdjustViewBounds(true);

        imageView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));

        SmartPitAppHelper.getInstance(context).setImage(imageView, list.get(position),
                width, height);

        row = imageView;

        ((ViewPager) container).addView(row);

        row.setClickable(false);
        row.setFocusableInTouchMode(false);
        final int pos = position;
        if (this.listener != null) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(pos);
                }
            });
        }

        return row;

    }

}
