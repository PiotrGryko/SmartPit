package com.example.smartpit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.example.smartpit.widget.SmartPitSlidingMenu;

public class SmartPitMenuLayout extends RelativeLayout implements AnimationListener {

    private String TAG = SmartPitMenuLayout.this.getClass().getName();

    private Animation inAnimation;

    private Animation outAnimation;

    private SmartPitSlidingMenu menu;

    private int visibility;

    private Context context;

    public void setMenu(final SmartPitSlidingMenu menu) {
        this.menu = menu;

        menu.setSlideAnimationListener(new SmartPitSlidingMenu.SlideAnimationListener() {
            @Override
            public void onFinishShowingAnimation() {

            }

            @Override
            public void onFinishHidingAnimation() {

               if(outAnimation!=null)
                   SmartPitMenuLayout.this.startAnimation(outAnimation);
            }
        });

    }

    public SmartPitMenuLayout(Context context) {
        super(context);
        this.context = context;

    }

    public SmartPitMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public void setInAnimation(Animation inAnimation) {
        this.inAnimation = inAnimation;
        inAnimation.setAnimationListener(this);
    }

    public void setOutAnimation(Animation outAnimation) {
        this.outAnimation = outAnimation;
        outAnimation.setAnimationListener(this);
    }

    public void setDuration(int duration)
    {
        if(inAnimation!=null)
            inAnimation.setDuration(duration);
        if(outAnimation!=null)
            outAnimation.setDuration(duration);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub

        if (visibility == View.VISIBLE) {
            menu.getContentLayout().show();
        } else {
            this.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {


    }

    public void setVisibility(int visibility)
    {
        super.setVisibility(visibility);
        this.visibility=visibility;
    }

    public void showMenuBase() {


        this.setVisibility(View.VISIBLE);
        if (inAnimation != null)
        { startAnimation(inAnimation);
        visibility=View.VISIBLE;
        }


    }

    public void hideMenuBase() {
        menu.getContentLayout().hide();
        visibility=View.GONE;

    }


}
