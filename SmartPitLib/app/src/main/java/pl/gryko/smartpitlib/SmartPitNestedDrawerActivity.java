package pl.gryko.smartpitlib;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.LinearLayout;

import pl.gryko.smartpitlib.fragment.SmartPitFragment;


/**
 * Created by piotr on 08.04.15.
 */
public class SmartPitNestedDrawerActivity extends SmartPitActivity {

    private DrawerLayout masterDrawerLayout;
    private LinearLayout masterDrawerContent;

    private DrawerLayout drawerLayout;
    private LinearLayout drawerContent;
    private int drawerGravity;
    private int masterDrawerGravity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.smart_pit_nested_drawer_activity);

        masterDrawerLayout = (DrawerLayout) this.findViewById(R.id.layout_drawer_master);
        masterDrawerContent = (LinearLayout) this.findViewById(R.id.layout_master_content);


        drawerLayout = (DrawerLayout) this.findViewById(R.id.layout_drawer);
        drawerContent = (LinearLayout) this.findViewById(R.id.layout_content);
    }

    public void setDrawerFragment(SmartPitFragment fragment) {
        this.getSupportFragmentManager().beginTransaction().add(R.id.layout_content, fragment)
                .commitAllowingStateLoss();

    }

    public void switchDrawerFragment(SmartPitFragment fragment) {
        if (!fragment.isAdded())
            this.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in,
                            R.anim.alpha_out, R.anim.alpha_in,
                            R.anim.alpha_out).remove(getCurrentDrawerFragment()).add(R.id.layout_content, fragment)
                    .commitAllowingStateLoss();

    }

    public void setMasterDrawerFragment(SmartPitFragment fragment) {
        this.getSupportFragmentManager().beginTransaction().add(R.id.layout_master_content, fragment)
                .commitAllowingStateLoss();

    }

    public void switchMasterDrawerFragment(SmartPitFragment fragment) {
        if (!fragment.isAdded())
            this.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.alpha_in,
                            R.anim.alpha_out, R.anim.alpha_in,
                            R.anim.alpha_out).remove(getCurrentMasterDrawerFragment()).add(R.id.layout_master_content, fragment)
                    .commitAllowingStateLoss();

    }



    public void switchMasterDrawerTitleFragment(SmartPitFragment fragment) {
        if (!fragment.isAdded())
            this.getSupportFragmentManager().beginTransaction().addToBackStack(null)
                    .setCustomAnimations(R.anim.alpha_in,
                            R.anim.alpha_out, R.anim.alpha_in,
                            R.anim.alpha_out).remove(getCurrentMasterDrawerFragment()).add(R.id.layout_master_content, fragment)
                    .commitAllowingStateLoss();

    }

    public SmartPitFragment getCurrentDrawerFragment() {
        SmartPitFragment f = (SmartPitFragment) this.getSupportFragmentManager().findFragmentById(R.id.layout_content);
        return f;
    }

    public SmartPitFragment getCurrentMasterDrawerFragment() {
        SmartPitFragment f = (SmartPitFragment) this.getSupportFragmentManager().findFragmentById(R.id.layout_master_content);
        return f;
    }

    public void setDrawerGravity(int gravity) {
        this.drawerGravity = gravity;

        DrawerLayout.LayoutParams masterParams = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.FILL_PARENT);

        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.FILL_PARENT);
        params.gravity = gravity;
        if (gravity == Gravity.RIGHT) {
            masterDrawerGravity = Gravity.LEFT;
           // drawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_RTL);
           // masterDrawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_LTR);
        } else if (gravity == Gravity.LEFT) {
            masterDrawerGravity = Gravity.RIGHT;

           // drawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_LTR);
           // masterDrawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_RTL);
        }
        masterParams.gravity = masterDrawerGravity;


        drawerContent.setLayoutParams(params);
        masterDrawerContent.setLayoutParams(masterParams);


    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public LinearLayout getDrawerContent() {
        return drawerContent;
    }

    public DrawerLayout getMasterDrawerLayout() {
        return masterDrawerLayout;
    }

    public LinearLayout getMasterDrawerContent() {
        return masterDrawerContent;
    }

    public void showMenu() {

        if (drawerLayout.isDrawerOpen(drawerGravity))
            drawerLayout.closeDrawer(drawerGravity);
        else
            drawerLayout.openDrawer(drawerGravity);
    }

    public void showMasterMenu() {

        if (masterDrawerLayout.isDrawerOpen(masterDrawerGravity))
            masterDrawerLayout.closeDrawer(masterDrawerGravity);
        else
            masterDrawerLayout.openDrawer(masterDrawerGravity);
    }


}
