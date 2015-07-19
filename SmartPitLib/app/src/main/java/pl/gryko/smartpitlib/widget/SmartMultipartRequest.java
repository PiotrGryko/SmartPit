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

/**
 * Created by piotr on 14.05.14.
 *
 * Implementation of MultipartFormData. Can be used for send files on server
 *
 */
public class SmartMultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private static  String FILE_PART_NAME = "file";
    private static  String STRING_PART_NAME = "text";

    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private final String mStringPart;

    /**
     * Constructor
     * @param url String url to perform request
     * @param errorListener ErrorListener for error response
     * @param listener SuccessListener for success response
     * @param file File to send
     * @param fileMame String name to name file argument.
     */
    public SmartMultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, File file, String fileMame) {
        super(Method.PUT, url, errorListener);

        mListener = listener;
        mFilePart = file;
        mStringPart = fileMame;
        FILE_PART_NAME = fileMame;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
       // try {
       //     entity.addPart(STRING_PART_NAME, new StringBody(mStringPart));
       // } catch (UnsupportedEncodingException e) {
        //    VolleyLog.e("UnsupportedEncodingException");
        //}
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
        return Response.success("Uploaded", getCacheEntry());
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
