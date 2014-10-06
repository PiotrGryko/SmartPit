package com.example.smartpit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.example.smartpit.widget.SmartPitMenuLayout;
import com.example.smartpit.widget.SmartPitSlidingMenu;

/**
 * Created by piotr on 07.07.14.
 */
public class SmartPitMenuActivity extends SmartPitActivity{

    private SmartPitMenuLayout menuBase;
    private SmartPitSlidingMenu menu;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.smart_menu_activity);


        menuBase = (SmartPitMenuLayout) this.findViewById(R.id.menu_base);
        menu = (SmartPitSlidingMenu) this.findViewById(R.id.menu);
       // menu.getContentLayout().addView(menuView);
       // menu.getContentLayout().setBackgroundColor(Color.WHITE);
       // menu.getContentLayout().setDuration(500);


        menuBase.setMenu(menu);
        menuBase.setVisibility(View.GONE);


        menuBase.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        menuBase.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        menuBase.setDuration(200);


    }

    public SmartPitSlidingMenu getMenu()
    {
        return menu;
    };



    public void showMenu() {

        if (menuBase.getVisibility() == View.GONE)
            menuBase.showMenuBase();
        else
            menuBase.hideMenuBase();

        menuBase.requestFocus();

    }
}
