package com.example.smartpit.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by piotr on 02.04.14.
 */
public class SmartPitSlidingContent extends LinearLayout {






    private String TAG = SmartPitSlidingContent.this.getClass().getName();




    private boolean isShowing = false;
    private boolean isAnimating = false;


    private long mStartTime;
    private float mGap;
    private int mViewLeft;
    private int DURATION = 500;

    private SmartPitSlidingMenu.SlideAnimationListener mSlideListener;


    public SmartPitSlidingContent(Context context) {
        super(context);
    }

    public SmartPitSlidingContent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setDuration(int duration) {
        this.DURATION = duration;
    }

    public void setSlideAnimationListener(SmartPitSlidingMenu.SlideAnimationListener listener)
    {
        this.mSlideListener=listener;
    }


    /* Animation Task */
    private Runnable showAnimationTask = new Runnable() {
        public void run() {
            long mCurTime = SystemClock.uptimeMillis();

            long totalTime = mCurTime - mStartTime;

            // Animation end
            if (totalTime > DURATION) {



                removeCallbacks(showAnimationTask);
                isAnimating=false;




                if(mSlideListener!=null)
                    mSlideListener.onFinishShowingAnimation();

                mGap=1;

            } else {
                float perCent = (((float) totalTime) / DURATION);

                mGap = perCent;

                post(this);
            }


            setPosition(mGap);
            invalidate();

        }
    };

    private Runnable hideAnimationTask = new Runnable() {
        public void run() {
            long mCurTime = SystemClock.uptimeMillis();

            long totalTime = mCurTime - mStartTime;

            // Animation end
            if (totalTime > DURATION) {


                removeCallbacks(hideAnimationTask);
                isAnimating=false;
                if(mSlideListener!=null)
                    mSlideListener.onFinishHidingAnimation();

                mGap=-1;

            } else {
                float perCent = (((float) totalTime) / DURATION);

                mGap = -perCent;


                post(this);
            }


            setPosition(mGap);
            invalidate();

        }
    };

    private void setPosition(float gap) {


        int move = (int) ((float) this.getWidth() * gap);
        this.layout(mViewLeft + move, 0, mViewLeft + this.getWidth() + move, this.getHeight());

    }


    public boolean isShowing() {
        return isShowing;
    }

    public void show() {

        if (!isAnimating) {
            isShowing = isAnimating = true;
            mStartTime = SystemClock.uptimeMillis();

            mViewLeft = this.getLeft();
            post(showAnimationTask);
        }

    }

    public void hide() {

        if (!isAnimating) {
            isShowing = false;
            isAnimating = true;

            mViewLeft = this.getLeft();
            mStartTime = SystemClock.uptimeMillis();
            post(hideAnimationTask);

        }
    }
}
