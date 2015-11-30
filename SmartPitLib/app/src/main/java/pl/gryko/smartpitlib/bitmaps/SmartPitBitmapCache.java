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


/**
 * Main images cache class. It wraps in memory LruCache and DiskLruCache.
 * Bitmaps are saved on disk and if possible to inMemory LruCache.
 * If possible bitmaps are feched from inMemory LruCache, if not they are feched from disk
 */


public class SmartPitBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {

    private static SmartPitBitmapCache mInstance;
    private static String TAG = SmartPitBitmapCache.class.getName();
    private DiskLruCache diskLru;



    private static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 2;

        return cacheSize;
    }

    /**
     * Singleton structure. Returns fresh instance
     * @param ctx Context
     * @return SmartPitBitmapCache instance
     */
    public static SmartPitBitmapCache getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new SmartPitBitmapCache(ctx);
        }
        // context = ctx;
        return mInstance;
    }

    /**
     * SmartPitBitmapCache constructor
     * @param context Context to be used inside class
     */
    public SmartPitBitmapCache(Context context) {
        super(getDefaultLruCacheSize());

        try {
            diskLru = DiskLruCache.open(
                    new File(context.getFilesDir(), "cache"), 1, 1,
                    (long) (20 * Math.pow(2, 20)));


        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    /**
     * Puts bitmap to DiskLruCache.
     * @param key String key
     * @param object Bitmap value
     */
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

        }
    }

    /**
     * Feches Bitmap from DiskLruCache, if not accessible returns null
     * @param url String key of saved bitmap
     * @return Bitmap or null
     */
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

            return null;
        }
    }

    /**
     * Checks if bitmap is availabile in inMemory LruCache
     * @param url String key of saved bitmap
     * @return true or false
     */
    public Bitmap isLocalCached(String url) {
        url = new String(Hex.encodeHex(DigestUtils
                .md5(url)));

        Bitmap tmp = get(url);


        return tmp;
    }

    /**
     * Checks if bitmap is available in diskLruCache.
     * @param url String key of saved bitmap
     * @return true or false
     */
    public boolean isDiskCached(String url) {

        url = new String(Hex.encodeHex(DigestUtils
                .md5(url)));


        try {


            DiskLruCache.Snapshot snapshot = null;


            snapshot = diskLru.get(url);
            if (snapshot == null)
                return false;

            snapshot = null;

            return true;

        } catch (Throwable e) {
            Log.d(TAG,e.toString());
            return false;
        }


    }

    /**
     * method that removes Bitmap with given key from LruCache and DiskLruCache
     * @param key String key of bitmap to remove
     */
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

    /**
     * Main bitmap feching bitmap. Loads it from memory or if not availalble from disk
     * @param url String key of saved Bitmap
     * @return Bitmap or null
     */
    public Bitmap getBitmap(String url) {
        if (url == null)
            return null;

        url = new String(Hex.encodeHex(DigestUtils
                .md5(url)));
        Bitmap memoryBitmap = this.get(url);

        if (memoryBitmap != null) {

            return memoryBitmap;

        } else {

            Bitmap b = getFromDisk(url);


            if (b != null && url != null) {
                this.put(url, b);
            }

            return b;

        }

    }

    /**
     * main saving method. Puts bitmap to local and disk memory
     * @param url String key to save Bitmap
     * @param bitmap Bitmap to be saved with given key
     */
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