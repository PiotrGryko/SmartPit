package com.example.smartpit.fragment;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.interfaces.SmartPitChildFragmentInterface;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.SmartPitAppHelper;

public class SmartPitFragment extends SherlockFragment implements
		SmartPitChildFragmentInterface {

	private SmartPitFragmentsInterface listener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (this.getSherlockActivity() instanceof SmartPitFragmentsInterface)
			listener = (SmartPitFragmentsInterface) this.getSherlockActivity();
	}

	public SmartPitFragmentsInterface getFragmentsListener() {
		return listener;
	}

	@Override
	public void stripView() {
		if (this.getView() != null) {
			SmartPitAppHelper.stripViewGroup(this.getView(), false);

			System.gc();

		}

	}

}
