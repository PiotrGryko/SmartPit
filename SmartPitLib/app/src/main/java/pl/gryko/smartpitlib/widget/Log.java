package pl.gryko.smartpitlib.widget;

/**
 * Implementation of Log that can me easy turned-off.
 * Use this Log instead android.Log and before production set Log.flag=false; at app start to disable logs.
 */

public class Log {

	public static boolean flag = true;

	public static void d(String tag, String msg) {
		if (flag)
			android.util.Log.d(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (flag)
			android.util.Log.v(tag, msg);
	}
	
	public static void i(String tag, String msg) {
		if (flag)
			android.util.Log.i(tag, msg);
	}
	
	public static void e(String tag, String msg) {
		if (flag)
			android.util.Log.e(tag, msg);
	}
	
	public static void w(String tag, String msg) {
		if (flag)
			android.util.Log.w(tag, msg);
	}



	
}
