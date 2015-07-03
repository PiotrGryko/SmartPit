package com.example.smartpit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by piotr on 12.05.14.
 */

public class SmartPitRowedLayout extends ViewGroup {

    private int line_height;
    private String TAG = SmartPitRowedLayout.class.getName();

    public SmartPitRowedLayout(Context context) {
        super(context);
    }

    public SmartPitRowedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        // ..heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeasured, MeasureSpec.AT_MOST);

        // The next line is WRONG!!! Doesn't take into account requested MeasureSpec mode!
        int height = MeasureSpec.makeMeasureSpec(heightMeasureSpec,MeasureSpec.EXACTLY);
        // int height = getMeasuredHeight();
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();


                //  measureChild(child, widthMeasureSpec, heightMeasureSpec);
                //  height+=child.getMeasuredHeight();

                child.measure(
                        width | MeasureSpec.AT_MOST,
                        height | MeasureSpec.AT_MOST);

                final int childw = child.getMeasuredWidth();



                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.height);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + lp.width;
            }
        }
        this.line_height = line_height;

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        // if(heightMeasureSpec == 0){
        // heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        //  setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(this.getSuggestedMinimumHeight(), heightMeasureSpec));

        //}
        //  Log.d(TAG," height measure spec"+Integer.toString(heightMeasureSpec));

        Log.d(TAG, "widht: " + Integer.toString(width) + " height " + Integer.toString(height));
        setMeasuredDimension(width, height);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return (p instanceof LayoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                Log.d(TAG,"child layout");
                xpos += childw + lp.width;
            }
        }
    }
}