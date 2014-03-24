package com.example.smartpit.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;

import com.example.smartpit.widget.SmartPitAppHelper;

public class SmartPitImagesFlipperAdapter extends PagerAdapter {

	private String TAG = SmartPitImagesFlipperAdapter.class.getName();
	private ArrayList<String> list;
	private Context context;
	private int width;
	private int height;

	int pos;

	public SmartPitImagesFlipperAdapter(Context context,
			ArrayList<String> list, int reqWidth, int reqHeight) {

		this.context = context;
		this.list = list;
		this.width = reqWidth;
		this.height = reqHeight;

	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);

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

		ImageView imageView;

		imageView = new ImageView(context.getApplicationContext());
		imageView.setScaleType(ScaleType.FIT_CENTER);

		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		SmartPitAppHelper.setImage(context, imageView, list.get(position),
				width, height);

		row = imageView;

		((ViewPager) container).addView(row);

		row.setClickable(false);
		row.setFocusableInTouchMode(false);

		return row;

	}

}
