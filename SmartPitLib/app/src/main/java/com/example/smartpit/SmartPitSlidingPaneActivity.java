package com.example.smartpit;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/**
 * Created by piotr on 15.11.14.
 */
public class SmartPitSlidingPaneActivity extends SmartPitActivity{

    private LinearLayout paneContent;
    private SlidingPaneLayout slidingPane;

    protected void onCreate(Bundle state)
    {
        super.onCreate(state);
        this.setContentView(R.layout.smart_pit_sliding_pane_activity);

        paneContent = (LinearLayout)findViewById(R.id.layout_content);
        slidingPane = (SlidingPaneLayout)findViewById(R.id.pane);

       slidingPane.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
           @Override
           public void onGlobalLayout() {
               slidingPane.openPane();
               slidingPane.getViewTreeObserver().removeGlobalOnLayoutListener(this);
           }
       });
    }

    public LinearLayout getPaneContent()
    {
        return paneContent;
    }
}
