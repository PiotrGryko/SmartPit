package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import pl.gryko.smartpitlib.SmartPitMenuActivity;


public class SmartPitMenuLayout extends RelativeLayout implements AnimationListener {

    private String TAG = SmartPitMenuLayout.this.getClass().getName();

    private Animation inAnimation;

    private Animation outAnimation;

    private SmartPitSlidingMenu menu;


    private int visibility;

    private Context context;

    public void setMenu(final SmartPitSlidingMenu menu, SmartPitMenuActivity.MenuType type) {
        this.menu = menu;
        menu.setMenuType(type);

        if(type== SmartPitMenuActivity.MenuType.RIGHT)
        {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            menu.setLayoutParams(layoutParams);
        }
        else if(type==SmartPitMenuActivity.MenuType.LEFT)
        {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            menu.setLayoutParams(layoutParams);
        }

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
