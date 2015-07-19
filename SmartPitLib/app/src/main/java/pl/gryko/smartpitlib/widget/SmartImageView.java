package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import pl.gryko.smartpitlib.R;


/**
 * Use this class instead ImageView to set source from url using SmartPitAppHelper().getInstance(context).setImage(SmartImageView, url, width, height);
 * Image is feched from web, saved on local and disk memory. If its available in memory its served from local LruCache or DiskLruCache.
 *
 * Class is ViewGroup with wrapped ImageView and ProgressBar inside. In default implementation while feching image progress bar is visible. After
 * successfull  loading, image shows with fading animation. User can set ErrorDrawable that is display  when image cant be load.
 */

public class SmartImageView extends ViewGroup {

    /**
     * Enum mode. invoke SmartImageView object.setMode(Mode) for circle or normal shaped image.
     */
    public static enum Mode {
        CIRLCE, NORMAL;
    }

    private String TAG = SmartImageView.class.getName();

    private ImageView imageView;
    private ImageView errorView;
    private SmartCircleImageView circleImageView;
    private ProgressBar progressBar;
    private Drawable errorImage;
    private Context context;
    private int mode;
    int childWidthSize;
    int childHeightSize;


    private void initChildrens(Context context) {

        errorImage = context.getResources().getDrawable(R.drawable.exclamation_mark);

        if (mode == Mode.CIRLCE.ordinal()) {
            errorView = new SmartCircleImageView(context);
            imageView = new SmartCircleImageView(context);
        } else {
            {
                imageView = new ImageView(context);
                errorView = new SmartCircleImageView(context);

            }
        }
        imageView.setAdjustViewBounds(true);
        progressBar = new ProgressBar(context);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#F9933F"), PorterDuff.Mode.MULTIPLY);


        this.addView(errorView);
        this.addView(imageView);
        this.addView(progressBar);
        this.context = context;
    }

    public SmartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;


        this.setLayoutParams(new ViewGroup.LayoutParams(context, attrs));
        initChildrens(context);


    }


    public SmartImageView(Context context) {
        super(context);


        this.setLayoutParams(this.generateDefaultLayoutParams());
        initChildrens(context);
    }

    /**
     * sets Drawable that is displayed at image loading failure
     * @param error
     */
    public void setErrorImage(Drawable error) {
        errorImage = error;
    }

    /**
     * allows setting deifferent ImageView than default
     * @param imageView ImageView that will holds image source.
     */
    public void setCustomImageView(ImageView imageView) {
        this.imageView = imageView;
        this.removeAllViews();
        this.addView(errorView);

        this.addView(this.imageView);
        this.addView(progressBar);
    }

    /**
     * Allows setting custom imageView for showing error drawable.
     * @param imageView
     */
    public void setCustomErrorImageView(ImageView imageView) {
        this.errorView = imageView;
        this.removeAllViews();
        this.addView(errorView);

        this.addView(this.imageView);
        this.addView(progressBar);
    }

    /**
     * sets image mode. Mode.CIRCLE.oridinal() or Mode.NORMAL.oridinal()
     * @param mode Mode.CIRCLE.oridinal() or Mode.NORMAL.oridinal()
     */
    public void setMode(int mode) {
        this.mode = mode;
        this.removeView(imageView);
        this.removeView(errorView);


        if (mode == Mode.CIRLCE.ordinal()) {
            errorView = new SmartCircleImageView(context);
            imageView = new SmartCircleImageView(context);
        } else {
            errorView = new ImageView(context);
            imageView = new ImageView(context);
        }

        this.addView(errorView);

        this.addView(imageView);
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int childCount = getChildCount();
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            width = widthSpecSize;
        } else {
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                    width = Math.max(width, child.getMeasuredWidth());
                }
            }
        }
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            width = Math.min(width, widthSpecSize);
        }

        // Measure Height
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
        } else {
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);

                if (child.getVisibility() != GONE) {

                    measureChild(child, widthMeasureSpec, heightMeasureSpec);

                    height = Math.max(height, child.getMeasuredHeight());
                }
            }
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            height = Math.min(height, heightSpecSize);
        }

        // Check against minimum width and height
        width = Math.max(width, getSuggestedMinimumWidth());
        height = Math.max(height, getSuggestedMinimumHeight());

        errorView.measure(width, height);

        imageView.measure(width, height);
        progressBar.measure(progressBar.getLayoutParams().width,
                progressBar.getLayoutParams().height);

        // Report final dimensions
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));

    }


    protected void onLayout(boolean flag, int l, int b, int r, int t) {

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();


        errorView.layout(0 + paddingLeft, 0 + paddingTop, childWidthSize
                - paddingRight, childHeightSize - paddingBottom);
        errorView.invalidate();

        imageView.layout(0 + paddingLeft, 0 + paddingTop, childWidthSize
                - paddingRight, childHeightSize - paddingBottom);
        imageView.invalidate();

        int left = (childWidthSize / 2) - (progressBar.getMeasuredWidth() / 2);
        int top = (childHeightSize / 2) - (progressBar.getMeasuredHeight() / 2);

        progressBar.layout(left, top, left + progressBar.getMeasuredWidth(),
                top + progressBar.getMeasuredHeight());

        progressBar.invalidate();
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w > 0)
            childWidthSize = w;
        if (h > 0)
            childHeightSize = h;

    }


    /**
     * sets bitmap to image view, shows result and hide progressbar
     * @param b
     */
    public void setImageBitmap(Bitmap b) {

        Log.d(TAG, "set image bitmap ");

        imageView.setImageBitmap(b);

        if (b != null) {
            imageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            //  errorView.setVisibility(View.GONE);

        }

    }


    /**
     * hides progressbar and shows error drawable
     */
    public void showErrorImage() {

        Log.d(TAG,"show error image");
        progressBar.setVisibility(View.GONE);


        errorView.setVisibility(View.VISIBLE);
        // imageView.setVisibility(View.GONE);
        errorView.setImageDrawable(errorImage);
        imageView.setImageDrawable(errorImage);

        imageView.setVisibility(View.GONE);


    }

    /**
     * returns error image view
     * @return ImageView that holds error drawable
     */
    public ImageView getErrorView()
    {
        return errorView;
    }

    /**
     * returns image IamgeView
     * @return ImageView that holds result image
     */
    public ImageView getImageView() {
        return imageView;
    }


    /**
     * return ProgressBar
     * @return ProgressBar displayed while image loading
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

}