package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by piotr on 06.06.15.
 *
 * Arrow toggler for action bar that works like Gmail arrow. With animation flot three stipes
 * connects creating arrow with 360 degree rotation. Invoke update(int move) with argument in range 0.0f - 1.0f
 * that indicates percent of animation completion. In example this can be used in cooperation with NavigationDrawerListener
 * that returns percent of drawer slide, or ViewPagerListener that return percent of page flip.
 *
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

    /**
     * returns paint used to draw arrow
     * @return Paint
     */
    public Paint getPaint()
    {
        return paint;
    }

    /**
     * invoke that method inside some sliding event listener. Pass argument in ragne 0.0f - 1.0.f that indicates animation progress
     * @param move float move from 0 to 1 that indicates animation progerss
     */
    public void update(float move) {
        this.MOVE = move;
        this.invalidate();
    }


    /**
     * custom draw method that draws arrow on canvas
     * @param canvas Canvas to draw
     */
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
