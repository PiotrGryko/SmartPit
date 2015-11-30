package pl.gryko.smartpitlib.widget;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by piotr on 14.05.14.
 *
 * Implementation of MultipartFormData. Can be used for send files on server
 */
public class SmartMultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private static String FILE_PART_NAME = "file";
    private static String STRING_PART_NAME = "text";

    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private HashMap<String, String> params;

    /**
     *
     * @param url server path
     * @param errorListener error listener
     * @param listener success listener
     * @param file File
     * @param fileName request file argument name
     * @param params HashMap of HTTP POST params
     */
    public SmartMultipartRequest(int METHOD, String url, Response.ErrorListener errorListener, Response.Listener<String> listener, File file, String fileName, HashMap<String, String> params) {
        super(METHOD, url, errorListener);

        mListener = listener;
        FILE_PART_NAME = fileName;
        mFilePart = file;
        this.params = params;

        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                try {
                    String key = iterator.next();

                    entity.addPart(key, new StringBody(params.get(key)));
                } catch (UnsupportedEncodingException e) {
                    VolleyLog.e("UnsupportedEncodingException");
                }
            }

        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return Response.success(new String(response.data), getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
