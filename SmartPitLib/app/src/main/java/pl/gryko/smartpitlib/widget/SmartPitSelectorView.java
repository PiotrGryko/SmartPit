package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import pl.gryko.smartpitlib.R;


/**
 * Created by piotr on 11.07.15.
 */
public class SmartPitSelectorView extends RelativeLayout {

    private class FinishAnimation {
        float input;
        float output;

        private Animation finish_animation;

        public Animation getAnimation() {
            return finish_animation;
        }

        public FinishAnimation(final float output) {
            this.input = current_progress;
            this.output = output;

            finish_animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {

                    float result = input + ((output - input) * interpolatedTime);

                    //  Log.d(TAG,"result" +result);
                    if (result <= 1 && input <= 1 && input >= 0 && output <= 1 && output >= 0) {
                        //    Log.d(TAG,"finish animation "+interpolatedTime +" "+input+" "+output+" "+result);
                        setFinishAnimationProgress(result);


                    }

                }
            };
            finish_animation.setDuration(200);
        }

        public void startAnimation() {
            if (input < output) {


                getBackgroundView().startAnimation(finish_animation);
            }
        }
    }

    private View background;
    private ImageView image;
    private Context context;
    private Animation open_animation;
    private Animation close_animation;
    private FinishAnimation finishAnimation;

    private boolean queue = false;
    private float current_progress=0;

    public ImageView getImageView() {
        return image;
    }

    public View getBackgroundView() {
        return background;
    }

    public void setFinishAnimationProgress(float progress) {
        current_progress = progress;
        ViewCompat.setScaleX(getBackgroundView(), progress);
        ViewCompat.setScaleY(getBackgroundView(), progress);

    }
    public void setAnimationProgress(float interpolatedTime) {

        if (finishAnimation != null && finishAnimation.getAnimation().hasStarted() && !finishAnimation.getAnimation().hasEnded()) {
            finishAnimation.getAnimation().cancel();
            getBackgroundView().clearAnimation();
            finishAnimation = null;
        }

        ViewCompat.setScaleX(background, interpolatedTime);
        ViewCompat.setScaleY(background, interpolatedTime);

        current_progress=interpolatedTime;

    }

    public void finishAnimation(float output) {
        finishAnimation = new FinishAnimation(output);
        finishAnimation.startAnimation();
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


        open_animation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        open_animation.setDuration(120);

        close_animation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        close_animation.setDuration(200);
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

    public Animation getOpenAnimation() {
        return open_animation;
    }

    public Animation getCloseAnimation() {
        return close_animation;
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

    public void open() {
        this.background.startAnimation(open_animation);
    }

    public void close() {

        if (open_animation.hasEnded())
            this.background.startAnimation(close_animation);
        else
            queue = true;
    }


    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                open();
                break;
            case MotionEvent.ACTION_UP:
                close();
                break;

        }
        return super.onTouchEvent(event);
    }


}
