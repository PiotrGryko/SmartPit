package pl.gryko.smartpitlib.bitmaps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import pl.gryko.smartpitlib.widget.Log;

/**
 * Created by piotr on 06.04.14.
 *
 * Class with helpfull methods connected with Bitmaps.
 * Class has singleton structure. Use SmartBitmapsHelper.getInstance(Context) to get correct instance
 *
 */
public class SmartBitmapsHelper {


    /**
     * Listener that will receive bitmap after successfull bitmap loading
     */
    public static interface BitmapLoadingListener {
        public void onBitmapLoaded(Bitmap bitmap);
    }

    private Context context;

    public static SmartBitmapsHelper instance;

    public SmartBitmapsHelper(Context context) {
        this.context = context;
    }

    /**
     * returns fresh instance
     * @param context Context
     * @return SmartBitmapsHelper
     */
    public static SmartBitmapsHelper getInstance(Context context) {
        if (instance == null)
            instance = new SmartBitmapsHelper(context);
        return instance;
    }


    private String TAG = SmartBitmapsHelper.class.getName();


    /**
     * calculate insample size based on given BitmapFactory.options and requested width and height
     * @param options BitmapFactory.Options with bitmap
     * @param reqWidth int requested width
     * @param reqHeight int request height
     * @return int calculated inSampleSize
     */

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

    /**
     * Decodes bitmap from resource. Method invokes inSampleSize method and loads bitmap in memory effecient way
     * @param res Resources
     * @param resId int id of resource cointaing bitmap
     * @param reqWidth int requeted width
     * @param reqHeight int requested height
     * @return Bitmap decoded from resource
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
    /**
     * Decodes bitmap from file. Method invokes inSampleSize method and loads bitmap in memory effecient way

     * @param child File to load bitmap from
     * @param reqWidth int requeted width
     * @param reqHeight int requested height
     * @return Bitmap decoded from resource
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


    /**
     * Decodes bitmap from InputStream. Method invokes inSampleSize method and loads bitmap in memory effecient way

     * @param stream InputStream to load bitmap from
     * @param reqWidth int requeted width
     * @param reqHeight int requested height
     * @return Bitmap decoded from resource
     */
    public Bitmap decodeSampledBitmapFromStream(InputStream stream, int reqWidth,
                                              int reqHeight) throws Throwable {

     //   FileInputStream fis = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();

        try {
           // fis = new FileInputStream(child);
            byte[] byteArr = new byte[0];
            byte[] buffer = new byte[1024];
            int len;
            int count = 0;

            while ((len = stream.read(buffer)) > -1) {
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
            stream.close();

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


    /**
     * method manualy saves Bitmap to disk. Saved files names are hashed md5 input names. This method can be used if necessery, rather use SmartPitBitmapCache
     * @param bitmap Bitmap to be saved on disk
     * @param file String filename that be hashed
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

    /**
     * loads bitmap from disk if it was saved using saveBitmapToCache method.
     * @param file String filename
     * @param width int requested width to scale output bitmap
     * @param height int requested height to scale output bitmap
     * @param listener BitmapLoadingListener that will receive loaded bitmap
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
