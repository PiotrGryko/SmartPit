package com.example.smartpit.widget;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.smartpit.SmartPitActivity;
import com.example.smartpit.bitmaps.SmartPitImagesListener;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;

public class SmartPitAppHelper {

	private static String TAG = SmartPitAppHelper.class.getName();

	private static Context context;
	private static ConnectivityManager cm;
	private static DecimalFormat df;
	private static SharedPreferences pref;
	private static SmartPitAppHelper instance;

	public static void initAppHelper(Context con) {
		context = con;
		cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		df = new DecimalFormat();
		df.setMaximumFractionDigits(0);
		df.setMaximumIntegerDigits(2);
		pref = PreferenceManager.getDefaultSharedPreferences(context);

	}

	public static SmartPitAppHelper getInstance() {
		if (instance == null)
			instance = new SmartPitAppHelper();
		return instance;
	}



	public  Resources getResources() {
		return context.getResources();
	}

	public  void setImage(Context context, SmartImageView imageView,
			String url, int width, int height) {

		Log.d(TAG, "url:  " + url);

		SmartPitImagesListener li = new SmartPitImagesListener(context, url,
				imageView);
		imageView.setImageBitmap(SmartPitActivity.getImageLoader()
				.get(url, li, width, height).getBitmap());

	}

	public  boolean isConnected() {
		boolean isConnected = false;

		NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if ((wifi != null && wifi.isConnected())
				|| (mobile != null && mobile.isConnected()))
			isConnected = true;
		else
			isConnected = false;

		return isConnected;
	}

	public  SharedPreferences getPreferences() {
		return pref;
	}

	public  String convertDecimalToDMS(double coordinate) {
		String result;

		BigDecimal bd = new BigDecimal(coordinate);
		BigDecimal fracDeg = bd.subtract(new BigDecimal(bd.toBigInteger()));

		double latMinutes = fracDeg.doubleValue() * 60;

		bd = new BigDecimal(latMinutes);
		BigDecimal fracMin = bd.subtract(new BigDecimal(bd.toBigInteger()));

		double latSeconds = fracMin.doubleValue() * 60;

		result = df.format(Math.floor(coordinate)) + Html.fromHtml("&deg")
				+ df.format(Math.floor(latMinutes)) + "'"
				+ df.format(Math.floor(latSeconds));

		return result;

	}

	public  int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public  Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		InputStream fis = res.openRawResource(resId);

		// First decode with inJustDecodeBounds=true to check dimensions
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

	public  Bitmap decodeSampledBitmapFromFile(File child, int reqWidth,
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

	public void saveDataToCache(final String data, final String filename) {

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

	public String loadDataFromCache(String filename) {
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

	public boolean isTablet() {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}

	public  void resumeFocus(View view,
			final SmartPitFragmentsInterface listener) {

		Log.d(TAG, "resume focus " + view.toString());

		view.setFocusableInTouchMode(true);

		view.requestFocus();
		view.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				return true;
			}
		});
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (keyCode != KeyEvent.KEYCODE_BACK)
					return false;

				Log.d("FragmentBase", "back pressed");
				if (event.getAction() == KeyEvent.ACTION_UP) {
					listener.getManager().popBackStack();
					if (listener.getManager().getBackStackEntryCount() == 0)
						listener.getSmartActivity().onBackPressed();
				}
				return true;
			}
		});
	}

	public  int getScreenWidth() {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public int getScreenHeight() {
		return context.getResources().getDisplayMetrics().heightPixels;
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

			b = decodeSampledBitmapFromFile(file, width,
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

	public  void stripView(View view, boolean recycle) {

		if (view != null) {

            try{
			if (view instanceof ImageView) {
				if (((ImageView) view).getDrawable() instanceof BitmapDrawable
						&& recycle) {
					((BitmapDrawable) ((ImageView) view).getDrawable())
							.getBitmap().recycle();
				}
				if (((ImageView) view).getDrawable() != null)
					((ImageView) view).getDrawable().setCallback(null);
				((ImageView) view).setImageDrawable(null);
			}
			view.getResources().flushLayoutCache();
			view.destroyDrawingCache();
            }
            catch(Throwable t)
            {
                Log.d(TAG,"strip view error catched");
            }
			Log.d(TAG, "clearing view " + view.toString());
			view = null;
		}
	}

	public  void stripViewGroup(View v, boolean recycle) {

		if (v != null) {
			if (v instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
					stripViewGroup(((ViewGroup) v).getChildAt(i), recycle);
				}

			} else
				stripView(v, recycle);
		}
	}

}
