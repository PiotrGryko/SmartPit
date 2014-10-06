package com.example.smartpit.bitmaps;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.example.smartpit.widget.Log;
import com.jakewharton.disklrucache.DiskLruCache;


/*

Class responsible for caching/fetching bitmaps.

 */

public class SmartPitBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {

    private static SmartPitBitmapCache mInstance;
    private static String TAG = SmartPitBitmapCache.class.getName();
    private DiskLruCache diskLru;



    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    public static SmartPitBitmapCache getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new SmartPitBitmapCache(ctx);
        }
        // context = ctx;
        return mInstance;
    }

    public SmartPitBitmapCache(Context context) {
        this(getDefaultLruCacheSize());

        try {
            diskLru = DiskLruCache.open(
                    new File(context.getFilesDir(), "cache"), 1, 1,
                    (long) (20 * Math.pow(2, 20)));


        } catch (IOException e) {

            Log.d(TAG, "error!! " + e.toString());
            e.printStackTrace();
        }

    }

    public SmartPitBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    public void putToDisk(String key, Bitmap object) {
        DiskLruCache.Editor editor = null;
        try {

            /*
            open editor of disk cache object to save file.
             */
            editor = diskLru.edit(key);


            if (editor == null) {
                return;
            }

            ObjectOutputStream out = new ObjectOutputStream(
                    editor.newOutputStream(0));

            object.compress(Bitmap.CompressFormat.PNG, 100, out);

            out.close();
            editor.commit();
        } catch (Throwable t) {

            Log.d(TAG, "error wihile putting ! " + t.toString());
        }
    }

    public Bitmap getFromDisk(String url) {

        try {
            //DiskLruCache.
            DiskLruCache.Snapshot snapshot = diskLru.get(url);

            ObjectInputStream in = new ObjectInputStream(
                    snapshot.getInputStream(0));
            return BitmapFactory.decodeStream(in);

        } catch (Throwable e) {

            Log.d(TAG, "error while loading from disk");
            return null;
        }
    }

    public boolean isCached(String url) {


        String mdUrl = new String(Hex.encodeHex(DigestUtils
                .md5(url)));

       // diskLru.

        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = diskLru.get(mdUrl);
            if (snapshot == null)
                return false;

            snapshot = null;
            return true;
        } catch (IOException e) {
            return false;
        }


    }

    public void removeKey(String key)
    {
        key = new String(Hex.encodeHex(DigestUtils
                .md5(key)));

        try {
            diskLru.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(String url) {

        url = new String(Hex.encodeHex(DigestUtils
                .md5(url)));


        Bitmap b = this.getFromDisk(url);


        if (b != null) {
            Log.d(TAG, "loaded bitmap from disk!");
        }

        return b;
    }

    @Override
    public void putBitmap(final String url, final Bitmap bitmap) {

        new Thread() {
            public void run() {
                SmartPitBitmapCache.this.putToDisk(new String(Hex.encodeHex(DigestUtils.md5(url))), bitmap);

            }
        }.start();
    }
}