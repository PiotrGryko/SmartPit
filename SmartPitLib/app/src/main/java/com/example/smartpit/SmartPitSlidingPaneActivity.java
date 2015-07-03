package com.example.smartpit;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.example.smartpit.fragment.SmartPitFragment;
import com.example.smartpit.widget.SmartPitAppDialog;

/**
 * Created by piotr on 15.11.14.
 */
public class SmartPitSlidingPaneActivity extends SmartPitActivity {

    private LinearLayout paneContent;
    private SlidingPaneLayout slidingPane;
    private FragmentManager fm;
    private ActionBar ab;


    protected void onCreate(Bundle state) {
        super.onCreate(state);
        this.setContentView(R.layout.smart_pit_sliding_pane_activity);

        fm = this.getSupportFragmentManager();
        ab= this.getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        paneContent = (LinearLayout) findViewById(R.id.layout_content);
        slidingPane = (SlidingPaneLayout) findViewById(R.id.pane);

        slidingPane.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                slidingPane.closePane();
                slidingPane.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onBackPressed();

                // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
                break;

        }

        return true;
    }



    public LinearLayout getPaneContent() {
        return paneContent;
    }

    public void setPaneFragment(SmartPitFragment fragment) {
        fm.beginTransaction().add(R.id.layout_content, fragment)
                .commitAllowingStateLoss();
    }


}
