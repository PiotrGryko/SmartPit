package com.example.smartpit.fragment;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.example.smartpit.R;
import com.example.smartpit.interfaces.SmartPitChildFragmentInterface;
import com.example.smartpit.widget.Log;
import com.example.smartpit.widget.SmartPitAppHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;

public class SmartPitMapFragment extends SmartPitFragment implements
        SmartPitChildFragmentInterface, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {



    private String TAG = SmartPitMapFragment.class.getName();
    private LocationManager lm;

    // private AppDialog alert;
    private GoogleMap map;
    private LocationClient mLocationClient;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient(this.getSherlockActivity(), this, this);

    }
    public void onStart()
    {
        super.onStart();
        mLocationClient.connect();
    }
    public void onStop()
    {
        super.onStop();
        mLocationClient.disconnect();
    }


    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this.getSherlockActivity(),
                        92);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        }
    }


    public GoogleMap getMap() {
        return map;
    }
    public LocationManager getLocationManager(){return lm;}

    public void initMap() {

        if (map == null)
            map = ((SupportMapFragment) this.getSherlockActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

    }


/*
    public void setMyLocationListenerEnabled(OnLocationChanged listener) {
        if(this.getMap()==null)
            return;
        this.getMap().setMyLocationEnabled(true);
        this.getMap().getUiSettings().setMyLocationButtonEnabled(true);
        lm = (LocationManager)this.getSherlockActivity().getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, this);
        else
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);


        this.listener = listener;
    }
*/
    public void onDestroyView() {
        super.onDestroyView();

        try {
            Fragment fragment = (this.getSherlockActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map));
            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
        } catch (Throwable t) {
            Log.d(TAG, t.toString());
        }
      //  if(this.listener!=null&& lm!=null)
       //     lm.removeUpdates(this);
        map = null;

    }

    public void stripView() {
        // TODO Auto-generated method stub
        if (this.getView() != null) {
            Log.d("CLEAR", "clearview");
            SmartPitAppHelper.getInstance(this.getSherlockActivity()).stripViewGroup(this.getView(), false);

        } else
            Log.d("CLEAR", "view null");

        System.gc();

    }

    public String getLabel() {
        return "";
    }



}
