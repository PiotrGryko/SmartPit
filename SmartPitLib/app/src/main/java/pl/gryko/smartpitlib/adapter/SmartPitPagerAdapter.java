package pl.gryko.smartpitlib.adapter;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import pl.gryko.smartpitlib.fragment.SmartPitBaseFragment;

/**
 * Adapter that is used by ViewPager navigation, in example in SmartPitPagerFragment.
 * Adapter extends FragmentStatePagerAdapter and use SmartPitBaseFragment instead of Fragment.
 */

public class SmartPitPagerAdapter extends FragmentStatePagerAdapter {

	private String TAG = "PageSlideAdapter";
	private ArrayList<SmartPitBaseFragment> list;

	public SmartPitPagerAdapter(FragmentManager fm, ArrayList<SmartPitBaseFragment> list) {
		super(fm);

		this.list = list;

	}

	/**
	 * return Fragment at giver position
	 * @param arg0 index of item to return;
	 * @return Fragment at given index
	 */
	@Override
	public Fragment getItem(int arg0) {

		return list.get(arg0);
	}

	/**
	 * return count of adapter elements
	 * @return int count of elements
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/**
	 * returns class name of element at giver index
	 * @param index of element
	 * @return name of element class at given position
	 */
	public String getPageTitle(int index) {
		return list.get(index).getClass().getName();

	}

	/**
	 * custom save state method
	 * @return Parselable
	 */
	public Parcelable saveState() {
		// super.saveState();

		Log.d(TAG, "saved state");
		return null;
	}

	/**
	 * custom restore state method
	 */
	public void restoreState(Parcelable a, ClassLoader b) {
		// super.restoreState(a, b);
		Log.d(TAG, "restore state");
	}

}