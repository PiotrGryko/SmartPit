package com.example.smartpit.fragment;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;

public class SmartPitFragment extends SherlockFragment {

	private SmartPitFragmentsInterface listener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (this.getSherlockActivity() instanceof SmartPitFragmentsInterface)
			listener = (SmartPitFragmentsInterface) this.getSherlockActivity();
	}

	public SmartPitFragmentsInterface getFragmentsListener() {
		return listener;
	}

}
