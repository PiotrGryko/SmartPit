package com.example.smartpit.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SmartPitViewPager extends ViewPager {

	private boolean swap;

	public SmartPitViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SmartPitViewPager(Context context) {
		super(context);

	}

	public void setSwap(boolean swap) {
		this.swap = swap;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// Never allow swiping to switch between pages
		if (swap)
			super.onInterceptTouchEvent(arg0);

		return swap;

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Never allow swiping to switch between pages
		if (swap)
			super.onTouchEvent(event);
		return swap;

	}

}
