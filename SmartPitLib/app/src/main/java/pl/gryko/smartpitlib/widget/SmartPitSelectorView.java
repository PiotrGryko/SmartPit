package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import pl.gryko.smartpitlib.R;


/**
 * Created by piotr on 11.07.15.
 */
public class SmartPitSelectorView extends RelativeLayout {

    private View background;
    private ImageView image;
    private Context context;
    private Animation open_animation;
    private Animation close_animation;

    private boolean queue = false;

    public ImageView getImageView()
    {
        return image;
    }
    public View getBackgroundView()
    {
        return background;
    }


    private void init(Context context) {
        this.context = context;
        this.background = new View(context);
        this.image = new ImageView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        this.background.setLayoutParams(params);
        this.image.setLayoutParams(params);
        //int padding = (int) context.getResources().getDimension(R.dimen.custom_image_padding);
        //this.image.setPadding(padding, padding, padding, padding);

        this.addView(background);
        this.addView(image);


        open_animation = AnimationUtils.loadAnimation(context, R.anim.pointer_in);
        close_animation = AnimationUtils.loadAnimation(context, R.anim.pointer_out);

        open_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                background.setVisibility(View.GONE);
                if (queue)
                    background.startAnimation(close_animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        close_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                background.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                queue = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void configure(int res_bkg, int res_resizing_bkg, int img_src) {
        this.setBackgroundDrawable(context.getResources().getDrawable(res_bkg));
        this.background.setBackgroundDrawable(context.getResources().getDrawable(res_resizing_bkg));
        this.image.setImageDrawable(context.getResources().getDrawable(img_src));
    }

    public SmartPitSelectorView(Context context) {
        super(context);
        init(context);
    }

    public SmartPitSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void open() {
        this.background.startAnimation(open_animation);
    }

    private void close() {

        if (open_animation.hasEnded())
            this.background.startAnimation(close_animation);
        else
            queue = true;
    }


    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                open();
                break;
            case MotionEvent.ACTION_UP:
                close();
                break;

        }
        return true;
    }


}
