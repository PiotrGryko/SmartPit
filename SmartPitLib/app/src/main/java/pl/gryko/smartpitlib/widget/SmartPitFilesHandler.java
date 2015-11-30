package pl.gryko.smartpitlib.widget;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by piotr on 12.01.15.
 *
 * Collection of static methods conntected with files handling.
 * Some of methods are quite old and might be not best solution.
 *
 */
public class SmartPitFilesHandler {

    /**
     * Download listener
     */
    public interface DownloadListener {
        public void onSuccess(File file, AsyncTask task);

        public void onFailure(File file, AsyncTask task);

        public void onProgress(int procent);
    }

    private static String TAG = SmartPitFilesHandler.class.getName();

    /**
     * Download file from url and save it cache dir
     * @param url File url
     * @param context Context
     * @param listener Download callback
     * @return AsyncTask to be executed
     */
    public static AsyncTask downloadFile(final String url, final Context context, final DownloadListener listener) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                File result = downloadFromUrlToCache(url, context, listener);
                return result;
            }

            protected void onPostExecute(Object result) {
                if ((File) result != null && listener != null)
                    listener.onSuccess((File) result, this);
                else if ((File) result == null && listener != null)
                    listener.onFailure((File) result, this);


            }
        };
        //task.execute();
        return task;

    }


    private static File downloadFromUrlToCache(String url, Context context, DownloadListener listener) {


        File file = null;
        String filename = "";

        File dir = new File(context.getCacheDir() + "/");

        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            URL downloadUrl = new URL(url);
            filename = new String(Hex.encodeHex(DigestUtils
                    .md5(url)));

            file = new File(dir, filename);

            if (file.exists()) {
                Log.d(TAG, "file already exits");
                return file;
            }

            InputStream is = null;

            file.createNewFile();
            /* Open a connection to that URL. */
            URLConnection ucon = downloadUrl.openConnection();

            is = ucon.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(is);


            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = new byte[1024];

            int current = 0;
            double sum = 0;
            double percent = 0;

            // /////////////////////////////////////////////////////////////////////////
            while ((current = bis.read(data)) != -1) {

                sum += current;

                double p = ((double) sum / (double) bis.available()) * 100;

                if (p > percent) {
                    percent = Math.ceil(p);
                    if (listener != null)
                        listener.onProgress((int) percent);


                }

                fos.write(data, 0, current);

            }

            fos.flush();
            fos.close();
            is.close();


            Log.d(TAG,
                    "loading file finished " + Long.toString(file.length()) + "  " + filename);

            return file;

        } catch (Throwable e) {
            e.printStackTrace();

            Log.d(TAG, "error whhile loading " + filename);

            if (file != null)
                file.delete();
            return null;
        }

    }


    /**
     * Unpack zip file
     * @param zipFile File zip to unpack
     * @return
     */
    public static boolean unpackZip(File zipFile) {
        InputStream is;
        ZipInputStream zis;
        try {
            is = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;

                String filename = ze.getName();

                File file = new File(zipFile.getParentFile().getPath(), filename);
                Log.d(TAG, "file path " + file.getPath());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);


                // reading and writing
                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                    byte[] bytes = baos.toByteArray();
                    fout.write(bytes);
                    baos.reset();
                }

                Log.d(TAG, "file saved " + filename);
                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * read simple String from assets text file
     * @param context Context
     * @param filename Sttring filename
     * @return returns readed String
     */

    public static String readTextFileFromAssets(Context context, String filename) {

        StringBuilder b = new StringBuilder("");
        InputStream fis;
        try {
            fis = context.getAssets().open(filename);

            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = fis.read(buffer)) != -1) {
                b.append(new String(buffer, 0, n));
            }
        } catch (IOException e) {
            //log the exception
        }

        return b.toString();
    }

    /**
     * Reads text file from given directory
     * @param context Context
     * @param dir directory that holds file
     * @param filename String filename to read
     * @return readed String
     */
    public static String readTextFile(Context context, String dir, String filename) {

        StringBuilder b = new StringBuilder("");
        try {
            File f = new File(dir, filename);
            BufferedReader fis = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(f)));
            byte[] buffer = new byte[1024];
            String n = "";
            while ((n = fis.readLine()) != null) {
                b.append(new String(n));
            }
        } catch (IOException e) {
            //log the exception
            Log.d(TAG, e.toString());
        }

        return b.toString();
    }

    /**
     * Saves given String as a textfile to cache dir
     * @param context Context
     * @param data String data to save
     * @param filename String name of a file
     */
    public static void saveTextDataToCache(final Context context, final String data, final String filename) {

        new Thread() {
            public void run() {

                File f = new File(
                        context.getApplicationContext().getCacheDir(), filename);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(f);
                    fos.write(data.getBytes());
                    fos.flush();
                    fos.close();

                } catch (Throwable t) {
                    // TODO Auto-generated catch block
                    Log.d(TAG,
                            "error while saving data to cache " + t.toString());

                }

                Log.d(TAG, "data saved to cache!");

            }
        }.start();

    }

    /**
     * Reads text data from cache directory
     * @param context Context
     * @param filename File name
     * @return String
     */
    public static String loadTextDataFromCache(Context context, String filename) {
        File f = new File(context.getApplicationContext().getCacheDir(),
                filename);
        if (!f.exists()) {
            Log.d(TAG, "can`t load data from cache, file doesn`t exits");

            return "";
        }

        StringBuffer data = new StringBuffer();
        String line;
        try {
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);

            while ((line = dis.readLine()) != null) {
                data.append(line);
            }
            dis.close();
            fis.close();

        } catch (Throwable t) {
            // TODO Auto-generated catch block
            Log.d(TAG, "can`t load data from cache, error while reading data");
            return "";
        }
        Log.d(TAG, "data loaded from cache");

        return data.toString();

    }

}
