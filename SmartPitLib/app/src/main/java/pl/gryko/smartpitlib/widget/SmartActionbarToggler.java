package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by piotr on 25.07.15.
 */
public class SmartActionbarToggler extends View {

    private String TAG = SmartActionbarToggler.class.getName();

    public enum ToggleMode {
        ARROW, STRIPES;
    }

    public enum ArrowMode {
        LEFT, RIGHT;
    }

    private int gravity = Gravity.LEFT;
    private Paint paint;
    private float MOVE = 0.0f;
    private float UNFINISHED_MOVE;
    private float dividerHeight;
    private int color = android.R.color.white;
    private Context context;
    private ToggleMode mode = ToggleMode.ARROW;
    private ArrowMode arrow_mode = ArrowMode.LEFT;

    private Animation endIfOpenNotFinished;
    private Animation endIfCloseNotFinished;

    //Allaws to set padding animation
    private float animatedOffset=0;
    //Allaws to chage widht of short arrow stripe
    private float arrowWieght=3;

    public void setAnimatedOffset(float offset)
    {
        this.animatedOffset=offset;
    }
    public void setArrowWieght(float arrowWeight)
    {
        this.arrowWieght=arrowWeight;
    }


    public void setGravity(int g) {
        this.gravity = g;
    }

    private void init(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(context.getResources().getColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        endIfOpenNotFinished = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //super.applyTransformation(interpolatedTime, t);
                update((UNFINISHED_MOVE) + (UNFINISHED_MOVE * interpolatedTime));


            }
        };
        endIfOpenNotFinished.setDuration(200);
        endIfCloseNotFinished = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                //super.applyTransformation(interpolatedTime, t);
                update(UNFINISHED_MOVE + (UNFINISHED_MOVE * interpolatedTime));


            }
        };
        endIfCloseNotFinished.setDuration(200);
    }

    public void startFinishOpenAnimation() {

        if (MOVE != 0 && MOVE != 1) {
            UNFINISHED_MOVE = MOVE;
            Log.d(TAG, "start finnishing animation " + MOVE);
            this.startAnimation(endIfOpenNotFinished);
        }

    }

    public void startFinishCloseAnimation() {

        if (MOVE != 0 && MOVE != 1) {
            UNFINISHED_MOVE = MOVE;
            Log.d(TAG, "start finnishing animation " + MOVE);
            this.startAnimation(endIfCloseNotFinished);
        }

    }

    public SmartActionbarToggler(Context context) {
        super(context);
        init(context);
        //dividerHeight = 20 / context.getResources().getDisplayMetrics().density;

    }

    public SmartActionbarToggler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public void update(float move) {

        if (mode == ToggleMode.STRIPES)
            this.MOVE = move / 1.5f;
        else
            this.MOVE = move;
        this.invalidate();
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(context.getResources().getColor(color));

    }

    public void setMode(ToggleMode mode) {
        if (this.mode != mode)
            this.mode = mode;
    }

    public void setArrowMode(ArrowMode mode) {
        if (this.arrow_mode != mode)
            this.arrow_mode = mode;
    }


    public void onDraw(Canvas canvas) {


        if (mode == ToggleMode.STRIPES) {
            dividerHeight = this.getHeight() / 6;
            int width = this.getWidth() - (int) (this.getWidth() * MOVE);


            float secondTop = this.getHeight() / 2;

            float firstTop = secondTop - dividerHeight;
            float thirdTop = secondTop + dividerHeight;


            //canvas.drawlin

            if (gravity == Gravity.LEFT) {
                canvas.drawLine(0, firstTop, width, firstTop, paint);

                canvas.drawLine(0, secondTop, width, secondTop, paint);

                canvas.drawLine(0, thirdTop, width, thirdTop, paint);
            } else {
                canvas.drawLine(this.getWidth() - width, firstTop, this.getWidth(), firstTop, paint);

                canvas.drawLine(this.getWidth() - width, secondTop, this.getWidth(), secondTop, paint);

                canvas.drawLine(this.getWidth() - width, thirdTop, this.getWidth(), thirdTop, paint);
            }


        } else {


            dividerHeight = this.getHeight() / 6;
            //int dividerHeight = 30;
            int width = this.getWidth();

            float secondTop = this.getHeight() / 2;

            float firstEndTop = secondTop - dividerHeight;
            float firstTop = firstEndTop + MOVE * dividerHeight;
            float thirdEndTop = secondTop + dividerHeight;
            float thirdTop = thirdEndTop - MOVE * dividerHeight;

            float fixedWidth = width - width * MOVE / arrowWieght;

            float fixedPadding = animatedOffset*MOVE;


            //canvas.drawlin


            canvas.save();
            canvas.rotate(180 * MOVE, this.getWidth() / 2, this.getHeight() / 2);

            if (arrow_mode == ArrowMode.RIGHT) {
                canvas.drawLine(fixedPadding, firstTop, fixedWidth, firstEndTop, paint);

                canvas.drawLine(fixedPadding, secondTop, width, secondTop, paint);

                canvas.drawLine(fixedPadding, thirdTop, fixedWidth, thirdEndTop, paint);
            } else {
                canvas.drawLine(width - fixedWidth, firstEndTop, width-fixedPadding, firstTop, paint);

                canvas.drawLine(0, secondTop, width-fixedPadding, secondTop, paint);

                canvas.drawLine(width - fixedWidth, thirdEndTop, width-fixedPadding, thirdTop, paint);
            }
            canvas.restore();

        }


    }
}
