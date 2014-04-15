package com.example.smartpit.fragment;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.smartpit.interfaces.SmartPitChildFragmentInterface;
import com.example.smartpit.interfaces.SmartPitFragmentsInterface;
import com.example.smartpit.widget.Log;
import com.example.smartpit.widget.SmartPitAppHelper;

public class SmartPitFragment extends SherlockFragment implements
		SmartPitChildFragmentInterface {

	private SmartPitFragmentsInterface listener;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


        if (this.getParentFragment()!=null &&  (this.getParentFragment() instanceof SmartPitFragmentsInterface))
         listener = (SmartPitFragmentsInterface) this.getParentFragment();


		else if (this.getSherlockActivity() instanceof SmartPitFragmentsInterface)
        listener = (SmartPitFragmentsInterface) this.getSherlockActivity();


	}


    public void onResume()
    {
        super.onResume();

        /*
        If SmartPitPagerFragment and nested fragment are used, resume focus only for currently visible tab

         */

        if(SmartPitPagerFragment.getHost()!=null)
        {
            if(this.getFragmentsListener().getTab()==SmartPitPagerFragment.getHost().getCurrentTab())
            {
                resumeFocus();

            }
        }
        else

            resumeFocus();
    }

    public void resumeFocus()
    {
        SmartPitAppHelper.getInstance(this.getSherlockActivity()).resumeFocus(this.getView(),
                this.getFragmentsListener());
    }

	public SmartPitFragmentsInterface getFragmentsListener() {
		return listener;
	}

	@Override
	public void stripView() {
		if (this.getView() != null) {
			SmartPitAppHelper.getInstance(this.getSherlockActivity()).stripViewGroup(this.getView(), false);

			System.gc();

		}

	}

}
