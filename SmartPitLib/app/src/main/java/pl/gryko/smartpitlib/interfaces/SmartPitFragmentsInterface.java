package pl.gryko.smartpitlib.interfaces;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import pl.gryko.smartpitlib.fragment.SmartPitFragment;

/**
 * main fragment managment interface. SmartPitActivity or SmartPitBaseFragment impleents these methods.
 */

public interface SmartPitFragmentsInterface {

	public void switchFragment(SmartPitFragment fragment, boolean removePrevious);

	public void switchTitleFragment(SmartPitFragment fragment, boolean removePrevious);

	public void setCurrentFragment(SmartPitFragment fragment, boolean removePrevious);

	public SmartPitFragment getCurrentFragment();
	
	public void setActionBarLabel(String label);
	
	public FragmentManager getManager();

    public void clearBackstack();
	
	public Activity getSmartActivity();



    public int getTab();
	

}
