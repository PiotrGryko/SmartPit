package com.example.smartpit.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.smartpit.R;

public class SmartImageView extends ViewGroup {

	private String TAG = SmartImageView.class.getName();

	private ImageView imageView;
	private ProgressBar progressBar;
    private Drawable errorImage;
    private Context context;

	int childWidthSize;
	int childHeightSize;



	

	private void initChildrens(Context context) {

		imageView = new ImageView(context);
		imageView.setAdjustViewBounds(true);
		progressBar = new ProgressBar(context);

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

    public void setErrorDrawable(Drawable error)
    {
        this.errorImage=error;
    }

	public void setImageBitmap(Bitmap b) {
		imageView.setImageBitmap(b);

		if (b != null) {
			progressBar.setVisibility(View.GONE);
			
		}

	}

    public void showErrorImage() {

        progressBar.setVisibility(View.GONE);


        if (errorImage != null) {
            imageView.setImageDrawable(errorImage);

        }
        else
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.exclamation_mark));

    }

	public ImageView getImageView() {
		return imageView;
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
