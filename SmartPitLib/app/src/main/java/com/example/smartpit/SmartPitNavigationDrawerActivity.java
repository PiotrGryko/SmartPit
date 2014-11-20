package com.example.smartpit;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.LinearLayout;

/**
 * Created by piotr on 03.11.14.
 */
public class SmartPitNavigationDrawerActivity extends SmartPitActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout drawerContent;
    private int drawerGravity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.smart_pit_drawer_activity);

        drawerLayout = (DrawerLayout) this.findViewById(R.id.layout_drawer);
        drawerContent = (LinearLayout) this.findViewById(R.id.layout_content);
    }

    public void setDrawerGravity(int gravity) {
        this.drawerGravity = gravity;

        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.FILL_PARENT);
        params.gravity = gravity;
        if (gravity == Gravity.RIGHT)
            drawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_RTL);
        else if (gravity == Gravity.LEFT)
            drawerLayout.setLayoutDirection(DrawerLayout.LAYOUT_DIRECTION_LTR);

        drawerContent.setLayoutParams(params);


    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public LinearLayout getDrawerContent() {
        return drawerContent;
    }

    public void showMenu() {
        if (drawerLayout.isDrawerOpen(drawerGravity))
            drawerLayout.closeDrawer(drawerGravity);
        else
            drawerLayout.openDrawer(drawerGravity);
    }

}
