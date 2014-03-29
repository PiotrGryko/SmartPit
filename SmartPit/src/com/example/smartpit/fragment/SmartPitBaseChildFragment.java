package com.example.smartpit.fragment;

import android.os.Bundle;

import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.SmartPitAppHelper;

public class SmartPitBaseChildFragment extends SmartPitFragment {

	SmartPitFragmentsInterface listener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (this.getParentFragment() instanceof SmartPitFragmentsInterface)
			listener = (SmartPitFragmentsInterface) this.getParentFragment();
	}

	public void onResume() {
		super.onResume();

		SmartPitAppHelper.getInstance().resumeFocus(this.getView(),
				this.getFragmentsListener());

	}

	public SmartPitFragmentsInterface getFragmentsListener() {
		return listener;
	}
}
