package com.example.smartpit.adapter;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.fragment.SmartPitFragment;

public class SmartPitPagerAdapter extends FragmentStatePagerAdapter {

	private String TAG = "PageSlideAdapter";
	private ArrayList<SmartPitFragment> list;

	public SmartPitPagerAdapter(FragmentManager fm, ArrayList<SmartPitFragment> list) {
		super(fm);

		this.list = list;

	}

	@Override
	public SherlockFragment getItem(int arg0) {

		return list.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public String getPageTitle(int index) {
		return list.get(index).getClass().getName();

	}

	public Parcelable saveState() {
		// super.saveState();

		Log.d(TAG, "saved state");
		return null;
	}

	public void restoreState(Parcelable a, ClassLoader b) {
		// super.restoreState(a, b);
		Log.d(TAG, "restore state");
	}

}