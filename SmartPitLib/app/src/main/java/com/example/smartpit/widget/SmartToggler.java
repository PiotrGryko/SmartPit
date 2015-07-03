package com.example.smartpit.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;


/**
 * Created by piotr on 18.05.15.
 */
public class SmartToggler extends View {


    private int gravity = Gravity.LEFT;
    private Paint paint;
    private float MOVE = 0.0f;
    private float dividerHeight;
    private int color = android.R.color.white;
    private Context context;


    public void setGravity(int g) {
        this.gravity = g;
    }


    public SmartToggler(Context context) {
        super(context);
        this.context = context;
        paint = new Paint();
        paint.setColor(context.getResources().getColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        //dividerHeight = 20 / context.getResources().getDisplayMetrics().density;

    }

    public SmartToggler(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        paint.setColor(context.getResources().getColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        //dividerHeight = 20 / context.getResources().getDisplayMetrics().density;

    }

    public void update(float move) {
        this.MOVE = move / 1.5f;
        this.invalidate();
    }

    public void setColor(int color)
    {
        this.color=color;
        paint.setColor(context.getResources().getColor(color));

    }


    public void onDraw(Canvas canvas) {

        dividerHeight = this.getHeight() / 5;
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

    }
}
