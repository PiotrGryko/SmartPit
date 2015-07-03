package pl.gryko.smartpitlib.widget;


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

import pl.gryko.smartpitlib.SmartPitMenuActivity;


public class SmartPitSlidingMenu extends RelativeLayout {


    public static interface SlideAnimationListener {
        public void onFinishShowingAnimation();

        public void onFinishHidingAnimation();
    }

    private SmartPitSlidingContent content;
    private SmartPitMenuActivity.MenuType type;


    public SmartPitSlidingContent getContentLayout() {
        return content;
    }

    public SmartPitSlidingMenu(Context context) {
        super(context);
        initSlidingLayout(context);


        this.addView(content);

    }

    public void setMenuType(SmartPitMenuActivity.MenuType type) {
        this.type = type;
        content.setMenuType(type);
    }


    public SmartPitSlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        initSlidingLayout(context);
        this.addView(content);
    }

    public void onLayout(boolean flag, int l, int t, int r, int b) {
        super.onLayout(flag, l, t, r, b);


        if (content.isShowing()) {

            content.layout(r - content.getWidth(), t, r, b);


        } else {

            if(type== SmartPitMenuActivity.MenuType.LEFT)
           content.layout(l - content.getWidth() + content.getOffset(), t, l + content.getOffset(), b);
            else
                content.layout(l, t, l + content.getWidth(), b);


        }


    }

    private void initSlidingLayout(Context context) {


        RelativeLayout.LayoutParams contentParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        content = new SmartPitSlidingContent(context, this);
        content.setLayoutParams(contentParams);
        content.setBackgroundColor(Color.BLUE);

    }

    public void setContent(View c) {
        content.addView(c);
    }


    public void setSlideAnimationListener(SlideAnimationListener listener) {
        content.setSlideAnimationListener(listener);
    }


}
