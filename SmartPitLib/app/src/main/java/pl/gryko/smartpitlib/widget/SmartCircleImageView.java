package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by piotr on 13.05.14.
 *
 * Implementation of ImageView that draw image into circle.
 *
 */
public class SmartCircleImageView extends ImageView {


    public SmartCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartCircleImageView(Context context) {
        super(context);
    }

    /**
     * onDraw implementation. Gets bitmap from ImageView by Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
     * next transform bitmap into cropped one and redraw in on canvas.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        if (bitmap == null)
            return;

        //Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();

        Bitmap roundBitmap = null;
        if (w < h)
            roundBitmap = getCroppedBitmap(bitmap, w);
        else
            roundBitmap = getCroppedBitmap(bitmap, h);


        int left = (w - roundBitmap.getWidth()) / 2;
        int top = (h / roundBitmap.getHeight()) / 2;

        canvas.drawBitmap(roundBitmap, left, top, null);


    }

    /**
     * Draws  bitmap into circle and return transformed bitmap
     * @param bmp bitmap to draw into circle
     * @param radius radius of circle
     * @return circle shaped Bitmap
     */
    public Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }

}