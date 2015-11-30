package pl.gryko.smartpitlib.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;



import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.gryko.smartpitlib.widget.SmartPitCameraPreview;

/**
 * Created by piotr on 06.05.14.
 *
 * SmartPitFragment with wrapped QR reader. Default implementation shows camera preview on layout. When qr code is visible,
 * camera caches focus, reads qr code and returns String data on parseData(String data) method.
 *
 * minimal sample:
 *
 * public class QrFragment extends SmartPitQrFragment
 * {
 *
 *     public View onCreateView(LayoutInfalter inflater, ViewGroup parent, Bundle savedInstanceState)
 *     {
 *         View v = inflater.inflate(R.layout.smart_qr_fragment, parent, false);
 *         initView();
 *
 *         return v;
 *     }
 * }
 *
 */
public abstract class SmartPitQrFragment extends SmartPitFragment{

    private Camera mCamera;
    private SmartPitCameraPreview mPreview;
    private Handler autoFocusHandler;

    ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private boolean initialized=false;

    public abstract void parseData(String data);

    /**
     * this line loads native QR libary
     */
    static {
        System.loadLibrary("iconv");
    }

    /**
     * inits SmartPitCameraPreview
     */
    public void initView()
    {

        this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        mPreview = new SmartPitCameraPreview(this.getActivity(), mCamera, previewCb, autoFocusCB);
        initialized=true;

    }

    /**
     * returns SmartPitCameraPreview instance
     * @return  SmartPitCameraPreview instance
     */
    public SmartPitCameraPreview getPreviewInstance()
    {
        return mPreview;
    }

    /**
     * use this method to restart camera after qrcode read.
     */
    public void startCamera()
    {

        if (barcodeScanned) {

            barcodeScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
    }




    public void onDestroyView() {
        super.onDestroyView();
        releaseCamera();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            autoFocusHandler.removeCallbacks(doAutoFocus);
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {

                    barcodeScanned = true;
                    parseData((sym.getData()));



                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };




    @Override
    public String getLabel() {
        return null;
    }

}
