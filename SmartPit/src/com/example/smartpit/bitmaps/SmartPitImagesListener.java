package com.example.smartpit.bitmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

public class SmartPitImagesListener implements ImageListener {

	private String TAG = "MyImageListener";
	private String filename;

	private ImageView imageView;

	Context context;

	public SmartPitImagesListener(Context context, String filename,
			ImageView image) {

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

		new Thread() {
			public void run() {

				new SmartPitBitmapsLoader(context).saveBitmapToCache(b,
						filename);
			}
		}.start();

	}
}
