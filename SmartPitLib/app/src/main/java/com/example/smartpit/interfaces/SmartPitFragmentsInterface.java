package com.example.smartpit.interfaces;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.SmartPitActivity;
import com.example.smartpit.fragment.SmartPitFragment;

public interface SmartPitFragmentsInterface {

	public void switchFragment(SmartPitFragment fragment, boolean removePrevious);

	public void switchTitleFragment(SmartPitFragment fragment, boolean removePrevious);

	public void setCurrentFragment(SmartPitFragment fragment, boolean removePrevious);

	public SmartPitFragment getCurrentFragment();
	
	public String setActionBarLabel(String label, boolean home, boolean facebook);
	
	public FragmentManager getManager();
	
	public Activity getSmartActivity();

    public int getTab();
	

}
