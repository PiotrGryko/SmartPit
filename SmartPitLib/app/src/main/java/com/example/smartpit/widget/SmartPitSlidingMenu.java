package com.example.smartpit.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.smartpit.R;

public class SmartPitSlidingMenu extends RelativeLayout {


    public static interface SlideAnimationListener
    {
        public void onFinishShowingAnimation();
        public void onFinishHidingAnimation();
    }



    private int offset;
    private SmartPitSlidingContent content;

    public void setOffset(int offset)
    {
        this.offset=offset;
    }

    public SmartPitSlidingContent getContentLayout() {
        return content;
    }

    public SmartPitSlidingMenu(Context context) {
        super(context);
        initSlidingLayout(context);

        this.addView(content);
    }

    public SmartPitSlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        initSlidingLayout(context);
        this.addView(content);
    }

    private void initSlidingLayout(Context context) {
        RelativeLayout.LayoutParams contentParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        content = new SmartPitSlidingContent(context);
        content.setLayoutParams(contentParams);

    }

    public void setContent(View c) {
        content.addView(c);
    }
/*

FIX MEASURMENT WITH OFFSET SETTED
 */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
/*
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);


        measureChild(content,content.getMeasuredWidth()+offset,content.getMeasuredHeight());

        setMeasuredDimension(specWidthSize,specHeightSize);
  */
    }

    public void onLayout(boolean flag, int l, int t, int r, int b) {
        content.layout(l, t, r, b);

        content.layout(l - content.getWidth() + offset, t, r - content.getWidth() + offset, b);
    }


    public void show() {
        content.show();
    }

    public void hide() {
        content.hide();
    }

    public void setSlideAnimationListener(SlideAnimationListener listener)
    {
        content.setSlideAnimationListener(listener);
    }


}
