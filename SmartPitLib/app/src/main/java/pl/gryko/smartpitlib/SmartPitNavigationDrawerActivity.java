package pl.gryko.smartpitlib;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.LinearLayout;

import pl.gryko.smartpitlib.fragment.SmartPitFragment;


/**
 * Created by piotr on 03.11.14.
 *
 * SmartPitActivity with included NavigationDrawer
 *
 * minimal sample:
 *
 * public class MainActivity extends SmartPitNavigationDrawerActivity
 * {
 *
 *     public void onCreate(Bundle savedInstanceState)
 *     {
 *
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.id.layout);
 *
 *         setDrawerGravity(Gravity.right)
 *
 *         setFirstFragment(new SmartPitFragment());
 *         setDrawerFragment(new SmartPitFragment());
 *
 *     }
 *
 * }
 */


public class SmartPitNavigationDrawerActivity extends SmartPitActivity {

    public DrawerLayout drawerLayout;
    public LinearLayout drawerContent;
    private int drawerGravity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.smart_pit_drawer_activity);

        drawerLayout = (DrawerLayout) this.findViewById(R.id.layout_drawer);
        drawerContent = (LinearLayout) this.findViewById(R.id.layout_content);
    }

    /**
     * sets fragment for NavigationDrawer
     * @param fragment SmartPitFragment to be setted as NavigatonDrawer
     */
    public void setDrawerFragment(SmartPitFragment fragment) {
        this.getSupportFragmentManager().beginTransaction().add(R.id.layout_content, fragment)
                .commitAllowingStateLoss();

    }

    /**
     * Invoke to change fragment at NavigationDrawer
     * @param fragment SmartPitFragment to replace
     */
    public void switchDrawerFragment(SmartPitFragment fragment) {
        if (!fragment.isAdded())
            this.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in,
                            R.anim.alpha_out, R.anim.alpha_in,
                            R.anim.alpha_out).remove(getCurrentDrawerFragment()).add(R.id.layout_content, fragment)
                    .commitAllowingStateLoss();

    }

    /**
     * Returns currently added NavgiationDrawer SmartPitFragment
     * @return SmartPitFragment
     */
    public SmartPitFragment getCurrentDrawerFragment() {
        SmartPitFragment f = (SmartPitFragment) this.getSupportFragmentManager().findFragmentById(R.id.layout_content);
        return f;
    }

    /**
     * Sets gravity for NavigationDrawer Gravity.right and Gravity.left are supported
     * @param gravity Gravity for sliding content
     */
    public void setDrawerGravity(int gravity) {
        this.drawerGravity = gravity;

        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.FILL_PARENT);
        params.gravity = gravity;




        //if (gravity == Gravity.RIGHT)
        //    drawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_RTL);
       // else if (gravity == Gravity.LEFT)
       //     drawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_LTR);


        drawerContent.setLayoutParams(params);


    }

    /**
     * return current DrawerLayout
     * @return DrawerLayout
     */
    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    /**
     * return LinearLayout that holds NavigationDrawer SmartPitFragment or content View
     * @return  LinearLayout
     */
    public LinearLayout getDrawerContent() {
        return drawerContent;
    }

    /**
     * close/open navigation drawer
     */
    public void showMenu() {

        if (drawerLayout.isDrawerOpen(drawerGravity))
            drawerLayout.closeDrawer(drawerGravity);
        else
            drawerLayout.openDrawer(drawerGravity);
    }

}
