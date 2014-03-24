package com.example.smartpit.bitmaps;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.example.smartpit.widget.Log;
import com.example.smartpit.widget.SmartPitAppHelper;

public class SmartPitBitmapsLoader {

	private String TAG = "LoadImageFromUrl";

	private Context context;

	public SmartPitBitmapsLoader(Context context) {

		this.context = context;

	}

	public void saveBitmapToCache(Bitmap bitmap, String filename) {

		filename = new String(Hex.encodeHex(DigestUtils.md5(filename)));

		FileOutputStream fos;
		File file;

		file = new File(context.getApplicationContext().getCacheDir(), filename);
		try {
			fos = new FileOutputStream(file);

			bitmap.compress(CompressFormat.PNG, 50, fos);
			fos.flush();
			fos.close();

			Log.d(TAG, filename + " bitmap saved to cache");

		} catch (Throwable e) {
			// TODO Auto-generated catch block

			Log.d(TAG, e.toString());
		}

	}

	public Bitmap loadBitmapFromCache(String filename, int width, int height) {

		filename = new String(Hex.encodeHex(DigestUtils.md5(filename)));

		File file = null;
		Bitmap b = null;
		try {
			file = new File(context.getApplicationContext().getCacheDir(),
					filename);

			b = SmartPitAppHelper.decodeSampledBitmapFromFile(file, width,
					height);
			// listener.setFull(b);

			Log.d(TAG, filename + " bitmap loaded from cache");
			return b;
		} catch (Throwable e) {
			Log.d(TAG,
					filename + " error while loading bitmap from cache "
							+ e.getMessage());

			return b;
		}

	}

}