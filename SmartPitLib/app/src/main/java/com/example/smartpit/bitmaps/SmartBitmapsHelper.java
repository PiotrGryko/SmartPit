package com.example.smartpit.bitmaps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.smartpit.widget.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by piotr on 06.04.14.
 *
 * Class with helpfull methods connected with Bitmaps managing.
 *
 *
 */
public class SmartBitmapsHelper {



    public static interface BitmapLoadingListener {
        public void onBitmapLoaded(Bitmap bitmap);
    }

    private Context context;

    public static SmartBitmapsHelper instance;

    public SmartBitmapsHelper(Context context) {
        this.context = context;
    }

    public static SmartBitmapsHelper getInstance(Context context) {
        if (instance == null)
            instance = new SmartBitmapsHelper(context);
        return instance;
    }


    private String TAG = SmartBitmapsHelper.class.getName();




    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;


            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /*
    Decode small sized bitmap from resource with given size.

     */

    public Bitmap decodeSampledBitmapFromResource(Resources res,
                                                  int resId, int reqWidth, int reqHeight) {

        InputStream fis = res.openRawResource(resId);


        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            byte[] byteArr = new byte[0];
            byte[] buffer = new byte[1024];
            int len;
            int count = 0;

            while ((len = fis.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(byteArr, 0, count, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "error " + e.toString());
            return null;
        }
    }
 /*
    Decode small sized bitmap from file with given size. Should be run in background thread.

     */

    public Bitmap decodeSampledBitmapFromFile(File child, int reqWidth,
                                              int reqHeight) throws Throwable {

        FileInputStream fis = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            fis = new FileInputStream(child);
            byte[] byteArr = new byte[0];
            byte[] buffer = new byte[1024];
            int len;
            int count = 0;

            while ((len = fis.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }
            fis.close();

            options.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(byteArr, 0, count, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = true;
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Log.d(TAG, "error" +e.toString());
            throw new Throwable();

        }
    }
/*
Save bitmap to cache.

 */

    public void saveBitmapToCache(final Bitmap bitmap, final String file) {

        new Thread() {
            public void run() {


                String filename = new String(Hex.encodeHex(DigestUtils.md5(file)));

                FileOutputStream fos;
                File file;

                file = new File(context.getApplicationContext().getCacheDir(), filename);
                try {
                    fos = new FileOutputStream(file);

                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
                    fos.flush();
                    fos.close();

                    Log.d(TAG, filename + " bitmap saved to cache");

                } catch (Throwable e) {
                    // TODO Auto-generated catch block

                    Log.d(TAG, e.toString());
                }
            }
        }.start();

    }
/*

Load bitmap from cache with given size. BitmapLoadingListener method returns loaded bitmap

 */
    public void loadBitmapFromCache(final String file, final int width, final int height, final BitmapLoadingListener listener) {

        new AsyncTask() {

            private Bitmap b = null;

            @Override
            protected Object doInBackground(Object[] params) {


                String filename = new

                        String(Hex.encodeHex(DigestUtils.md5(file)

                ));

                File file = null;

                try

                {
                    file = new File(context.getApplicationContext().getCacheDir(),
                            filename);

                    b = decodeSampledBitmapFromFile(file, width,
                            height);


                    Log.d(TAG, filename + " bitmap loaded from cache");
                    return b;
                } catch (Throwable e) {
                    Log.d(TAG,
                            filename + " error while loading bitmap from cache "
                                    + e.getMessage()
                    );

                    return b;
                }


            }

            public void onPostExecute(Object result) {

                listener.onBitmapLoaded(b);
            }

        }.execute();


    }
}
