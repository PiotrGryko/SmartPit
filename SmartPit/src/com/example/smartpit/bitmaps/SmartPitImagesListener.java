package com.example.smartpit.bitmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.smartpit.widget.SmartImageView;

public class SmartPitImagesListener implements ImageListener {

	private String TAG = "MyImageListener";
	private String filename;

	private SmartImageView imageView;

	Context context;

	public SmartPitImagesListener(Context context, String filename,
			SmartImageView image) {

		this.context = context;
		this.filename = filename;

		this.imageView = image;

	}

	@Override
	public void onErrorResponse(VolleyError arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(ImageContainer arg0, boolean arg1) {
		// TODO Auto-generated method stub

		Log.d(TAG, "onResponse");

		final Bitmap b = arg0.getBitmap();

		if (imageView != null) {
			imageView.setImageBitmap(b);

		}

		

	}
}
