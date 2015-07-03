package com.example.smartpit.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.smartpit.SmartPitMenuActivity;

/**
 * Created by piotr on 02.04.14.
 */
public class SmartPitSlidingContent extends LinearLayout {


    private String TAG = SmartPitSlidingContent.this.getClass().getName();


    private SmartPitSlidingMenu parent;

    private boolean isShowing = true;
    private boolean isAnimating = false;


    private boolean isTouching;
    private boolean rightMove;
    private float lastMove;


    private long mStartTime;
    private float mGap;
    private float mMove;
    private int mViewLeft;
    private int DURATION = 500;

    private float initX = 0;
    private float initY = 0;

    private int initGap = 10;


    private int offset;

    private SmartPitMenuActivity.MenuType type;

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }


    private SmartPitSlidingMenu.SlideAnimationListener mSlideListener;

    public void setMenuType(SmartPitMenuActivity.MenuType type) {
        this.type = type;
    }

    public SmartPitSlidingContent(Context context, SmartPitSlidingMenu parent) {
        super(context);

        this.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
        // this.setGravity(Gravity.CENTER);
        this.parent = parent;
    }

    public SmartPitSlidingContent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setDuration(int duration) {
        this.DURATION = duration;
    }

    public void initTouchListener() {
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN: {


                        initX = event.getX();
                        initY = event.getY();
                        //   break;
                        return true;
                    }

                    case MotionEvent.ACTION_MOVE: {

                        mMove = event.getX() - initX;

                        if (!isTouching) {
                            initX = event.getX();
                            isTouching = true;
                            lastMove = mMove;
                            if (mMove > 0) {
                                rightMove = true;
                                Log.d(TAG, "right!");
                            } else {
                                rightMove = false;

                                Log.d(TAG, "left!");
                            }


                        } else {


                            mMove = event.getX() - initX;


                            setPosition(mMove / SmartPitSlidingContent.this.getWidth());
                            lastMove = mMove;


                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {

                        if (rightMove)
                            show();
                        else
                            hide();
                        isTouching = false;
                        return true;
                    }

                }
                return true;
            }
        });
    }

    public void setSlideAnimationListener(SmartPitSlidingMenu.SlideAnimationListener listener) {
        this.mSlideListener = listener;
    }


    /* Animation Task */
    private Runnable showAnimationTask = new Runnable() {
        public void run() {
            long mCurTime = SystemClock.uptimeMillis();

            long totalTime = mCurTime - mStartTime;

            // Animation end
            if (totalTime > DURATION || !isAnimating) {


                removeCallbacks(showAnimationTask);
                isAnimating = false;


                if (mSlideListener != null)
                    mSlideListener.onFinishShowingAnimation();


                    mGap = 1;

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
            if (totalTime > DURATION || !isAnimating) {


                removeCallbacks(hideAnimationTask);
                isAnimating = false;
                if (mSlideListener != null)
                    mSlideListener.onFinishHidingAnimation();


                    mGap = -1;
               ;



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

           Log.d(TAG,"set position "+Float.toString(gap) +" width "+this.getWidth()+" move "+this.getWidth()*gap);
        int move =  (int)((float) (this.getWidth()) * gap);

        if(type== SmartPitMenuActivity.MenuType.RIGHT)
            move=-move;

       int finalLeft = mViewLeft + move;
        int finalRight = mViewLeft + this.getWidth() + move;


        Log.d(TAG,"set left "+finalLeft + "move "+move);


        if (type == SmartPitMenuActivity.MenuType.LEFT) {
            if (finalRight > parent.getRight()) {
                Log.d(TAG, "right to far");
                finalLeft = parent.getRight() - this.getWidth();
                finalRight = parent.getRight();
                isTouching = false;
                isAnimating = false;
            } else if (finalRight < parent.getRight() - this.getWidth() + offset) {
                Log.d(TAG, "left to far");
                finalRight = parent.getRight() - this.getWidth() + offset;
                finalLeft = finalRight - this.getWidth();
                isTouching = false;
                isAnimating = false;
            }

        }
        else if(type== SmartPitMenuActivity.MenuType.RIGHT) {
            if (finalLeft > parent.getRight()) {
                Log.d(TAG, "right to far");
                finalLeft = parent.getRight();
                finalRight = parent.getRight()+this.getWidth();
                isTouching = false;
                isAnimating = false;
            } else if (finalLeft < 0) {
                Log.d(TAG, "left to far");
                finalRight =0+this.getWidth();
                finalLeft = 0;
                isTouching = false;
                isAnimating = false;

            }
        }
        Log.d(TAG,"final left "+finalLeft +"  parent left  "+parent.getLeft()+" final right "+finalRight+" parent right "+parent.getRight());
        mViewLeft = finalLeft;
        this.layout(finalLeft, 0, finalRight, this.getHeight());


    }


    public boolean isShowing() {
        return isShowing;
    }

    public void show() {

        Log.d(TAG, "show");

        if (!isAnimating) {
            isShowing = isAnimating = true;
            mStartTime = SystemClock.uptimeMillis();

            mViewLeft = this.getLeft();
            post(showAnimationTask);
        }

    }

    public void hide() {

        Log.d(TAG, "hide");

        if (!isAnimating) {
            isShowing = false;
            isAnimating = true;

            mViewLeft = this.getLeft();
            mStartTime = SystemClock.uptimeMillis();
            post(hideAnimationTask);

        }
    }
}
