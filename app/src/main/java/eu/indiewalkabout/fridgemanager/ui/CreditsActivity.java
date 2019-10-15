package eu.indiewalkabout.fridgemanager.ui;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.util.ConsentSDK;

public class CreditsActivity extends AppCompatActivity
        implements OnFABMenuSelectedListener {

    // admob banner ref
    private AdView mAdView;

    private FABRevealMenu   fabMenu;
    private FloatingActionButton fab ;

    private Toolbar creditsToolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // load ads banner
        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.Companion.getAdRequest(CreditsActivity.this));


        // get a support action bar
        toolBarInit();

        // navigation fab
        addRevealFabBtn();

        // make bottom navigation bar and status bar hide
        hideStatusNavBars();
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private void hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        View decorView = getWindow().getDecorView();

        // hide status bar
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= 16){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        // hide navigation bar
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Toolbar init
     * ---------------------------------------------------------------------------------------------
     */
    private void toolBarInit(){
        // get the toolbar
        creditsToolbar = findViewById(R.id.credits_toolbar);

        toolbarTitle = creditsToolbar.findViewById(R.id.credits_toolbar_title_tv);
        toolbarTitle.setText(R.string.credits_btn_title);

        // place toolbar in place of action bar
        setSupportActionBar(creditsToolbar);

        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *                                          MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    public void onBackPressed() {
        super.onBackPressed();
        if (fabMenu != null) {
            if (fabMenu.isShowing()) {
                fabMenu.closeMenu();

            }
        }
    }

    public FABRevealMenu getFabMenu() {
        return fabMenu;
    }

    public void setFabMenu(FABRevealMenu fabMenu) {
        this.fabMenu = fabMenu;
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Adding revealing main_fab button
     * ---------------------------------------------------------------------------------------------
     */
    private void addRevealFabBtn(){
        fab     = findViewById(R.id.credits_fab);
        fabMenu = findViewById(R.id.credits_fabMenu);


        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);

                //attach menu to main_fab
                fabMenu.bindAnchorView(fab);

                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        fabMenu.setMenuDirection(Direction.LEFT);

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Revealing main_fab button menu management
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    public void onMenuItemSelected(View view, int id) {
        if (id == R.id.menu_insert) {
            Intent toInsertFood = new Intent(CreditsActivity.this, InsertFoodActivity.class);
            startActivity(toInsertFood);

        } else if (id == R.id.menu_expiring_food) {
            Intent showExpiringFood = new Intent(CreditsActivity.this, FoodListActivity.class);
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);
            startActivity(showExpiringFood);

        } else if (id == R.id.menu_consumed_food) {
            Intent showSavedFood = new Intent(CreditsActivity.this, FoodListActivity.class);
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED);
            startActivity(showSavedFood);

        } else if (id == R.id.menu_dead_food) {
            Intent showDeadFood = new Intent(CreditsActivity.this, FoodListActivity.class);
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD);
            startActivity(showDeadFood);

        } else if (id == R.id.menu_home) {
            Intent returnHome = new Intent(CreditsActivity.this, MainActivity.class);
            startActivity(returnHome);
        }
    }
}
