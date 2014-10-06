package com.example.smartpit.fragment;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;

public class SmartPitMapFragment extends SmartPitFragment implements LocationListener, android.location.LocationListener {


    public static interface OnLocationChangeListener {
        public void onLocationChanged(double latitude, double longitude);
    }

    private String TAG = SmartPitMapFragment.class.getName();
    private LocationManager lm;

    // private AppDialog alert;
    private GoogleMap map;
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private OnLocationChangeListener locationListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    public void onStop() {
        super.onStop();
        mLocationClient.disconnect();
    }


    public GoogleMap getMap() {
        return map;
    }

    public LocationManager getLocationManager() {
        return lm;
    }

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

    private void startLocationUpdates() {


        Log.d(TAG, "start location updates ");

        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60 * 1000, 0, this);
            // loadingDialog = SmartPitAppDialog.getLoadingDialog(AlertDialog.THEME_HOLO_LIGHT, this, this.getString(R.string.dialog_location));
            // loadingDialog.show();
            Log.d(TAG, "start location updates network");

        }
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 0, this);
            //  loadingDialog = SmartPitAppDialog.getLoadingDialog(AlertDialog.THEME_HOLO_LIGHT, this, this.getString(R.string.dialog_location));
            //  loadingDialog.show();
            Log.d(TAG, "start location gps");

        }
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // AppHelper.showSettingsDialog(this);
            Log.d(TAG, "show settings");
        }

        // else


    }


    public void initLocationUpdates(int priority, int interval, int fastestInterval, final OnLocationChangeListener locationListener) {
        this.locationListener = locationListener;
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
               priority);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(interval);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(fastestInterval);

        mLocationClient = new LocationClient(SmartPitMapFragment.this.getSherlockActivity(), new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.d(TAG, "on location client connected");

                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    //   AppHelper.showSettingsDialog(MainActivity.this);

                    return;
                }


                if (mLocationClient != null) {
                    if (mLocationClient.getLastLocation() != null) {

                        locationListener.onLocationChanged(mLocationClient.getLastLocation().getLatitude(), mLocationClient.getLastLocation().getLongitude());


                    }

                    mLocationClient.requestLocationUpdates(mLocationRequest, SmartPitMapFragment.this);
                } else
                    startLocationUpdates();
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "on disconnected");
            }
        }, new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.d(TAG, "on connection failed ");

                startLocationUpdates();


            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "status changed " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "provider enabled " + provider);

        startLocationUpdates();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
