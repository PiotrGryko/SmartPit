package pl.gryko.smartpitlib.cloud;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * class for testings gcm
 */

public class SmartPitGcmSender {

	private static String TAG = SmartPitGcmSender.class.getName();

	private static String API = "https://android.googleapis.com/gcm/send";


	public static void senMessage(Context context,
			Response.Listener successListener,
			Response.ErrorListener errorListener) {

		String request = API + "/api/products";

		Log.d(TAG, request);

		StringRequest jr = new StringRequest(Request.Method.GET, request,
				successListener, errorListener);

		// jr.setRetryPolicy(new DefaultRetryPolicy(900000,
		// DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
		// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		RequestQueue rq = Volley.newRequestQueue(context);

		rq.add(jr);
		rq.start();

	}

}
