package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * Created by piotr on 05.11.14.
 */
public class SmartPitTabHostIndicator extends LinearLayout {


    public static interface OnSwipeListener {

        public void onSwipeRight(double movement, int position);

        public void onSwipeLeft(double movement, int position);

    }

    private String TAG = SmartPitTabHostIndicator.class.getName();
    private int[] tabs;
    private View indicatorView;
    private Context context;

    private boolean isMovingLeft;
    private boolean isMovingRight;

    private TabHost tabHost;
    private TabWidget tabWidget;

    private double currentMargins;

    private int offset;

    private OnSwipeListener listener;


    public void setCurrentTab(int tab) {
        offset = getTabsOffset(tab);
        updateChildren(tab, 0);
    }

    private int getTabsOffset(int current) {
        int sum = 0;
        if (tabs == null)
            return sum;
        for (int i = 0; i < current; i++) {


            sum += tabs[i];
        }
        return sum;
    }

    public void setSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }

    public SmartPitTabHostIndicator(Context context) {
        super(context);
        this.context = context;
    }

    public SmartPitTabHostIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void initView(final TabHost tabHost, final View indicatorView, final boolean wrapChildrens) {
        Log.d(TAG,"init tabhost view");
        //    ((LayoutParams) tabWidget.getLayoutParams()).weight = 0;
        this.tabHost = tabHost;
        this.tabWidget = tabHost.getTabWidget();

        //  tabWidget.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ///tabWidget.getChildTabViewAt(i).setLayoutParams(childParams);
            if (wrapChildrens)
                tabWidget.getChildTabViewAt(i).getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            else
                tabWidget.getChildTabViewAt(i).getLayoutParams().width = ViewGroup.LayoutParams.FILL_PARENT;

            tabWidget.getChildTabViewAt(i).getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        }

        Log.d(TAG,"start adding layout observer");


        tabWidget.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "on global layout");

                tabs = new int[tabWidget.getChildCount()];
                int sum = 0;

                for (int i = 0; i < tabWidget.getChildCount(); i++) {
                    tabs[i] = tabWidget.getChildTabViewAt(i).getWidth();
                    Log.d(TAG, "width of child element " + tabs[i]);
                    sum += tabs[i];
                }
                LinearLayout.LayoutParams widgetParams = new LayoutParams(sum, ViewGroup.LayoutParams.WRAP_CONTENT);
                tabWidget.setLayoutParams(widgetParams);
                //tabWidget.getLayoutParams().width = sum;
                //tabWidget.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;


                SmartPitTabHostIndicator.this.indicatorView = indicatorView;

                if (tabs != null && tabs.length > 0) {
                    LayoutParams childrenParams = new LayoutParams(tabs[0], ViewGroup.LayoutParams.FILL_PARENT);
                    SmartPitTabHostIndicator.this.indicatorView.setLayoutParams(childrenParams);
                    //    SmartPitTabHostIndicator.this.indicatorView.getLayoutParams().width=tabs[0];
                    //   SmartPitTabHostIndicator.this.indicatorView.getLayoutParams().height= ViewGroup.LayoutParams.FILL_PARENT;

                }
                SmartPitTabHostIndicator.this.removeAllViews();
                SmartPitTabHostIndicator.this.addView(SmartPitTabHostIndicator.this.indicatorView);

                tabWidget.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


    }

    public void updateChildren(int position, double movement) {


        if (position == tabHost.getCurrentTab() && movement > 0 && !isMovingLeft && !isMovingRight) {
            currentMargins = tabs[position + 1] - tabs[position];
            isMovingRight = true;
            isMovingLeft = false;


        } else if (movement > 0 && !isMovingLeft && !isMovingRight) {
            isMovingLeft = true;
            isMovingRight = false;
            currentMargins = tabs[position + 1] - tabs[position];
            // currentMargins = -currentMargins;


        } else if (movement == 0) {
            isMovingLeft = false;
            isMovingRight = false;
        }

        if (isMovingRight && listener != null)
            listener.onSwipeRight(movement, position);
        else if (isMovingLeft && listener != null)
            listener.onSwipeLeft(movement, position);


        if (tabs == null || tabs.length == 0)
            return;

        double move = getTabsOffset(position) + movement * tabs[position];

        if (move != 0)
            offset = (int) move;
        //   if (movement > 0)
        //      currentMargins *= movement;

        invalidate();

        Log.d(TAG, "offset " + offset + " current margis movement " + currentMargins * movement);
        indicatorView.layout(offset, 0, tabs[position] + (int) (offset + currentMargins * movement), indicatorView.getHeight());
        indicatorView.invalidate();

    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (tabs != null) {
            if (tabs.length > 0)
                indicatorView.layout(offset, 0, this.getWidth() / tabs.length + offset, indicatorView.getHeight());
        }
    }


}
