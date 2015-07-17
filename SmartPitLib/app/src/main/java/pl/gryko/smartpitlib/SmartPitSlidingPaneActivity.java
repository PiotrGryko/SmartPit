package pl.gryko.smartpitlib;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import pl.gryko.smartpitlib.fragment.SmartPitFragment;


/**
 * Created by piotr on 15.11.14.
 *
 * SmartPitActivity  with build in SlidingPaneLayout navigation
 *
 *  public class MainActivity extends SmartPitSlidingPaneActivity
 * {
 *
 *     public void onCreate(Bundle savedInstanceState)
 *     {
 *
 *         super.onCreate(savedInstanceState)
 *         setContentView(R.id.layout)
 *
 *         setFirstFragment(new SmartPitFragment())
 *         setPaneFragment(new SmartPitFragment())
 *
 *
 *     }
 *
 * }
 *
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


    /**
     *
     * @return LinearLayout that holds pane fragment or pane ViewContent
     */
    public LinearLayout getPaneContent() {
        return paneContent;
    }

    /**
     * set SmartPitFragment for SlidingPaneLayout
     * @param fragment SmartPitFragment to be setted as SlidingPane content
     */
    public void setPaneFragment(SmartPitFragment fragment) {
        fm.beginTransaction().add(R.id.layout_content, fragment)
                .commitAllowingStateLoss();
    }


}
