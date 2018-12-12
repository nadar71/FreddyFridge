package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Date;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.DateConverter;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * ---------------------------------------------------------------------------------------------
 * InsertFoodActivity
 * Get a new food to keep monitored
 * ---------------------------------------------------------------------------------------------
 */
public class InsertFoodActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    private final static String TAG = InsertFoodActivity.class.getSimpleName();

    // flag for open activity in udate mode
    // public static final String UPDATEMODE = "update_mode";

    // key for id item to be changed for extra content when activity invoked in update mode
    public static final String  ID_TO_BE_UPDATED = "id";

    // Extra for the food item ID to be received after device rotation
    public static final String INSTANCE_ID = "instanceFoodId";

    // default value of ID_TO_BE_UPDATED as requested in getIntExtra
    public static final int  DEFAULT_ID = -1;


    // Views ref
    private Button       save_btn;
    private EditText     foodName_et;
    private CalendarView dateExpir_cv;
    private Toolbar      foodInsertToolbar;

    // admob banner ref
    private AdView mAdView;

    // admob banner interstitial ref
    private InterstitialAd mInterstitialAd;

    // Db reference
    private FoodDatabase foodDb;

    // Date picked up reference
    private Calendar datePicked;

    // set the food id as default: will be changed in case of update
    private int foodId = DEFAULT_ID;

    // Object foodEntry to change in case of update
    LiveData<FoodEntry> foodEntryToChange;


    /**
    * ---------------------------------------------------------------------------------------------
    * onCreate
    * @param savedInstanceState
    * ---------------------------------------------------------------------------------------------
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_food);

        // init
        initAds();

        showInterstitialAd();

        // init db instance
        foodDb = FoodDatabase.getsDbInstance(getApplicationContext());

        // init views
        initViews();

        // init toolbar
        toolBarInit();

        // restore the task id after rotation, in case savedInstanceState has been created
        // otherwise it remains DEFAULT_TASK_ID set above
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ID)) {
            foodId = savedInstanceState.getInt(INSTANCE_ID, DEFAULT_ID);
        }

        // check if activity requested in update mode
        checkUpdateModeOn();

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Init and Load admob ads
     * ---------------------------------------------------------------------------------------------
     */
    private void initAds() {

        // load ad banner
        mAdView = findViewById(R.id.adView);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("7DC1A1E8AEAD7908E42271D4B68FB270")
                .build();
        // load and show admob banner
        mAdView.loadAd(adRequest);




        // init and load admob interstitial
        mInterstitialAd = new InterstitialAd(this);

        // Sample AdMob interstitial ID:         ca-app-pub-3940256099942544/1033173712
        // THIS APP REAL AdMob interstitial ID:  ca-app-pub-8846176967909254/5671183832
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            // AdRequest adInterstitialRequest = new AdRequest.Builder().build();

            AdRequest adInterstitialRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("7DC1A1E8AEAD7908E42271D4B68FB270")
                    .build();



            // load and show admob interstitial
            mInterstitialAd.loadAd(adInterstitialRequest);
        }



    }




    /**
     * ---------------------------------------------------------------------------------------------
     * Show an interstitial on ui request
     * ---------------------------------------------------------------------------------------------
     */
    private void showInterstitialAd() {
        initAds();

        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "Ad interstitial did not load", Toast.LENGTH_SHORT).show();
        }
    }



    /**
    * ---------------------------------------------------------------------------------------------
    *  Views init
    * ---------------------------------------------------------------------------------------------
    */
    private void initViews(){
        save_btn     = findViewById(R.id.save_btn);
        foodName_et  = findViewById(R.id.foodName_et);
        dateExpir_cv = findViewById(R.id.calendar_cv);
        dateExpir_cv.setOnDateChangeListener(this);


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveBtnClicked();
            }
        });

    }



    /**
    * ---------------------------------------------------------------------------------------------
    * Toolbar init
    * ---------------------------------------------------------------------------------------------
    */
    private void toolBarInit(){
        // get the toolbar
        foodInsertToolbar = (Toolbar) findViewById(R.id.insert_food_toolbar);
        foodInsertToolbar.setTitle(R.string.insertFoodActivity_title);

        // place toolbar in place of action bar
        setSupportActionBar(foodInsertToolbar);

        // get a support action bar
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Get the date picked up by the user from calendar
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onSelectedDayChange: done");
        datePicked = new GregorianCalendar(year, month, dayOfMonth);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * save insert data in case of rotations
     * @param outState
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_ID, foodId);
        super.onSaveInstanceState(outState);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Check if the activity has been called with update mode active
     * ---------------------------------------------------------------------------------------------
     */
    public void checkUpdateModeOn(){
        // check intent extra
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ID_TO_BE_UPDATED))  {

            Log.d(TAG, "onCreate: Update mode ACTIVE");
            save_btn.setText(R.string.update_btn_label);

            // if id is the default one insert the new to be updated
            if(foodId ==  DEFAULT_ID) {
                foodId =  intent.getIntExtra(ID_TO_BE_UPDATED, DEFAULT_ID);

                // use  LiveData to retrieve food entry attributes
                foodEntryToChange = foodDb.foodDbDao().loadFoodById(foodId);
                // foodEntryToChange = foodDb.foodDbDao().loadFoodById(foodId);

                // observe foodEntryToChange
                foodEntryToChange.observe(this, new Observer<FoodEntry>() {
                    @Override
                    public void onChanged(@Nullable FoodEntry foodEntry) {

                        // dispose observer do not need it no more
                        foodEntryToChange.removeObserver(this);
                        // update UI with new data from db
                        populateUI(foodEntry);
                    }
                });

            }

        }

    }

    /**
    * ---------------------------------------------------------------------------------------------
    * Save/Update Button
    * ---------------------------------------------------------------------------------------------
    */
    public void onSaveBtnClicked(){
        Log.d(TAG, "onSaveBtnClicked");
        final String foodName     = foodName_et.getText().toString();

        // validate entry : name
        if ( foodName.isEmpty() ) {
            Toast.makeText(InsertFoodActivity.this, " Choose a Name...", Toast.LENGTH_SHORT).show();
            return;
        }

        // validate entry : date
        if(foodId == DEFAULT_ID  && datePicked == null) {
            Toast.makeText(InsertFoodActivity.this, " Choose a date...", Toast.LENGTH_SHORT).show();
            return;
        }

        // ----------------------------------------
        // Update db using executor
        // ----------------------------------------
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(foodId == DEFAULT_ID) {     // save a new task
                    Log.d(TAG, "run: Save new food entry");

                    Date   expiringDate       = datePicked.getTime();

                    Log.d(TAG, "foodName : "             + foodName);
                    Log.d(TAG, "expiringDate : "         + expiringDate);

                    // create a new food obj and init with data inserted by user
                    FoodEntry foodEntry = new FoodEntry(0,foodName,expiringDate);

                    // db insert
                    foodDb.foodDbDao().insertFoodEntry(foodEntry);

                    // end activity
                    finish();

                    // restart it for new insertion if any
                    startActivity(getIntent());

                }else{                               // update a previous task
                    Log.d(TAG, "run: update old food entry");

                    // create a new food obj and init with data inserted by user
                    FoodEntry foodEntry;

                    // date picked in case of date change
                    Date   expiringDate;

                    // date get from food selected unchanged
                    long   expiringDate_l;

                    // get date from user selection if new
                    // or the previous date unchanged
                    if (datePicked != null) {
                        expiringDate       = datePicked.getTime();
                        foodEntry = new FoodEntry(0,foodName,expiringDate);
                    } else {
                        expiringDate_l = dateExpir_cv.getDate();
                        foodEntry = new FoodEntry(0,foodName,DateConverter.toDate(expiringDate_l));
                    }

                    // set id to the task to update
                    foodEntry.setId(foodId);

                    // keep if the has been already consumed
                    foodEntry.setDone(foodEntryToChange.getValue().getDone());

                    // update task on db
                    foodDb.foodDbDao().updateFoodEntry(foodEntry);

                    // end activity
                    finish();


                }
            }

        });

        /*
        // confirm to user
        if(foodId == DEFAULT_ID) {
            Toast.makeText(InsertFoodActivity.this, foodName + " Saved !", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(InsertFoodActivity.this, foodName + " Updated !", Toast.LENGTH_SHORT).show();
        }
        */


    }


    /**
     * -----------------------------------------------------------------------------
     * Called when in update mode to re-populate UI
     * @param foodEntry
     * -----------------------------------------------------------------------------
     */
    private void populateUI(FoodEntry foodEntry) {
        if (foodEntry == null){
            return;
        }

        foodName_et.setText(foodEntry.getName());
        dateExpir_cv.setDate(DateConverter.fromDate(foodEntry.getExpiringAt()));
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
            // show interstitial ads
            showInterstitialAd();

            // go home
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
