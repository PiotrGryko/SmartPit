package pl.gryko.smartpitlib.bitmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import pl.gryko.smartpitlib.widget.SmartImageView;
import pl.gryko.smartpitlib.widget.SmartPitAppHelper;

/**
 * Default implementation of SmartPitImageLoader.SmartImagesListener. It shows error image at
 * loading error. If loading is successful it sets bitmap to ImageView, and shows result on screen with fading animation.
 */
public class SmartPitImagesListener implements SmartPitImageLoader.SmartImagesListener {

    private String TAG = "MyImageListener";
    private String filename;

    private SmartImageView imageView;

    Context context;

    public SmartPitImagesListener(Context context, String filename,
                                  SmartImageView image) {

        this.context = context;
        this.filename = filename;

        this.imageView = image;

        this.imageView.setTag(this.filename);

    }

    @Override
    public void onErrorResponse(VolleyError arg0) {
        // TODO Auto-generated method stub

        if (imageView != null)
            imageView.showErrorImage();


    }

    @Override
    public void onResponse(SmartPitImageLoader.SmartImageContainer arg0, boolean arg1) {
        // TODO Auto-generated method stub

        Log.d(TAG, "onResponse " + Boolean.toString(arg1));

        final Bitmap b = arg0.getBitmap();

        if (imageView != null) {
            if (imageView.getTag() == filename) {
                imageView.setVisibility(View.GONE);
                imageView.setImageBitmap(b);
                SmartPitAppHelper.showViewWithAnimation(imageView,300);
            }
        }


    }
}
