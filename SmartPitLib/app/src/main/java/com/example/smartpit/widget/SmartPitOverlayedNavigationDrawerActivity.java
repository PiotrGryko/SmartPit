package com.example.smartpit.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.smartpit.R;
import com.example.smartpit.SmartPitActivity;

/**
 * Created by piotr on 05.11.14.
 */
public class SmartPitOverlayedNavigationDrawerActivity extends SmartPitActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout drawerContent;
    private int drawerGravity;

    public void onCreate(Bundle savedInstanceState) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.smart_pit_drawer_activity, null); // "null" is important.

        drawerLayout = (DrawerLayout)drawer.findViewById(R.id.layout_drawer);
        drawerContent = (LinearLayout)drawer.findViewById(R.id.layout_content);

        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawerLayout.findViewById(R.id.fragment_container); // This is the container we defined just now.
        container.addView(child);

        // Make the drawer replace the first child
        decor.addView(drawer);

        super.onCreate(savedInstanceState);

       // this.setContentView(R.layout.smart_activity);




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
