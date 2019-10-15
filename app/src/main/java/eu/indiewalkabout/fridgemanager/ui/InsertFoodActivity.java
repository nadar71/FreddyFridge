package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.ads.AdView;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import java.util.ArrayList;
import java.util.Date;

import eu.indiewalkabout.fridgemanager.SingletonProvider;
import eu.indiewalkabout.fridgemanager.FridgeManagerRepository;
import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.DateConverter;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.AppExecutors;
import eu.indiewalkabout.fridgemanager.util.ConsentSDK;
import eu.indiewalkabout.fridgemanager.util.DateUtility;
import eu.indiewalkabout.fridgemanager.util.KeyboardUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * ---------------------------------------------------------------------------------------------
 * InsertFoodActivity
 * Get a new food to keep monitored
 * ---------------------------------------------------------------------------------------------
 */
public class InsertFoodActivity extends AppCompatActivity
        implements CalendarView.OnDateChangeListener, OnFABMenuSelectedListener {

    private final static String TAG = InsertFoodActivity.class.getSimpleName();

    // flag for open activity in udate mode
    // public static final String UPDATEMODE = "update_mode";

    // key for id item to be changed for extra content when activity invoked in update mode
    public static final String  ID_TO_BE_UPDATED = "id";

    // Extra for the food item ID to be received after device rotation
    public static final String INSTANCE_ID = "instanceFoodId";

    // default value of ID_TO_BE_UPDATED as requested in getIntExtra
    public static final int  DEFAULT_ID = -1;

    // unique code for system service request
    private static final int REQ_CODE_SPEECH_INPUT = 100;


    // Views ref
    private Button         save_btn;
    private ImageView      speakerBtn;
    private EditText       foodName_et;
    private CalendarView   dateExpir_cv;
    // private Toolbar        foodInsertToolbar;
    private FABRevealMenu  fabMenu;


    // admob banner ref
    private AdView mAdView;

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


        // load ad banner
        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(InsertFoodActivity.this));

        // init views
        initViews();

        // init toolbar
        // toolBarInit();

        // navigation fab
        addRevealFabBtn();

        // restore the task id after rotation, in case savedInstanceState has been created
        // otherwise it remains DEFAULT_TASK_ID set above
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ID)) {
            foodId = savedInstanceState.getInt(INSTANCE_ID, DEFAULT_ID);
        }

        // check if activity requested in update mode
        checkUpdateModeOn();

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


    @Override
    protected void onResume() {
        super.onResume();
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                Log.d("keyboard", "keyboard visible: "+isVisible);
                if (isVisible == true){
                    mAdView.setVisibility(View.INVISIBLE);
                } else {
                    mAdView.setVisibility(View.VISIBLE);
                }
            }
        });
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
        speakerBtn   = findViewById(R.id.speak_btn);
        dateExpir_cv.setOnDateChangeListener(this);

        // set tomorro selected in calendar widget
        long dateTodayNormalizedAtMidnight  =
                DateUtility.getLocalMidnightFromNormalizedUtcDate(DateUtility.getNormalizedUtcMsForToday());
        // dateExpir_cv.setDate(dateTodayNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS);
        dateExpir_cv.setDate(dateTodayNormalizedAtMidnight);

        // saving editing test click
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveBtnClicked();
            }
        });

        // speaker activation
        speakerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

    }



    /**
    * ---------------------------------------------------------------------------------------------
    * Toolbar init
    * ---------------------------------------------------------------------------------------------
    */
    private void toolBarInit(){
        // get a support action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }





    /**
     * ---------------------------------------------------------------------------------------------
     * Get the date picked up by the user from calendar
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     * ---------------------------------------------------------------------------------------------
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

                // Create the viewModel for the food entry, based on  foodId
                final FoodsViewModel viewModel = ViewModelProviders.of(this).get(FoodsViewModel.class);

                // Populate the text edit fields and
                // observe changes in data through LiveData: getFoodEntry() actually return 1 LiveData<FoodEntry>
                foodEntryToChange = viewModel.getFoodEntry(foodId);
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
            Toast.makeText(InsertFoodActivity.this,
                    getResources().getString(R.string.food_description_alert), Toast.LENGTH_SHORT).show();
            return;
        }

        // validate entry : date
        if(foodId == DEFAULT_ID  && datePicked == null) {
            Toast.makeText(InsertFoodActivity.this, getResources().getString(R.string.food_expdate_alert), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, foodName + getResources().getString(R.string.food_inserted_alert), Toast.LENGTH_SHORT).show();

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

                    // repo insert
                    FridgeManagerRepository repository = ((SingletonProvider) SingletonProvider.getsContext()).getRepository();
                    repository.insertFoodEntry(foodEntry);

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
                    FridgeManagerRepository repository = ((SingletonProvider) SingletonProvider.getsContext()).getRepository();
                    repository.updateFoodEntry(foodEntry);

                    // end activity
                    finish();


                }
            }

        });

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
     * Voice input
     * ---------------------------------------------------------------------------------------------
     */
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    foodName_et.setText(result.get(0));
                }
                break;
            }

        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set datePicked from outside
     * @param calendar
     * ---------------------------------------------------------------------------------------------
     */
    public void setDatePicked(Calendar calendar){
        datePicked = calendar;
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Set dateExpir_cv from outside
     * @param date
     * ---------------------------------------------------------------------------------------------
     */
    public void setDateExpir_cv(Date date){
        dateExpir_cv.setDate(DateConverter.fromDate(date));
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
            // showInterstitialAd();

            // go home
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
        final FloatingActionButton fab = findViewById(R.id.insert_food_fab);
        final FABRevealMenu fabMenu    = findViewById(R.id.insert_food_fabMenu);

        // remove insert menu item
        fabMenu.removeItem(R.id.menu_insert);

        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);

                //attach menu to main_fab
                fabMenu.bindAnchorView(fab);

                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(InsertFoodActivity.this);
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
        if (id == R.id.menu_expiring_food) {
            Intent showExpiringFood = new Intent(InsertFoodActivity.this, FoodListActivity.class);
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);
            startActivity(showExpiringFood);

        } else if (id == R.id.menu_consumed_food) {
            Intent showSavedFood = new Intent(InsertFoodActivity.this, FoodListActivity.class);
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED);
            startActivity(showSavedFood);

        } else if (id == R.id.menu_dead_food) {
            Intent showDeadFood = new Intent(InsertFoodActivity.this, FoodListActivity.class);
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD);
            startActivity(showDeadFood);

        } else if (id == R.id.menu_home) {
            Intent returnHome = new Intent(InsertFoodActivity.this, MainActivity.class);
            startActivity(returnHome);
        }
    }
}
