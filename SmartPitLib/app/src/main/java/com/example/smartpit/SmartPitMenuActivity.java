package com.example.smartpit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.example.smartpit.fragment.SmartPitBaseFragment;
import com.example.smartpit.fragment.SmartPitFragment;
import com.example.smartpit.widget.SmartPitMenuLayout;
import com.example.smartpit.widget.SmartPitSlidingMenu;

import java.util.ArrayList;

/**
 * Created by piotr on 07.07.14.
 * <p/>
 * Child activity can add child views by getMenu().addView(View v).
 * <p/>
 * menuChildView.onClick(View v){
 * FragmentExample.getFragmentsListener().switchFragment(NextFragment fragment, boolean removePrevious)
 * <p/>
 * }
 */
public abstract class SmartPitMenuActivity extends SmartPitActivity {

    private SmartPitMenuLayout menuBase;
    private SmartPitSlidingMenu menu;
    private ArrayList<SmartPitBaseFragment> fragmentsList;


    public static enum MenuType {LEFT, RIGHT}


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.smart_menu_activity);


        menuBase = (SmartPitMenuLayout) this.findViewById(R.id.menu_base);
        menu = (SmartPitSlidingMenu) this.findViewById(R.id.menu);


        // menu.getContentLayout().addView(menuView);
        // menu.getContentLayout().setBackgroundColor(Color.WHITE);
        // menu.getContentLayout().setDuration(500);


    }

    public void initMenu(MenuType type) {

        menuBase.setMenu(menu, type);
        menuBase.setVisibility(View.GONE);


        menuBase.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        menuBase.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        menuBase.setDuration(200);
        menuBase.hideMenuBase();


    }


    public SmartPitSlidingMenu getMenu() {
        return menu;
    }

    ;


    public void showMenu() {

        if (menuBase.getVisibility() == View.GONE)
            menuBase.showMenuBase();
        else
            menuBase.hideMenuBase();

        menuBase.requestFocus();

    }
}
