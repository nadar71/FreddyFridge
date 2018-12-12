package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.List;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


import eu.indiewalkabout.fridgemanager.util.DateUtility;



/**
* ---------------------------------------------------------------------------------------------
* FoodListActivity
* Show list of food depending on FOOD_TYPE
* ---------------------------------------------------------------------------------------------
*/
public class FoodListActivity extends AppCompatActivity implements FoodListAdapter.ItemClickListener  {

    public static final String TAG = FoodListActivity.class.getSimpleName();

    // Views ref
    private Toolbar         foodsListToolbar;
    private RecyclerView    foodList;
    private FoodListAdapter foodListAdapter;

    // admob banner ref
    private AdView mAdView;

    // Hold food entries data
    private List<FoodEntry> foodEntries ;

    // Date utility class instance
    private DateUtility dateUtility;

    // Db reference
    private FoodDatabase foodDb;

    // Key constant to use as key for intent extra
    // get the type of list to show from intent content extra
    public static final String FOOD_TYPE     = "food_type";

    // Values to use for passing intent extras in key/value pair
    // expiring food flag
    public static final String FOOD_EXPIRING = "ExpiringFood";

    // saved food flag
    public static final String FOOD_SAVED    = "SavedFood";

    // dead food flag
    public static final String FOOD_DEAD     = "DeadFood";

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

        // load ad banner
        mAdView = findViewById(R.id.adView);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("7DC1A1E8AEAD7908E42271D4B68FB270")
                .build();
        mAdView.loadAd(adRequest);

        // Db instance
        foodDb = FoodDatabase.getsDbInstance(getApplicationContext());

        // TODO : use this in another way
        // get intent extra for configuring list type
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(FOOD_TYPE)) {
            foodlistType = intent.getStringExtra(FOOD_TYPE);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + foodlistType.toString());
        }

        // init toolbar
        toolBarInit();

        // init recycle view list
        // TODO : accepts a parameter for the type of list
        initRecycleView();
    }


    /**
     * with livedata, this is not no more necessary
     */
    /*
    @Override
    protected void onResume() {
        super.onResume();
        // setupAdapter();
    }
    */

    // Recycle touch an item callback to update/modify task
    @Override
    public void onItemClickListener(int itemId) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.");
    }




    /**
    * ---------------------------------------------------------------------------------------------
    * Toolbar init
    * TODO : check if possible to MAKE it AN EXTERNAL UTIL FUNCTIONS
    * ---------------------------------------------------------------------------------------------
    */
    private void toolBarInit() {

        // get the toolbar
        foodsListToolbar = (Toolbar) findViewById(R.id.food_list_toolbar);

        // set correct title
        if (foodlistType.equals(FOOD_EXPIRING)) {
            foodsListToolbar.setTitle(R.string.foodExpiring_activity_title);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodExpiring_activity_title);

        } else if (foodlistType.equals(FOOD_SAVED)) {
            foodsListToolbar.setTitle(R.string.foodSaved_activity_title);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodSaved_activity_title);

        } else if (foodlistType.equals(FOOD_DEAD)) {
            foodsListToolbar.setTitle(R.string.foodDead_activity_title);
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodDead_activity_title);

        }


        // place toolbar in place of action bar
        setSupportActionBar(foodsListToolbar);

        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        // up button
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    /**
    * ---------------------------------------------------------------------------------------------
    * Recycle view list init
    * ---------------------------------------------------------------------------------------------
    */
    private void initRecycleView(){
        // Set the RecyclerView's view
        foodList = (RecyclerView) findViewById(R.id.food_list_recycleView);

        if (foodList == null) {
            Log.d(TAG, "onCreate: foodList == null ");
        }

        // Set LinearLayout
        foodList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        foodListAdapter = new FoodListAdapter(this, this, foodlistType);
        foodList.setAdapter(foodListAdapter);

        // Divider decorator
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        foodList.addItemDecoration(decoration);

        // Configure the adpater; it uses LiveData to keep updated
        setupAdapter();

    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Used to reload from db the tasks list and update the list view in screen
     * ---------------------------------------------------------------------------------------------
     */
    private void setupAdapter() {

        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ");

        // List<FoodEntry> foodEntries = null;

        // choose the type of food to load from db
        if (foodlistType.equals(FOOD_EXPIRING)) {
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            // foodEntries = foodDb.foodDbDao().loadAllFoodExpiring(dateUtility.getNormalizedUtcMsForToday());
            retrieveAllFoodExpiring();

        }else if (foodlistType.equals(FOOD_SAVED)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            //foodEntries = foodDb.foodDbDao().loadAllFoodSaved();
            retrieveAllSavedFood();

        }else if (foodlistType.equals(FOOD_DEAD)){
            Log.d(TAG, "setupAdapter: FOOD_TYPE : " + foodlistType);
            // foodEntries = foodDb.foodDbDao().loadAllFoodDead(dateUtility.getNormalizedUtcMsForToday());
            retrieveAllDeadFood();

        }


    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Livedata recovering Expiring Food list
     * ---------------------------------------------------------------------------------------------
     */
    private void retrieveAllFoodExpiring() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB");

        // set livedata to this query
        LiveData<List<FoodEntry>> foods = foodDb.foodDbDao().loadAllFoodExpiring(dateUtility.getNormalizedUtcMsForToday());

        // setup the observer for these data
        setUpObserver(foods, "Expiring Food");
        /*
        // observe changes in data
        foods.observe(this, new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                Log.d(TAG, "Receiving database "+ "Expiring Food" + " LiveData");
                foodListAdapter.setFoodEntries(foodEntries);
            }
        });
        */
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Livedata recovering Saved Food list
     * ---------------------------------------------------------------------------------------------
     */
    private void retrieveAllSavedFood() {
        Log.d(TAG, "Actively retrieving Saved Food from DB");

        // set livedata to this query
        LiveData<List<FoodEntry>> foods = foodDb.foodDbDao().loadAllFoodSaved();

        // setup the observer for these data
        setUpObserver(foods, "Saved Food");
        // observe changes in data
        /*
        foods.observe(this, new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                Log.d(TAG, "Receiving database "+ "Saved Food" + " LiveData");
                foodListAdapter.setFoodEntries(foodEntries);
            }
        });
        */
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Livedata recovering Dead Food list
     * ---------------------------------------------------------------------------------------------
     */
    private void retrieveAllDeadFood() {
        Log.d(TAG, "Actively retrieving Dead Food from DB");

        // set livedata to this query
        LiveData<List<FoodEntry>> foods = foodDb.foodDbDao().loadAllFoodDead(dateUtility.getNormalizedUtcMsForToday());

        // setup the observer for these data
        setUpObserver(foods, "Dead Food");
        /*
        // observe changes in data
        foods.observe(this, new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                Log.d(TAG, "Receiving database "+ "Dead Food" + " LiveData");
                foodListAdapter.setFoodEntries(foodEntries);
            }
        });
        */
    }


    /**
     * setUpObserver : recall when LiveData target the data, to set an observer which observe on data changes
     * @param foods
     * @param foodTypeTag
     */
    private void setUpObserver(LiveData<List<FoodEntry>> foods, String foodTypeTag){

        // convert to static to use in inner class
        final String foodTag = foodTypeTag;

        // observe changes in data
        foods.observe(this, new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                Log.d(TAG, "Receiving database "+ foodTag + " LiveData");
                foodListAdapter.setFoodEntries(foodEntries);
            }
        });

    }

    /**
    * ---------------------------------------------------------------------------------------------
    *                                          MENU STUFF
    * ---------------------------------------------------------------------------------------------
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * EXPERIMENT
     * ---------------------------------------------------------------------------------------------
     */

    /*
    public  void onClickBtnDelete(View v) {
        Toast.makeText(this, "Delete Button pressed", Toast.LENGTH_SHORT).show();
    }
    */
}
