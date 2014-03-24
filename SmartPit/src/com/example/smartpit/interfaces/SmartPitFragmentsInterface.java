package com.example.smartpit.interfaces;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.SmartPitActivity;

public interface SmartPitFragmentsInterface {

	public void switchFragment(SherlockFragment fragment, boolean removePrevious);

	public void switchTitleFragment(SherlockFragment fragment, boolean removePrevious);

	public void setCurrentFragment(SherlockFragment fragment, boolean removePrevious);

	public Fragment getCurrentFragment();
	
	public String setActionBarLabel(String label, boolean home, boolean facebook);
	
	public FragmentManager getManager();
	
	public Activity getSmartActivity();
	

}
