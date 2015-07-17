package pl.gryko.smartpitlib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import pl.gryko.smartpitlib.bitmaps.SmartPitBitmapCache;
import pl.gryko.smartpitlib.bitmaps.SmartPitImageLoader;
import pl.gryko.smartpitlib.cloud.SmartPitGcmIntentService;
import pl.gryko.smartpitlib.cloud.SmartPitRegistrationTask;
import pl.gryko.smartpitlib.fragment.SmartPitBaseFragment;
import pl.gryko.smartpitlib.fragment.SmartPitFragment;
import pl.gryko.smartpitlib.interfaces.SmartPitFragmentsInterface;
import pl.gryko.smartpitlib.widget.SmartPitAppHelper;

/**
 * Main Library class. Each library activity extends this class. Custom activity have to be extended by this class.
 * SmartPitActivity has build in simple SmartPitFragment navigation system. SetFirstFragment ,switchFragment and switchTitleFragment
 * methods allaws to easy navigte through app pages that covers whole screen. Each page schould extend SmartPitFragment class.
 *
 * minimum sample:
 *
 * public class MainActivity extends SmartPitActivity
 * {
 *
 *     public void onCreate(Bundle savedInstanceState)
 *     {
 *
 *         super.onCreate(savedInstanceState)
 *         setContentView(R.id.layout)
 *
 *         setFirstFragment(new SmartPitFragment())
 *
 *
 *     }
 *
 * }
 *
 *
 */
public class SmartPitActivity extends ActionBarActivity implements
        SmartPitFragmentsInterface {

    private String TAG = SmartPitActivity.class.getName();

    private final int SELECT_PHOTO = 909;
    private final int CAMERA_REQUEST = 911;



    private ArrayList<SmartPitFragment> fragmentsList;
    private FragmentManager fm;
    private GoogleCloudMessaging gcm;

    private View customActionbarView;
    private TextView customActionbarLabel;

    private ActionBar ab;

    private static RequestQueue rq;
    private static CookieStore cookieStore;
    private static SmartPitImageLoader mImageLoader;


    /**
     * @param savedInstanceState activity savedInstanceState.
     *
     * OnCreate method initialize fragmentsList, global mImageLoader and volley request queue with
     * configured cookie store
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_activity);


        fm = this.getSupportFragmentManager();


        fragmentsList = new ArrayList<SmartPitFragment>();


        mImageLoader = new SmartPitImageLoader(Volley.newRequestQueue(this),
                SmartPitBitmapCache.getInstance(this));

        BasicHttpParams params = new BasicHttpParams();
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        final SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
        schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        DefaultHttpClient httpclient = new DefaultHttpClient(cm, params);

        cookieStore = new BasicCookieStore();
        httpclient.setCookieStore(cookieStore);

        HttpStack httpStack = new HttpClientStack(httpclient);


        rq = Volley.newRequestQueue(this, httpStack);


    }

    /**
     *
     * @return globalImageLoader
     */
    public static SmartPitImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     *
     * @return configured volley CookieStore
     */

    public static CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     *
     * @returns fragmentManager used to basic navigation
     */
    public FragmentManager getManager() {
        return fm;
    }


    /**
     * this method clears fragmentsBackstacks and back to first fragment
     */
    @Override
    public void clearBackstack() {
        try {
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     *
     * @return Activity
     */
    @Override
    public Activity getSmartActivity() {
        return this;
    }


    /**
     * activity on resume invokes resumeFocus at currenty added fragment. Resolves the issue with disabled fragments after screen blank.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (fragmentsList != null && this.getCurrentFragment() != null)
            this.getCurrentFragment().resumeFocus();

        Log.d(TAG, "on resume");
    }

    /**
     *
     * @return global volley request queue with configured cookie store
     */
    public static RequestQueue getRequestQueue() {
        return rq;
    }

    /**
     * THIS METHOD WILL BE REWRITTEN!
     * @param background
     * @param customView
     * @param label
     */
    public void initActionbar(Drawable background, View customView, TextView label) {
        this.customActionbarView = customView;
        this.customActionbarLabel = label;
        ab = this.getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(customView, new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));
        ab.setBackgroundDrawable(background);

    }

    /**
     * THIS METHOD WILL BE REWRITEEN
     * @param background
     * @param icon
     * @param backIcon
     * @param arrow
     */
    public void initBackstackActionbar(Drawable background, final Drawable icon, final Drawable backIcon, boolean arrow) {
        ab = this.getSupportActionBar();
        ab.setDisplayUseLogoEnabled(true);

        for (int i = 0; i < fragmentsList.size(); i++) {
            final int index = i;
            if (fragmentsList.get(i) instanceof SmartPitBaseFragment) {
                ((SmartPitBaseFragment) fragmentsList.get(i)).setBackstackListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (((SmartPitBaseFragment) fragmentsList.get(index)).getManager().getBackStackEntryCount() > 0) {
                            ab.setLogo(backIcon);
                            ab.setDisplayHomeAsUpEnabled(true);


                        } else
                            ab.setLogo(icon);
                    }
                });
            }
        }
        ab.setBackgroundDrawable(background);

        if (arrow)

            ab.show();

    }

    /**
     * Initialize navigation system and sets first fragment. Should be invoked inside activity onCreate.
     * @param fragment to be set as initial fragment.
     */
    public void setFirstFragment(SmartPitFragment fragment) {

        this.setCurrentFragment(fragment, false);

        fm.beginTransaction().add(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();

    }

    // ///////////this method adds fragment to fragments list.
    // ////////// it replaces dupes to avoid fragments arguments issues

    /**
     * Activity keeps track of currently added fragment. This method is invoked after each fragment transition.
     * @param fragment
     * @param removePrevious
     */
    @Override
    public void setCurrentFragment(SmartPitFragment fragment,
                                   boolean removePrevious) {

        if (removePrevious) {
            for (int i = 0; i < fragmentsList.size(); i++) {
                if (fragment.getClass() == fragmentsList.get(i).getClass()) {
                    fragmentsList.remove(i);
                    fragmentsList.add(fragment);

                    return;
                }

            }
        }
        Log.d(TAG, "new Fragment added to list");
        fragmentsList.add(fragment);
    }

    /**
     * Method wrapped around defaul onBackPressed. Its invoked only when user is exiting activity. Allaws to easly show confirm popup at app kill.
     */

    public void onExit() {
        this.finish();
    }

    /**
     * Modified default onBackPressed. First it invokes currently added fragment onBackPressed() and check that it consumes event. It allaws to easy
     * create custom onBackPressed actions for different pages.
     */
    public void onBackPressed() {


        if (fragmentsList != null && this.getCurrentFragment() != null && !this.getCurrentFragment().onBackPressed()) {
            if (this.getManager().getBackStackEntryCount() == 0)
                onExit();
            else
                super.onBackPressed();
        }
        // else
        //     super.onBackPressed();
    }

    /**
     * return currently added fragment
     * @return
     */

    @Override
    public SmartPitFragment getCurrentFragment() {

        for (int i = 0; i < fragmentsList.size(); i++) {
            if (fragmentsList.get(i).isAdded()) {
                // Log.d(TAG, "setted currentFragment");
                return fragmentsList.get(i);
            }

        }

        return null;
    }

    /**
     * Switch fragments inside activity. Each fragmentTransaction will be added to backstack.
     * @param fragment
     * @param removePrevious
     */
    @Override
    public void switchFragment(SmartPitFragment fragment, boolean removePrevious) {
        try {
            SmartPitFragment oldFragment = getCurrentFragment();

            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_down,
                            R.anim.abc_fade_out,R.anim.slide_up,
                            R.anim.abc_fade_out).remove(oldFragment)
                    .add(R.id.fragment_container, fragment).addToBackStack(null)
                    .commitAllowingStateLoss();

            setCurrentFragment(fragment, removePrevious);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Switch fragments, fragments won`t be added to backstack
     * @param fragment
     * @param removePrevious
     */
    @Override
    public void switchTitleFragment(SmartPitFragment fragment,
                                    boolean removePrevious) {

        try {
            clearBackstack();


            SmartPitFragment oldFragment = getCurrentFragment();

            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_down,
                            R.anim.abc_fade_out).remove(oldFragment)
                    .add(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();

            setCurrentFragment(fragment, removePrevious);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * this method allaws to easly interact in example with activity actionbar based on data from different bages.
     * Each SmartPitFragment contains getLabel() method. When fragment is added, setActionBar method is invoked with
     * fragments getLabel() as argument.
     * @param text comes from currentlyAdded getLabel() method.
     */

    @Override
    public void setActionBarLabel(String text) {

        if (customActionbarView == null || text == null)
            return;

        customActionbarLabel.setText(text);


    }

    /**
     * NEED TO BE TESTED, RELOCATED. This method wrapps google push-notifictaions.
     * @param senderId push senderId
     * @param listener message received listener
     * @param l registration task listener
     */
    public void initGcmService(String senderId, SmartPitGcmIntentService.OnMessageListener listener, SmartPitRegistrationTask.OnRegistrationListener l) {
        if (SmartPitAppHelper.getInstance(this).checkPlayServices(this)) {


            SmartPitGcmIntentService.setOnMessageListener(listener);
            new SmartPitRegistrationTask(this, senderId, l).execute();

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * This method invokes gallery image picker. Result will be available at on of onGallerImagePicked method.
     */

    public void pickImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }
    /**
     * This method invokes Camera image. Result will be available at on of onCameraImagePicked method.
     */

    public void pickImageFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    /**
     * Inoked after picking image from gallery
     * @param uri
     */
    public void onGalleryImagePicked(Uri uri) {

    }
    /**
     * Inoked after picking image from gallery
     * @param file
     */
    public void onGalleryImagePicked(File file) {

    }
    /**
     * Inoked after picking image from gallery
     * @param bitmap
     */
    public void onGalleryImagePicked(Bitmap bitmap) {

    }

    /**
     * Inoked after picking image from camera
     * @param uri
     */
    public void onCameraImagePicked(Uri uri) {

    }
    /**
     * Inoked after picking image from camera
     * @param file
     */
    public void onCameraImagePicked(File file) {

    }
    /**
     * Inoked after picking image from camera
     * @param bitmap
     */
    public void onCameraImagePicked(Bitmap bitmap) {

    }

    /**
     * Helper method. Retunrs scalled - orientated photo from gallery URI.
     * @param photoUri
     * @param MAX_IMAGE_DIMENSION
     * @return
     * @throws IOException
     */
    public Bitmap scaleImage(Uri photoUri, int MAX_IMAGE_DIMENSION) throws IOException {
        InputStream is = getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(photoUri);

        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        String type = getContentResolver().getType(photoUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (type.equals("image/png")) {
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        byte[] bMapArray = baos.toByteArray();
        baos.close();
        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
    }

    /**
     * returns orientation of image in photoUri
     * @param photoUri
     * @return
     */
    public int getOrientation(Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * invoked after picking image from gallery/camera
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                Log.d(TAG, "case 1 " + resultCode);
                if (resultCode == RESULT_OK) {
                    Log.d(TAG,"result ok");
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    File file = new File(filePath);
                    onGalleryImagePicked(file);

                    InputStream input = null;
                    try {
                        input = getContentResolver().openInputStream(selectedImage);


                        Bitmap b = BitmapFactory.decodeStream(input);
                        onGalleryImagePicked(b);

                        onGalleryImagePicked(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
                break;
            case CAMERA_REQUEST:
                Log.d(TAG, "case 2 " + requestCode);
                if (resultCode == RESULT_OK) {


                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    onCameraImagePicked(thumbnail);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");

                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();

                        onCameraImagePicked(destination);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
        }
    }

    @Override
    public int getTab() {
        return -1;
    }

}
