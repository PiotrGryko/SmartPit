package pl.gryko.smartpitlib;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;



import java.util.ArrayList;

import pl.gryko.smartpitlib.fragment.SmartPitBaseFragment;
import pl.gryko.smartpitlib.widget.SmartPitMenuLayout;
import pl.gryko.smartpitlib.widget.SmartPitSlidingMenu;

/**
 *
 * DEPRECATED use SmartPitNavigationDrawerActivity instead!
 *
 * Custom sliding menu. Created before NavigationDrawer.
 *
 *
 * minimal sample:
 * public class MainActivity extends SmartPitMenuActivity
 * {
 *
 *     public void onCreate(Bundle savedInstanceState)
 *     {
 *
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.id.layout);
 *
 *         setFirstFragment(new SmartPitFragment());
 *         initMenu(MenuType.LEFT);
 *         getMenu().addView(menu layout view);
 *     }
 *
 * }
 *
 * To close/open menu use showMenu()
 *
 *
 *
 */

public abstract class SmartPitMenuActivity extends SmartPitActivity {

    private SmartPitMenuLayout menuBase;
    private SmartPitSlidingMenu menu;
    private ArrayList<SmartPitBaseFragment> fragmentsList;


    public static enum MenuType {LEFT, RIGHT}

    /**
     *
     * @param savedInstanceState activity savedInstanceState.
     *
     * OnCreate method. Should invoke initMenu(MenuType.TYPE), getMenu().addView(View menu)
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.smart_menu_activity);


        menuBase = (SmartPitMenuLayout) this.findViewById(R.id.menu_base);
        menu = (SmartPitSlidingMenu) this.findViewById(R.id.menu);



        // menu.getContentLayout().addView(menuView);
        // menu.getContentLayout().setBackgroundColor(Color.WHITE);
        // menu.getContentLayout().setDuration(500);


    }

    /**
     * Initialize sliding menu
     * @param type MenuType enum value. Accepts MenuType.LEFT or MenuType.RIGHT
     */
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


    /**
     *
     * @return returns SmartPitSlidingMenu.
     */
    public SmartPitSlidingMenu getMenu() {
        return menu;
    }


    /**
     * open/close sliding menu
     */
    public void showMenu() {

        if (menuBase.getVisibility() == View.GONE)
            menuBase.showMenuBase();
        else
            menuBase.hideMenuBase();

        menuBase.requestFocus();

    }
}
