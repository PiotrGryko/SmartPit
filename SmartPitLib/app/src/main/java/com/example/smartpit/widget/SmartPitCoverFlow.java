package com.example.smartpit.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;

import com.example.smartpit.adapter.SmartPitCoverFlowAdapter;

import java.util.ArrayList;

/**
 * Created by piotr on 29.03.14.
 */
public class SmartPitCoverFlow extends ViewGroup {


    public static interface OnCoverFlowItemClickListener
    {
        public void onItemClick(int position);
    }

    public static enum ORIENTATION {
        VERTICAL, HORIZONTAL
    }

    // ~--- static fields ------------------------------------------------------

    private float initY = 0;
    private float initX = 0;

    private Rect bounds;

    /* Scale ratio for each "layer" of children */
    private static final float SCALE_RATIO = 0.75f;
    /* Gesture sensibility */
    private static final int MAJOR_MOVE = 5;
    /* Animation time */
    private static final int DURATION = 200;

    // ~--- fields -------------------------------------------------------------

    private String TAG = SmartPitCoverFlow.class.getName();

    /* Number of pixel between the top of two Views */
    private int mSpaceBetweenViews = 20;
    /* Rotation between two Views */
    private int mRotation;
    /* Status of rotation */
    private boolean mRotationEnabled = false;
    /* Tanslation between two Views */
    private int mTranslate;
    /* Status of translatation */
    private boolean mTranslatateEnabled = false;

    /* Number of internal Views */
    private int mHowManyViews = 5;
    /* Size of internal Views */
    private float mChildSizeRatio = 0.75f;
    /* Adapter */
    private BaseAdapter mAdapter = null;
    /* Item index of center view */
    private int mCurrentItem = 0;
    /* Index of center view in the ViewGroup */
    private int mCenterView = 2;
    /* Width of all children */
    private int mChildrenWidth;
    /* Width / 2 */
    private int mChildrenWidthMiddle;
    /* Height of all children */
    private int mChildrenHeight;
    /* Height / 2 */
    private int mChildrenHeightMiddle;
    /* Height center of the ViewGroup */
    private int mHeightCenter;
    /* Width center of the ViewGroup */
    private int mWidthCenter;
    /* Number of view below/above center view */
    private int mMaxChildUnderCenter;
    /* Collect crap views */
    private Collector mCollector = new Collector();
    /* Avoid multiple allocation */
    private Matrix mMatrix = new Matrix();

    /* Gap between fixed position (for animation) */
    private float mGap;
    /* is animating */
    private boolean mIsAnimating = false;
    /* Avoid multiple allocation */
    private boolean mIsTouching = false;
    private long mCurTime;
    /* Animation start time */
    private long mStartTime;
    /* Final item to reach (for animation from mCurrentItem to mItemToReach) */
    private int mItemtoReach = 0;

    private float finalGap = 0;
    private int mLayoutOrientation;


    private int currentListSize;





    public void setCurrentListSize(int size) {
        this.currentListSize = size;

        android.util.Log.d(TAG, "current list size: " + Integer.toString(size));

    }

    /* Animation Task */
    private Runnable animationTask = new Runnable() {
        public void run() {
            mCurTime = SystemClock.uptimeMillis();

            long totalTime = mCurTime - mStartTime;

            // Animation end
            if (totalTime > DURATION || (mGap > 1 || mGap < -1)) {

                android.util.Log.d(TAG, "before view added");

                // Add new views
                if (mItemtoReach > mCurrentItem) {
                    fillBottom();
                } else {
                    fillTop();
                }

                // Register value to stop animation

                mCurrentItem = mItemtoReach;

                android.util.Log.d(TAG,
                        "current item equals item to reach"
                                + Integer.toString(mCurrentItem));

                mGap = 0;
                mIsAnimating = false;
                mIsTouching = false;

                // Calculate the new center view in the ViewGroup
                mCenterView = mCurrentItem;
                if (mCurrentItem >= mMaxChildUnderCenter) {
                    mCenterView = mMaxChildUnderCenter;
                }


                removeCallbacks(animationTask);


            } else {
                float perCent = ((float) totalTime) / DURATION;

                mGap = ((mCurrentItem - mItemtoReach) * perCent) + finalGap;



                post(this);
            }
            // Layout children

            childrenLayout(mGap);
            invalidate();

        }
    };

    // ~--- constructors -------------------------------------------------------

    public SmartPitCoverFlow(Context context) {
        super(context);

        initSlidingAnimation();
        // Window w = this.getw
    }

    public SmartPitCoverFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSlidingAnimation();
    }

    public SmartPitCoverFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSlidingAnimation();
    }

    // ~--- set methods --------------------------------------------------------

    /* Define height space in pixel between 2 views */
    public void setSpaceBetweenViews(int spaceInPixel) {
        mSpaceBetweenViews = spaceInPixel;
    }

    /* Define rotation between 2 views */
    public void setRotation(int rotation) {
        mRotationEnabled = true;
        mRotation = rotation;
    }

    public void disableRotation() {
        mRotationEnabled = false;
    }

    /* Define translate between 2 views */
    public void setTranslate(int translate) {
        mTranslatateEnabled = true;
        mTranslate = translate;
    }

    public void disableTranslate() {
        mTranslatateEnabled = false;
    }

    public void setLayoutOrientation(int orientation) {
        if (orientation == ORIENTATION.VERTICAL.ordinal())
            this.mLayoutOrientation = ORIENTATION.VERTICAL.ordinal();
        else
            this.mLayoutOrientation = ORIENTATION.HORIZONTAL.ordinal();

    }

    /* Specify number of child to display (only odd number for this version) */
    public boolean setHowManyViews(int howMany) {
        if (howMany % 2 != 0) {
            return false;
        }
        mHowManyViews = howMany;

        return true;
    }

    /* Specify size ratio of all children */
    public boolean setChildSizeRation(float parentPerCent) {
        if ((parentPerCent > 1f) && (parentPerCent < 1f)) {
            return false;
        }
        mChildSizeRatio = parentPerCent;

        return true;
    }



    /* Set adapter */
    public void setAdapter(BaseAdapter adapter) {
        // if (adapter != null) {
        mAdapter = adapter;

        mCenterView = 1;
        mCurrentItem = mCenterView;

        if (adapter.getCount() == 1) {
            mCurrentItem = mCenterView = 0;
        }


        if ((mHowManyViews % 2) == 0) {
            // TODO : Fix it (for the moment work only with odd
            // mHowManyViews)
            mMaxChildUnderCenter = (mHowManyViews / 2);
            // odd
        } else {
            mMaxChildUnderCenter = (mHowManyViews / 2);
        }


        android.util.Log.d(TAG, "how many " + Integer.toString(mHowManyViews));

        for (int i = 0; i <= mMaxChildUnderCenter + mCenterView; i++) {
            if (i > (mAdapter.getCount() - 1)) {
                break;
            }
            final View v = mAdapter.getView(i, null, this);
            addView(v, i);

        }

        childrenLayout(0);
        invalidate();

    }

    public void setInitialPosition(int position) {

        android.util.Log.d(TAG,
                "current center view setposition: "
                        + Integer.toString(mCurrentItem % currentListSize));


        this.removeViewAt(mCenterView);
        this.addView(mAdapter.getView(mCurrentItem, null, this), mCenterView);

        android.util.Log.d(TAG,
                "list position "
                        + Integer.toString(mCurrentItem % currentListSize)
                        + "cover flow element " + Integer.toString(mCenterView));

    }

    private void fillTop() {

        // Local (below center): too many children
        if (mCenterView < mMaxChildUnderCenter) {
            if (getChildCount() > mMaxChildUnderCenter + 1) {
                View old = getChildAt(getChildCount() - 1);

                detachViewFromParent(old);
                mCollector.collect(old);
            }
        }

        // Global : too many children
        if (getChildCount() >= mHowManyViews) {
            View old = getChildAt(mHowManyViews - 1);

            detachViewFromParent(old);
            mCollector.collect(old);
        }

        final int indexToRequest = mCurrentItem - (mMaxChildUnderCenter + 1);

        // retrieve if required
        if (indexToRequest >= 0) {
            android.util.Log.v("UITEST", "Fill top with " + indexToRequest);

            View recycled = mCollector.retrieve();
            View v = mAdapter.getView(indexToRequest, recycled, this);

            if (recycled != null) {
                attachViewToParent(v, 0, generateDefaultLayoutParams());
                v.measure(mChildrenWidth, mChildrenHeight);
            } else {
                addView(v, 0);

            }
        }
    }

    /* fillBottom if required and garbage old views out of screen */
    private void fillBottom() {

        // Local (above center): too many children
        if (mCenterView >= mMaxChildUnderCenter) {
            View old = getChildAt(0);

            detachViewFromParent(old);
            mCollector.collect(old);
        }

        // Global : too many children
        if (getChildCount() >= mHowManyViews) {
            View old = getChildAt(0);

            detachViewFromParent(old);
            mCollector.collect(old);
        }

        final int indexToRequest = mCurrentItem + (mMaxChildUnderCenter + 1);

        if (indexToRequest < mAdapter.getCount()) {
            android.util.Log.v("UITEST", "Fill bottom with " + indexToRequest);

            View recycled = mCollector.retrieve();
            View v = mAdapter.getView(indexToRequest, recycled, this);

            if (recycled != null) {
                android.util.Log.v("UITEST", "view attached");
                attachViewToParent(v, -1, generateDefaultLayoutParams());
                v.measure(mChildrenWidth, mChildrenHeight);
            } else {
                android.util.Log.v("UITEST", "view added");
                addView(v, -1);

            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    private void initSlidingAnimation() {
        setChildrenDrawingOrderEnabled(true);
        setStaticTransformationsEnabled(true);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (mIsAnimating)
                    return true;

                float gapFloat;

                int action = event.getAction();

                switch (action) {

                    case MotionEvent.ACTION_CANCEL: {

                        android.util.Log.d(TAG, "cancel");

                        if (finalGap > 0) {

                            // Top-bottom movement
                            if (mCurrentItem > 0) {

                                mItemtoReach = mCurrentItem - 1;
                                mStartTime = SystemClock.uptimeMillis();
                                mIsAnimating = true;
                                post(animationTask);

                                android.util.Log.d(TAG,
                                        "top-bottom"
                                                + Integer.toString(mCurrentItem));

                                return true;
                            }
                        } else {

                            // Bottom-Top movement
                            if (mCurrentItem < (mAdapter.getCount() - 1)) {

                                android.util.Log.d(TAG,
                                        "bottom top "
                                                + Integer.toString(mCurrentItem));

                                mItemtoReach = mCurrentItem + 1;
                                mStartTime = SystemClock.uptimeMillis();
                                mIsAnimating = true;
                                post(animationTask);

                                return true;
                            }
                        }
                        return true;
                    }

                    case MotionEvent.ACTION_DOWN: {
                        initY = event.getY();
                        initX = event.getX();
                        // mIsTouching = true;

                        android.util.Log.d(TAG, "curret " + Integer.toString(mCurrentItem));

                        int[] location = new int[2];
                        v.getLocationInWindow(location); // get position on the
                        // screen.

                        bounds = new Rect(location[0], location[1], location[0]
                                + SmartPitCoverFlow.this.getWidth(), location[1]
                                + SmartPitCoverFlow.this.getHeight());

                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {

                        if (!bounds.contains((int) SmartPitCoverFlow.this.getLeft()
                                + (int) event.getX(), (int) SmartPitCoverFlow.this.getTop()
                                + (int) event.getY())) {
                            android.util.Log.d(TAG, "leav");
                        }

                        if (mLayoutOrientation == ORIENTATION.VERTICAL.ordinal())
                            gapFloat = event.getY() - initY;
                        else
                            gapFloat = event.getX() - initX;

                        if (Math.abs(gapFloat) > MAJOR_MOVE && !mIsTouching) {
                            initY = event.getY();
                            initX = event.getX();
                            mIsTouching = true;
                        } else {
                            mGap = finalGap = (gapFloat / mSpaceBetweenViews);

                            // Log.d(TAG, "final gap "+Float.toString(finalGap));
                            // if(mCurrentItem>0&&mCurrentItem<mAdapter.getCount()-1)
                            if (finalGap > -1 && finalGap < 1 && mIsTouching) {

                                // if(!(mCurrentItem==0&&finalGap>=0))
                                if ((finalGap > 0 && mCurrentItem > 0)
                                        || finalGap < 0
                                        && mCurrentItem < (mAdapter.getCount() - 1)) {
                                    childrenLayout(finalGap);

                                    invalidate();
                                } else
                                    break;
                            }
                        }

                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        if (mIsTouching) {
                            int decrease = Math.abs((int) finalGap) + 1;
                            // Log.d(TAG, Integer.toString(decrease));

                            if (finalGap > 0) {

                                // Top-bottom movement
                                if (mCurrentItem > 0) {

                                    mItemtoReach = mCurrentItem - 1;
                                    mStartTime = SystemClock.uptimeMillis();
                                    mIsAnimating = true;
                                    post(animationTask);

                                    android.util.Log.d(TAG,
                                            "top-bottom"
                                                    + Integer
                                                    .toString(mCurrentItem));

                                    return true;
                                }
                            } else {

                                // Bottom-Top movement
                                if (mCurrentItem < (mAdapter.getCount() - 1)) {

                                    android.util.Log.d(TAG,
                                            "bottom top "
                                                    + Integer
                                                    .toString(mCurrentItem));

                                    mItemtoReach = mCurrentItem + 1;
                                    mStartTime = SystemClock.uptimeMillis();
                                    mIsAnimating = true;
                                    post(animationTask);

                                    return true;
                                }
                            }
                        } else {
                            if (event.getX() > mWidthCenter - mChildrenWidth / 2
                                    && event.getX() < mWidthCenter + mChildrenWidth
                                    / 2
                                    && event.getY() > mHeightCenter
                                    - mChildrenHeight / 2
                                    && event.getY() < mHeightCenter
                                    + mChildrenHeight / 2)
                                SmartPitCoverFlow.this.getChildAt(mCenterView)
                                        .performClick();
                        }
                    }
                    return true;

                }

                return true;
            }

        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        mWidthCenter = specWidthSize / 2;
        mHeightCenter = specHeightSize / 2;

        if (specHeightSize > specWidthSize)
            mChildrenHeight = mChildrenWidth = (int) (specWidthSize * mChildSizeRatio);
        else
            mChildrenHeight = mChildrenWidth = (int) (specHeightSize * mChildSizeRatio);

        // mChildrenHeight = (int) (specHeightSize * mChildSizeRatio);

        mChildrenWidthMiddle = mChildrenWidth / 2;
        mChildrenHeightMiddle = mChildrenHeight / 2;

        if (mLayoutOrientation == ORIENTATION.VERTICAL.ordinal()) {
            mSpaceBetweenViews = (int) (mChildrenHeight / 1.25f);
        } else {
            mSpaceBetweenViews = (int) (mChildrenWidth);
        }

        // Measure all children
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);



            measureChild(child, mChildrenWidth, mChildrenHeight);
        }

        setMeasuredDimension(specWidthSize, specHeightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        childrenLayout(0);
    }

    /* Fix position of all children */
    public void childrenLayout(float gap) {

        if (mLayoutOrientation == ORIENTATION.HORIZONTAL.ordinal()) {
            final int leftCenterView = mWidthCenter - (mChildrenWidth / 2);
            final int topCenterView = mHeightCenter - (mChildrenHeight / 2);

            final int count = getChildCount();

            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);

                final float offset = mCenterView - i - gap;
                final int left = (int) (leftCenterView - (mSpaceBetweenViews * offset));

                child.layout(left, topCenterView, left + mChildrenWidth,
                        topCenterView + mChildrenHeight);
                // child.l
            }

        } else {
            final int leftCenterView = mWidthCenter - (mChildrenWidth / 2);
            final int topCenterView = mHeightCenter - (mChildrenHeight / 2);

            // Log.d(TAG, "left: "+Integer.toString(leftCenterView));
            // Log.d(TAG, "top: "+Integer.toString(topCenterView));

            final int count = getChildCount();

            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);

                final float offset = mCenterView - i - gap;
                final int top = (int) (topCenterView - (mSpaceBetweenViews * offset));

                child.layout(leftCenterView, top, leftCenterView
                        + mChildrenWidth, top + mChildrenHeight);
            }
        }
        for (int i = 0; i < mHowManyViews; i++) {
            if (getChildAt(i) != null)
                getChildAt(i).invalidate();
        }
    }

    // ~--- get methods --------------------------------------------------------

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int centerView = mCenterView;

        // Log.d(TAG, "child drawing order");

        if (mGap > 0.5f) {
            // if (centerView > 0)
            // Log.d(TAG, "centre view --");
            centerView--;
        } else if (mGap < -0.5f) {
            // if (centerView < mAdapter.getCount() - 1)

            // Log.d(TAG, "centre view ++");

            centerView++;
        }

        // before center view
        if (i < centerView) {
            return i;
            // after center view
        } else if (i > centerView) {
            return centerView + (childCount - 1) - i;
            // center view
        } else {
            return childCount - 1;
        }
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {

        final int topCenterView = mHeightCenter - mChildrenHeightMiddle;
        final int leftCenterView = mWidthCenter - mChildrenWidthMiddle;

        float offset = 0;

        if (mLayoutOrientation == ORIENTATION.VERTICAL.ordinal()) {
            offset = (-child.getTop() + topCenterView)
                    / (float) mSpaceBetweenViews;
        } else {
            offset = (-child.getLeft() + leftCenterView)
                    / (float) mSpaceBetweenViews;
        }

        // if (offset != 0) {

        final float absOffset = Math.abs(offset);
        float scale = (float) Math.pow(SCALE_RATIO, absOffset);

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        // We can play with transparency here -> t.setAlpha()

        final Matrix m = t.getMatrix();
        m.setScale(scale, scale);

        if (mTranslatateEnabled) {
            m.setTranslate(mTranslate * offset, 0);
        }
        if (mLayoutOrientation == ORIENTATION.VERTICAL.ordinal()) {
            // scale from top
            if (offset > 0) {
                m.preTranslate(-mChildrenWidthMiddle, 0);
                m.postTranslate(mChildrenWidthMiddle, 0);

                // scale from bottom
            } else {
                m.preTranslate(-mChildrenWidthMiddle, -mChildrenHeight);
                m.postTranslate(mChildrenWidthMiddle, mChildrenHeight);
            }
        } else {

            m.preTranslate(-mChildrenWidthMiddle, -mChildrenHeight / 2);
            m.postTranslate(mChildrenWidthMiddle, mChildrenHeight / 2);

        }

        mMatrix.reset();
        if (mRotationEnabled) {
            mMatrix.setRotate(mRotation * offset);
        }
        mMatrix.preTranslate(-mChildrenWidthMiddle, -mChildrenHeightMiddle);
        mMatrix.postTranslate(mChildrenWidthMiddle, mChildrenHeightMiddle);

        m.setConcat(m, mMatrix);

        return true;
    }

    // ~--- inner classes ------------------------------------------------------

    /* Class used to recycle views */
    private class Collector {
        ArrayList<View> mOldViews = new ArrayList<View>();

        public void collect(View v) {
            mOldViews.add(v);
        }

        public View retrieve() {
            if (mOldViews.size() == 0) {
                return null;
            } else {
                return mOldViews.remove(0);
            }
        }
    }

}
