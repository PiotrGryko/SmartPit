package pl.gryko.smartpitlib.bitmaps;

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

import com.jakewharton.disklrucache.DiskLruCache;

import pl.gryko.smartpitlib.widget.Log;

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
        final int cacheSize = maxMemory / 2;

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
        super(getDefaultLruCacheSize());

        try {
            diskLru = DiskLruCache.open(
                    new File(context.getFilesDir(), "cache"), 1, 1,
                    (long) (20 * Math.pow(2, 20)));


        } catch (IOException e) {

            Log.d(TAG, "error!! " + e.toString());
            e.printStackTrace();
        }

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

            Bitmap b = BitmapFactory.decodeStream(in);

            this.put(url, b);


            return b;

        } catch (Throwable e) {

            Log.d(TAG, "error while loading from disk");
            return null;
        }
    }

    public Bitmap isLocalCached(String url) {
        url = new String(Hex.encodeHex(DigestUtils
                .md5(url)));

        Bitmap tmp = get(url);
        Log.d(TAG, "checking local cache " + url +" "+tmp);

        if (tmp != null)
            Log.d(TAG, "bitmap cached in local memory " + url);
        return tmp;
    }

    public boolean isDiskCached(String url) {

        url = new String(Hex.encodeHex(DigestUtils
                .md5(url)));

        Log.d(TAG,"check disck caching "+url);

        try {


            DiskLruCache.Snapshot snapshot = null;


            snapshot = diskLru.get(url);
            if (snapshot == null)
                return false;

            snapshot = null;
            Log.d(TAG, "Bitmap is disc cached " + url);

            return true;

        } catch (Throwable e) {
            Log.d(TAG,e.toString());
            return false;
        }


    }

    public void removeKey(String key) {
        key = new String(Hex.encodeHex(DigestUtils
                .md5(key)));

        try {
            this.remove(key);
            diskLru.remove(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap(String url) {
        if (url == null)
            return null;

        url = new String(Hex.encodeHex(DigestUtils
                .md5(url)));
        Bitmap memoryBitmap = this.get(url);

        if (memoryBitmap != null) {

            Log.d(TAG, "bitmap loaded from local memory");
            return memoryBitmap;

        } else {

            Bitmap b = getFromDisk(url);


            if (b != null && url != null) {
                Log.d(TAG, "loaded bitmap from disk!");
                this.put(url, b);
            }

            return b;

        }

    }

    @Override
    public void putBitmap(final String url, final Bitmap bitmap) {

        if (bitmap == null || url == null)
            return;
        final String hash = new String(Hex.encodeHex(DigestUtils.md5(url)));

        put(hash, bitmap);


        new Thread() {
            public void run() {


                SmartPitBitmapCache.this.putToDisk(hash, bitmap);

            }
        }.start();

    }
}