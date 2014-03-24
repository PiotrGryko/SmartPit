package com.example.smartpit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class SmartPitAnimatedLayout extends LinearLayout{

	private String TAG = SmartPitAnimatedLayout.this.getClass().getName();

	private Animation inAnimation;

	private Animation outAnimation;

	private int visibility;

	public SmartPitAnimatedLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		
	}

	public SmartPitAnimatedLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setInAnimation(Animation inAnimation) {
		this.inAnimation = inAnimation;
		//inAnimation.setAnimationListener(this);
	}

	public void setOutAnimation(Animation outAnimation) {
		this.outAnimation = outAnimation;
		//outAnimation.setAnimationListener(this);
	}
	
//	public void set

	

	// public void setVisibility(int visibility)
	// {
	// super.
	// }

	public void setVisibile(int visibility) {

		this.visibility = visibility;
		if (getVisibility() != visibility) {
			if (visibility == VISIBLE) {
				if (inAnimation != null)
					startAnimation(inAnimation);
				this.setVisibility(visibility);

			} else if ((visibility == INVISIBLE) || (visibility == GONE)) {
				if (outAnimation != null)
					startAnimation(outAnimation);
			}
		}
		// if(visibility == View.VISIBLE);

		// super.setVisibility(visibility);
	}
}
