package com.example.smartpit.fragment;

import android.os.Bundle;

import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.Log;
import com.example.smartpit.widget.SmartPitAppHelper;

public class SmartPitBaseChildFragment extends SmartPitFragment {

	SmartPitFragmentsInterface listener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (this.getParentFragment() instanceof SmartPitFragmentsInterface)
			listener = (SmartPitFragmentsInterface) this.getParentFragment();
	}


    public void onResume()
    {
        super.onResume();

        if(SmartPitPagerFragment.getHost()!=null)
        {

            if(this.getFragmentsListener().getTab()==SmartPitPagerFragment.getHost().getCurrentTab())
                resumeFocus();
        }
        else
            resumeFocus();
    }

    public void resumeFocus()
    {
        SmartPitAppHelper.getInstance().resumeFocus(this.getView(),
                this.getFragmentsListener());
    }

	public SmartPitFragmentsInterface getFragmentsListener() {
		return listener;
	}
}
