package com.example.smartpit.bitmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class SmartPitBitmapCache extends LruCache<String, Bitmap> implements ImageCache {
	
	private static SmartPitBitmapCache mInstance;
	
	public static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		return cacheSize;
	}

	public static SmartPitBitmapCache getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new SmartPitBitmapCache();
		}
		return mInstance;
	}

	public SmartPitBitmapCache() {
		this(getDefaultLruCacheSize());
	}

	public SmartPitBitmapCache(int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight() / 1024;
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}