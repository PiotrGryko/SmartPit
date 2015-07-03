package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by piotr on 22.11.14.
 */
public class SmartProgressBar extends View {

    private String TAG = SmartProgressBar.class.getName();

    private int currentX;


    private Paint baseColor;
    private Paint currentColor;
    int[] colors;

    private Thread animationRunnable;
    private boolean isAnimating;
    private boolean isFinishing;
    private Handler handler = new Handler();
    private int index = 0;
    private float cycleDuration = 600;
    private long lastTime = 0;
    private long finishingTime = 0;
    private int mainColor;
    private Paint white;
    private float result;

    private int animationIndex;


    public void estimatePosition(float value) {

        if (isAnimating)
            return;


        currentX = (int) (this.getWidth() * value);
        this.invalidate();
    }

    private void init() {

        currentColor = new Paint();

        baseColor = new Paint();


        ///   currentColor.setColor(Color.YELLOW);


    }

    private void estimateAnimation(float value) {
        currentX = (int) (this.getWidth() * value);
        this.invalidate();
    }

    public SmartProgressBar(Context context) {
        super(context);
        init();
    }

    public SmartProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();


    }

    public void setColors(int mainColor, int[] color) {
        colors = color;
        //  paintThree.setColor(color);
        baseColor.setColor(mainColor);
        currentColor.setColor(color[0]);

        this.mainColor = mainColor;
    }

    public void onDraw(Canvas canvas) {


        if (!isAnimating)
            canvas.drawRect((this.getWidth() / 2) - currentX / 2, 0, (this.getWidth() / 2) + currentX / 2, this.getHeight(), baseColor);
        else {


            canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), baseColor);
            canvas.drawRect((this.getWidth() / 2) - currentX / 2, 0, (this.getWidth() / 2) + currentX / 2, this.getHeight(), currentColor);


        }
        super.onDraw(canvas);
        // canvas.drawRect((this.getWidth() / 2) - currentX / 4, 0, (this.getWidth() / 2) + currentX / 4, this.getHeight(), paintThree);


    }

    public void startAnimation() {

        if (isAnimating)
            return;

        isAnimating = true;
        currentX = 0;

        animationRunnable = new Thread() {
            @Override
            public void run() {


                //  Log.d(TAG, "started trehad");

                // double value = 0;
                while (isAnimating) {

                    result = ((float) (System.currentTimeMillis() - lastTime)) / cycleDuration;


                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (System.currentTimeMillis() - lastTime > cycleDuration) {
                        lastTime = System.currentTimeMillis();

                        currentX = 0;


                        baseColor.setColor(currentColor.getColor());
                        //      if (!isFinishing)
                        currentColor.setColor(colors[index]);
                        //     else
                        //        currentColor.setColor(Color.WHITE);


                        index++;

                        if (index == colors.length)
                            index = 0;


                        if (isFinishing && finishingTime == 0) {
                            finishingTime = System.currentTimeMillis();
                            //    baseColor.setColor(currentColor.getColor());

                            //    currentX=0;
                            //    currentColor=white;


                        } else if (isFinishing && finishingTime > 0) {


                            if (animationIndex == 0) {
                                finishingTime = System.currentTimeMillis();
                                animationIndex++;
                                currentColor.setColor(Color.WHITE);


                            } else {
                                animationIndex = 0;
                                finishingTime = 0;
                                isFinishing = false;
                                // stopAnimation();
                                isAnimating = false;

                                baseColor.setColor(mainColor);
                                currentColor.setColor(mainColor);
                                //   currentColor=colors[0];
                                index = 0;
                                //    currentColor=mainColor;
                            }

                        }


                        Log.d(TAG, " cycle ");


                        result = 0;
                    } else {


                        //    Log.d(TAG, "result " + result +" last time "+lastTime +" current time "+System.currentTimeMillis());


                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                estimateAnimation(result);
                            }
                        }, 20);

                    }


                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        estimateAnimation(0);
                        animationRunnable = null;
                    }
                });
            }
        };
        animationRunnable.start();

    }

    public void stopAnimation() {
        isFinishing = true;
    }


}
