package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by piotr on 06.06.15.
 */
public class SmartArrowToggler extends View {


    private Paint paint;
    private float MOVE = 0.0f;

    private float dividerHeight;
    private int color = android.R.color.white;


    public SmartArrowToggler(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(context.getResources().getColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        //dividerHeight=25/context.getResources().getDisplayMetrics().density;
    }

    public SmartArrowToggler(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(context.getResources().getColor(color));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        //dividerHeight=25/context.getResources().getDisplayMetrics().density;

    }

    public void update(float move) {
        this.MOVE = move;
        this.invalidate();
    }


    public void onDraw(Canvas canvas) {


        dividerHeight=this.getHeight()/5;
        //int dividerHeight = 30;
        int width = this.getWidth();

        float secondTop = this.getHeight()/2;

        float firstEndTop = secondTop-dividerHeight;
        float firstTop=firstEndTop+MOVE*dividerHeight;
        float thirdEndTop =  secondTop+dividerHeight;
        float thirdTop = thirdEndTop-MOVE*dividerHeight;

        float fixedWidth = width-width*MOVE/2;



        //canvas.drawlin


        canvas.save();
        canvas.rotate(180*MOVE, this.getWidth()/2,this.getHeight()/2);

        canvas.drawLine(0, firstTop, fixedWidth, firstEndTop, paint);

        canvas.drawLine(0, secondTop, width, secondTop, paint);

        canvas.drawLine(0, thirdTop, fixedWidth, thirdEndTop, paint);
        canvas.restore();



    }
}
