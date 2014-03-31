package com.example.smartpit.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.example.smartpit.adapter.SmartPitPagerAdapter;
import com.example.smartpit.widget.SmartPitViewPager;

public class SmartPitPagerFragment extends SmartPitFragment {

	private SmartPitPagerAdapter pagerAdapter;

	private TabHost host;
	private SmartPitViewPager viewPager;

	public void setPagerAndHost(View v, boolean swapable, int pagerId, int pagesLimit) {
		viewPager = (SmartPitViewPager) v.findViewById(pagerId);
		viewPager.setOffscreenPageLimit(pagesLimit);

		viewPager.setSwap(swapable);

		host = (TabHost) v.findViewById(android.R.id.tabhost);
		host.setup();
	}

	public TabHost getHost() {
		return host;
	}

	public SmartPitViewPager getPager() {
		return viewPager;
	}

	public void setAdapter(ArrayList<SmartPitFragment> fragmentsList) {
		/*
		 * TabHost.TabSpec spec = null;
		 * 
		 * for (int i = 0; i < fragmentsList.size(); i++) { spec = host
		 * .newTabSpec(Integer.toString(i)) .setIndicator(
		 * createTabIndicator(this.getSherlockActivity(), "dg")) .setContent(new
		 * TabContent(this.getSherlockActivity())); host.addTab(spec); }
		 */
		pagerAdapter = new SmartPitPagerAdapter(this.getSherlockActivity()
				.getSupportFragmentManager(), fragmentsList);

		new SetAdapterTask().execute();
	}

	private class SetAdapterTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;

		}

		protected void onPostExecute(Void result) {
			viewPager.setAdapter(pagerAdapter);
		}

	}

	

}
