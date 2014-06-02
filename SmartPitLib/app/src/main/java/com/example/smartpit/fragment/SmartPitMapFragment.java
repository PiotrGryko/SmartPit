package com.example.smartpit.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.example.smartpit.R;
import com.example.smartpit.interfaces.SmartPitChildFragmentInterface;
import com.example.smartpit.widget.Log;
import com.example.smartpit.widget.SmartPitAppHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class SmartPitMapFragment extends SmartPitFragment implements
        SmartPitChildFragmentInterface {

    private String TAG = SmartPitMapFragment.class.getName();

    // private AppDialog alert;
    private GoogleMap map;

    public GoogleMap getMap() {
        return map;
    }

    public void initMap() {

        if (map == null)
            map = ((SupportMapFragment) this.getSherlockActivity()
                    .getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

    }

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
