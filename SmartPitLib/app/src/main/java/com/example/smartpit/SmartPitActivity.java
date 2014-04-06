package com.example.smartpit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.smartpit.bitmaps.SmartPitBitmapCache;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.schedule.SmartPitScheduleDataReceiver;
import com.example.smartpit.schedule.SmartPitScheduledIntentService;
import com.example.smartpit.widget.SmartPitAppHelper;

public class SmartPitActivity extends SherlockFragmentActivity implements
		SmartPitFragmentsInterface {

	private String TAG = SmartPitActivity.class.getName();

	private ArrayList<SherlockFragment> fragmentsList;
	private FragmentManager fm;
	private static ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smart_activity);

		SmartPitAppHelper.initAppHelper(this);

		
		fm = this.getSupportFragmentManager();

		
		fragmentsList = new ArrayList<SherlockFragment>();

		mImageLoader = new ImageLoader(Volley.newRequestQueue(this),
				SmartPitBitmapCache.getInstance(this));

	}

	public static ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public void setFirstFragment(SherlockFragment fragment) {

		this.setCurrentFragment(fragment, false);

		fm.beginTransaction().add(R.id.fragment_container, fragment)
				.commitAllowingStateLoss();

	}

	public FragmentManager getManager() {
		return fm;
	}

	// ///////////this method add fragment to fragments list.
	// ////////// it replaces dupes to avoid fragments arguments issues
	@Override
	public void setCurrentFragment(SherlockFragment fragment,
			boolean removePrevious) {

		if (removePrevious) {
			for (int i = 0; i < fragmentsList.size(); i++) {
				if (fragment.getClass() == fragmentsList.get(i).getClass()) {
					fragmentsList.remove(i);
					fragmentsList.add(fragment);

					return;
				}

			}
		}
		Log.d(TAG, "new Fragment added to list");
		fragmentsList.add(fragment);
	}

	// /////return currently added fragment
	@Override
	public SherlockFragment getCurrentFragment() {

		for (int i = 0; i < fragmentsList.size(); i++) {
			if (fragmentsList.get(i).isAdded()) {
				// Log.d(TAG, "setted currentFragment");
				return fragmentsList.get(i);
			}

		}

		return null;
	}

	//
	// ////////////////////replace current fragment with argument fragment,
	// /////////////// transition with in/out animations added to backstack
	@Override
	public void switchFragment(SherlockFragment fragment, boolean removePrevious) {
		SherlockFragment oldFragment = getCurrentFragment();

		fm.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right,
						R.anim.slide_out_left, android.R.anim.slide_in_left,
						android.R.anim.slide_out_right).remove(oldFragment)
				.add(R.id.fragment_container, fragment).addToBackStack(null)
				.commitAllowingStateLoss();

		setCurrentFragment(fragment, removePrevious);
	}

	// //////////method switch title fragment, transition with in animation not
	// added to backstack
	@Override
	public void switchTitleFragment(SherlockFragment fragment,
			boolean removePrevious) {
		SherlockFragment oldFragment = getCurrentFragment();

		fm.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right,
						R.anim.slide_out_left, android.R.anim.slide_in_left,
						android.R.anim.slide_out_right).remove(oldFragment)
				.add(R.id.fragment_container, fragment)
				.commitAllowingStateLoss();

		setCurrentFragment(fragment, removePrevious);

	}

	@Override
	public String setActionBarLabel(String label, boolean home, boolean facebook) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Activity getSmartActivity() {
		// TODO Auto-generated method stub
		return this;
	}

    public void initScheduledService(int delay,  SmartPitScheduledIntentService.ScheduleTaskListener listener)
    {

        SmartPitScheduledIntentService.setTaskListener(listener);
        SmartPitScheduleDataReceiver.setDelay(delay);
        this.sendBroadcast(new Intent(this, SmartPitScheduleDataReceiver.class));

    }

    public void onDestroy()
    {
        super.onDestroy();
        SmartPitScheduleDataReceiver.stopService();
    }

    @Override
    public int getTab() {
        return 0;
    }

}
