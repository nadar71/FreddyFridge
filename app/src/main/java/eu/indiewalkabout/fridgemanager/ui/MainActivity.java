package eu.indiewalkabout.fridgemanager.ui;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import java.util.List;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.reminder.ReminderScheduler;
import eu.indiewalkabout.fridgemanager.util.ConsentSDK;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


public class MainActivity extends AppCompatActivity
        implements FoodListAdapter.ItemClickListener, OnFABMenuSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();


    private static final String APP_OPENING_COUNTER ="app-opening-counter";
    private static final int DEFAULT_COUNT          = 0;
    private static final int NUM_MAX_OPENINGS       = 2;


    // admob banner ref
    private AdView mAdView;

    private Application application;

    // UI item reference
    private ImageView settingsBtn, helpBtn;
    private TextView emptyListText;
    private FABRevealMenu fabMenu;
    private FloatingActionButton fab ;

    // recycle View stuff
    private RecyclerView    foodList;
    private FoodListAdapter foodListAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setAppOpenings(0); // debug reset


        // open intro only for the first 3 times
        int numPrevOpenings = getAppOpenings();
        Intent  callingActivity = getIntent();
        boolean comingFromIntro = callingActivity.getBooleanExtra("ComingFromIntro", false);
        if ( (numPrevOpenings < NUM_MAX_OPENINGS) && (!comingFromIntro) ){
            setAppOpenings(numPrevOpenings+1);
            goToIntro();
        }



        // empty view for empty list message
        emptyListText = findViewById(R.id.empty_view);

        settingsBtn = findViewById(R.id.settingsIcon_img);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), MainSettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        helpBtn = findViewById(R.id.help_img);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent introIntent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(introIntent);
            }
        });

        // Initialize ConsentSDK
        ConsentSDK consentSDK = new ConsentSDK.Builder(this)
                .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // redminote 5 // Add your test device id "Remove addTestDeviceId on production!"
                // .addTestDeviceId("9978A5F791A259430A0156313ED9C6A2")
                .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                .build();


        // To check the consent and load ads
        consentSDK.checkConsent(new ConsentSDK.ConsentCallback() {
            @Override
            public void onResult(boolean isRequestLocationInEeaOrUnknown) {
                Log.i("gdpr_TAG", "onResult: isRequestLocationInEeaOrUnknown : "+isRequestLocationInEeaOrUnknown);
                // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
                mAdView.loadAd(ConsentSDK.getAdRequest(MainActivity.this));
            }
        });

        // request ad banner
        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(MainActivity.this));


        // add main_fab revealing menu
        addRevealFabBtn();

        // Recycle view
        initRecycleView();

        // start scheduler for notifications reminder
        ReminderScheduler.scheduleChargingReminder(this);

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
     * Go to intro activity
     * ---------------------------------------------------------------------------------------------
     */
    public void goToIntro(){
        Intent introActivityIntent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(introActivityIntent);
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Get the number of times the app has been opened
     * ---------------------------------------------------------------------------------------------
     */
    private int getAppOpenings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt(APP_OPENING_COUNTER, DEFAULT_COUNT);
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Set the number of times the app has been opened
     * ---------------------------------------------------------------------------------------------
     */
    private void setAppOpenings( int count) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(APP_OPENING_COUNTER, count);
        editor.apply();
    }





    /**
     * *** only for debug notifications
     * @param view
     */
    public void testNotification(View view) {
        // NotificationsUtility.remindNextDaysExpiringFood(this);
    }


    /**
     * *** only for debug notifications
     * @param view
     */
    public void testTodayNotification(View view) {
        // NotificationsUtility.remindTodayExpiringFood(this);
    }


    // Recycle touch an item callback to update/modify task
    @Override
    public void onItemClickListener(int itemId) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.");
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Recycle view list init
     * ---------------------------------------------------------------------------------------------
     */
    private void initRecycleView(){
        // Set the RecyclerView's view
        foodList = findViewById(R.id.main_today_food_list_recycleView);

        // Set LinearLayout
        foodList.setLayoutManager(new LinearLayoutManager(this));

        // retrieve application context for viewmodel
        // application = (Application) getApplicationContext();
        application = getApplication();

        // Initialize the adapter and attach it to the RecyclerView
        foodListAdapter = new FoodListAdapter(this, this,
                FoodListActivity.FOOD_EXPIRING_TODAY);
        foodList.setAdapter(foodListAdapter);

        // Divider decorator
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
        retrieveFoodExpiringToday();
        Log.d(TAG, "setupAdapter: FOOD_TYPE : " + FoodListActivity.FOOD_EXPIRING_TODAY);
    }



    /**
     * ---------------------------------------------------------------------------------------------
     * Livedata/ViewModel recovering Expiring Today Food list
     * ---------------------------------------------------------------------------------------------
     */
    private void retrieveFoodExpiringToday() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB");

        // Log.d(TAG, "Time now : "+System.currentTimeMillis().)



        // Declare my viewModel factory, parametrized with foodlistType
        FoodsViewModelFactory factory =
                new FoodsViewModelFactory(FoodListActivity.FOOD_EXPIRING_TODAY);

        // Create the viewModel for the food list, based on  foodlistType
        final FoodsViewModel viewModel = ViewModelProviders.of(this,factory).get(FoodsViewModel.class);

        // Observe changes in data through LiveData: getFoodList() actually return LiveData<List<FoodEntry>>
        LiveData<List<FoodEntry>> foods = viewModel.getFoodList();
        foods.observe(this, new Observer<List<FoodEntry>>() {
            @Override
            public void onChanged(@Nullable List<FoodEntry> foodEntries) {
                Log.d(TAG, "Receiving database "+ FoodListActivity.FOOD_EXPIRING_TODAY + " LiveData");
                // foodList.setVisibility(View.VISIBLE);
                foodListAdapter.setFoodEntries(foodEntries);
                if (foodEntries.size() > 0 ) {
                    emptyListText.setVisibility(View.INVISIBLE);
                } else {
                    emptyListText.setVisibility(View.VISIBLE);
                }
            }


        });

    }





    // ---------------------------------------------------------------------------------------------
    //                                          MENU STUFF
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent settingsIntent = new Intent(this, MainSettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    public void onBackPressed() {
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
        fab     = findViewById(R.id.main_fab);
        fabMenu = findViewById(R.id.main_fabMenu);

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
            Intent toInsertFood = new Intent(MainActivity.this, InsertFoodActivity.class);
            startActivity(toInsertFood);

        } else if (id == R.id.menu_expiring_food) {
            Intent showExpiringFood = new Intent(MainActivity.this, FoodListActivity.class);
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);
            startActivity(showExpiringFood);

        } else if (id == R.id.menu_consumed_food) {
            Intent showSavedFood = new Intent(MainActivity.this, FoodListActivity.class);
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED);
            startActivity(showSavedFood);

        } else if (id == R.id.menu_dead_food) {
            Intent showDeadFood = new Intent(MainActivity.this, FoodListActivity.class);
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD);
            startActivity(showDeadFood);
        }
    }


}
