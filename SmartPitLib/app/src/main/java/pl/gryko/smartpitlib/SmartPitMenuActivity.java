package pl.gryko.smartpitlib;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;



import java.util.ArrayList;

import pl.gryko.smartpitlib.fragment.SmartPitBaseFragment;
import pl.gryko.smartpitlib.widget.SmartPitMenuLayout;
import pl.gryko.smartpitlib.widget.SmartPitSlidingMenu;


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
