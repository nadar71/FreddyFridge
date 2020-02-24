package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;


import java.util.List;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


import eu.indiewalkabout.fridgemanager.util.ConsentSDK;
import eu.indiewalkabout.fridgemanager.util.GenericUtility;


/**
* ---------------------------------------------------------------------------------------------
* FoodListActivity
* Show list of food depending on FOOD_TYPE
* ---------------------------------------------------------------------------------------------
*/
public class FoodListActivity extends AppCompatActivity
        implements FoodListAdapter.ItemClickListener, OnFABMenuSelectedListener {

    public static final String TAG = FoodListActivity.class.getSimpleName();

    private Toolbar         foodsListToolbar;
    private RecyclerView    foodList;
    private FoodListAdapter foodListAdapter;
    private TextView        emptyListText, toolbarTitle;
    private FABRevealMenu   fabMenu;
    private FloatingActionButton fab ;


    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    // Key constant to use as key for intent extra
    // get the type of list to show from intent content extra
    public static final String FOOD_TYPE           = "food_type";

    // Values to use for passing intent extras in key/value pair
    // expiring food flag
    public static final String FOOD_EXPIRING       = "ExpiringFood";

    // expiring food Today flag
    public static final String FOOD_EXPIRING_TODAY = "ExpiringFoodToday";

    // consumed food flag
    public static final String FOOD_SAVED          = "SavedFood";

    // expired food flag
    public static final String FOOD_DEAD           = "DeadFood";

    // Hold the type of food to show in list, use here and by adapter
    // TODO : find a better way to pass this info to adapter
    private String foodlistType;





    /**
    * ---------------------------------------------------------------------------------------------
    * onCreate
    * @param savedInstanceState
    * ---------------------------------------------------------------------------------------------
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        mAdView = findViewById(R.id.adView);

        mAdView.loadAd(ConsentSDK.getAdRequest(FoodListActivity.this));

        // empty view for empty list message
        emptyListText = findViewById(R.id.empty_view);


        // TODO : use this in another way
        // get intent extra for configuring list type
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(FOOD_TYPE)) {
            foodlistType = intent.getStringExtra(FOOD_TYPE);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + foodlistType.toString());
        }

        // init toolbar
        toolBarInit();

        // navigation fab btn
        addRevealFabBtn();

        // init recycle view list
        initRecycleView();

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



    // Recycle touch an item callback to update/modify task
    @Override
    public void onItemClickListener(int itemId) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.");
    }




    /**
    * ---------------------------------------------------------------------------------------------
    * Toolbar init
    * ---------------------------------------------------------------------------------------------
    */
    private void toolBarInit() {
        // get the toolbar
        foodsListToolbar = (Toolbar) findViewById(R.id.food_list_toolbar);

        setToolBarTitle();

        // place toolbar in place of action bar
        setSupportActionBar(foodsListToolbar);

        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        // up button
        actionBar.setDisplayHomeAsUpEnabled(true);

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set Toolbar title
     * ---------------------------------------------------------------------------------------------
     */
    private void setToolBarTitle() {
        toolbarTitle = foodsListToolbar.findViewById(R.id.toolbar_title_tv);

        // set correct title
        if (foodlistType.equals(FOOD_EXPIRING)) {
            // foodsListToolbar.setTitle(R.string.foodExpiring_activity_title);
            toolbarTitle.setText(R.string.foodExpiring_activity_title);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodExpiring_activity_title);

        } else if (foodlistType.equals(FOOD_SAVED)) {
            // foodsListToolbar.setTitle(R.string.R.string.foodSaved_activity_title);
            toolbarTitle.setText(R.string.foodSaved_activity_title);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodSaved_activity_title);

        } else if (foodlistType.equals(FOOD_DEAD)) {
            // foodsListToolbar.setTitle(R.string.foodDead_activity_title);
            toolbarTitle.setText(R.string.foodDead_activity_title);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodDead_activity_title);

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Show an interstitial on ui request
     * ---------------------------------------------------------------------------------------------
     */
    private void showInterstitialAd() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_key_interstitial));

        mInterstitialAd.loadAd(ConsentSDK.getAdRequest(this));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // for debug :
                // Toast.makeText(FoodListActivity.this, "Ad Interstitial LOADED", Toast.LENGTH_SHORT).show();
                // Show interstitial
                mInterstitialAd.show();
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // for debug :
                // Toast.makeText(FoodListActivity.this, "Ad Interstitial   FAILED TO LOAD", Toast.LENGTH_SHORT).show();
                // Log.i(TAG, "onAdFailedToLoad: ConsentSDK.getAdRequest(MainActivity.this) : "
                //        +ConsentSDK.getAdRequest(MainActivity.this));
            }
        });

    }

    /**
    * ---------------------------------------------------------------------------------------------
    * Recycle view list init
    * ---------------------------------------------------------------------------------------------
    */
    private void initRecycleView(){
        foodList = findViewById(R.id.food_list_recycleView);

        if (foodList == null) {
            Log.d(TAG, "onCreate: foodList == null ");
        }

        foodList.setLayoutManager(new LinearLayoutManager(this));


        // Init adapter
        foodListAdapter = new FoodListAdapter(this, this, foodlistType);
        foodList.setAdapter(foodListAdapter);

        // Divider
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        foodList.addItemDecoration(decoration);

        // Configure the adpater; it uses LiveData to keep updated on changes
        setupAdapter();

        // make fab button hide when scrolling list
        foodList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }






    /**
     * ---------------------------------------------------------------------------------------------
     * Used to reload from db the tasks list and update the list view in screen
     * ---------------------------------------------------------------------------------------------
     */
    private void setupAdapter() {

        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ");
        retrieveAllFood();
        Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);


    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Livedata/ViewModel recovering Expiring Food list
     * ---------------------------------------------------------------------------------------------
     */
    private void retrieveAllFood() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB");

        // Instance factory FoodsViewModelFactory, parametrized with foodlistType
        FoodsViewModelFactory factory = new FoodsViewModelFactory(foodlistType);

        // Create the FoodsViewModel  for the food list, based on  foodlistType
        final FoodsViewModel viewModel = ViewModelProviders.of(this,factory).get(FoodsViewModel.class);

        // Observe changes in data through LiveData: getFoodList() actually return LiveData<List<FoodEntry>>
        LiveData<List<FoodEntry>> foods = viewModel.getFoodList();
        foods.observe(this, new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                Log.d(TAG, "Receiving database "+ foodlistType + " LiveData");
                foodListAdapter.setFoodEntries(foodEntries);
                if (foodEntries.size() > 0 ) {
                    emptyListText.setVisibility(View.INVISIBLE);
                } else {
                    emptyListText.setVisibility(View.VISIBLE);
                }
            }
        });

    }





    /**
    * ----------------------------------------------------------------------------------------------
    *                                          MENU STUFF
    * ----------------------------------------------------------------------------------------------
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {
            // show interstitial ad on back home only 50% of times
            int guess = GenericUtility.randRange_ApiCheck(1,10);
            if (guess <=4) {
                showInterstitialAd();
            }

            // showInterstitialAd();
            onBackPressed();
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
        fab     = findViewById(R.id.food_list_fab);
        fabMenu = findViewById(R.id.food_list_fabMenu);

        // choose what menu item must be seen based on type of list
        if (foodlistType.equals(FoodListActivity.FOOD_EXPIRING)){
            fabMenu.removeItem(R.id.menu_expiring_food);

        } else if (foodlistType.equals(FoodListActivity.FOOD_DEAD)){
            fabMenu.removeItem(R.id.menu_dead_food);

        } else if (foodlistType.equals(FoodListActivity.FOOD_SAVED)){
            fabMenu.removeItem(R.id.menu_consumed_food);
        }

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
            Intent toInsertFood = new Intent(FoodListActivity.this, InsertFoodActivity.class);
            startActivity(toInsertFood);

        } else if (id == R.id.menu_expiring_food) {
            Intent showExpiringFood = new Intent(FoodListActivity.this, FoodListActivity.class);
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);
            startActivity(showExpiringFood);

        } else if (id == R.id.menu_consumed_food) {
            int guess = GenericUtility.randRange_ApiCheck(1,10);
            if (guess <=3) {
                showInterstitialAd();
            }
            Intent showSavedFood = new Intent(FoodListActivity.this, FoodListActivity.class);
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED);
            startActivity(showSavedFood);

        } else if (id == R.id.menu_dead_food) {
            int guess = GenericUtility.randRange_ApiCheck(1,10);
            if (guess <=3) {
                showInterstitialAd();
            }
            Intent showDeadFood = new Intent(FoodListActivity.this, FoodListActivity.class);
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD);
            startActivity(showDeadFood);

        } else if (id == R.id.menu_home) {
            // show interstitial ad on back home only 50% of times

            int guess = GenericUtility.randRange_ApiCheck(1,10);
            if (guess <=4) {
                showInterstitialAd();
            }

            // showInterstitialAd();

            Intent returnHome = new Intent(FoodListActivity.this, MainActivity.class);
            startActivity(returnHome);
        }
    }

}
