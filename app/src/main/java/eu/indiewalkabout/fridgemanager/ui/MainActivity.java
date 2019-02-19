package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;


import com.google.android.gms.ads.AdView;
// import com.hitomi.cmlibrary.CircleMenu;
// import com.hitomi.cmlibrary.CircleMenuRectMain;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;
import com.hlab.fabrevealmenu.enums.Direction;
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener;
import com.hlab.fabrevealmenu.view.FABRevealMenu;

import java.util.List;

import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.reminder.ReminderScheduler;
import eu.indiewalkabout.fridgemanager.util.ConsentSDK;
import eu.indiewalkabout.fridgemanager.util.NotificationsUtility;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


public class MainActivity extends AppCompatActivity
        implements FoodListAdapter.ItemClickListener, OnFABMenuSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    // admob banner ref
    private AdView mAdView;

    // Db reference
    private FoodDatabase foodDb;

    // UI item reference
    // private CircleMenu circleMenu;
    // private CircleMenuRectMain circleMenuRect;
    private Button temporarySettingsBtn;
    private TextView emptyListText;
    private FABRevealMenu fabMenu;



    // recycle View stuff
    private RecyclerView    foodList;
    private FoodListAdapter foodListAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Db instance
        foodDb = FoodDatabase.getsDbInstance(getApplicationContext());

        // empty view for empty list message
        emptyListText = findViewById(R.id.empty_view);

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


        temporarySettingsBtn = findViewById(R.id.settingsBtn);
        temporarySettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getApplicationContext(), MainSettingsActivity.class);
                startActivity(settingsIntent);
            }
        });



        mAdView = findViewById(R.id.adView);

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(MainActivity.this));



        // -----------------------------------------------------------------------------------------
        // Costum UI : circle menu
        // -----------------------------------------------------------------------------------------
        // Log.d("MainActivity", "Before getting circleMenu from R");
        /*
        circleMenu = (CircleMenu) findViewById(R.id.circle_menu); //

        // Log.d("MainActivity", "Before circleMenu.setMainMenu");

        //circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.mipmap.icon_menu, R.mipmap.icon_cancel)
        circleMenu.setMainMenu(Color.parseColor("#740001"), R.drawable.ic_main_menu_icon, R.mipmap.icon_cancel)
                  .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.icon_home)
                  .addSubMenu(Color.parseColor("#30A400"), R.mipmap.icon_search)
                  .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.icon_notify)
                  .addSubMenu(Color.parseColor("#FF4B32"), R.mipmap.icon_notify)
                  //.addSubMenu(Color.parseColor("#8A39FF"), R.mipmap.icon_setting)
                  //.addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.icon_gps)
                  .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) { // btn numbered in clockwise direction from top
                        switch(index){
                            case 0 :
                                Log.i("Insert New Food ", String.valueOf(index));
                                Intent toInsertFood = new Intent(MainActivity.this, InsertFoodActivity.class);
                                startActivity(toInsertFood);
                                break;


                            case 1 :
                                Log.i("Show Expiring Food ", String.valueOf(index));
                                Intent showExpiringFood = new Intent(MainActivity.this, FoodListActivity.class);
                                showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING);
                                startActivity(showExpiringFood);
                                break;

                            case 2 :
                                Log.i("Show SAVED Food", String.valueOf(index));
                                Intent showSavedFood = new Intent(MainActivity.this, FoodListActivity.class);
                                showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED);
                                startActivity(showSavedFood);
                                break;

                            case 3 :
                                Log.i("Show DEAD Food", String.valueOf(index));
                                Intent showDeadFood = new Intent(MainActivity.this, FoodListActivity.class);
                                showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD);
                                startActivity(showDeadFood);
                                break;


                            default:
                                break;
                        }
                    }

                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {}

            @Override
            public void onMenuClosed() {}

        });
        */

        // add fab revealing menu
        addRevealFabBtn();

        // Recycle view
        initRecycleView();

        // start scheduler for notifications reminder
        ReminderScheduler.scheduleChargingReminder(this);

    }


    /**
     * *** only for debug notifications
     * @param view
     */
    public void testNotification(View view) {
        NotificationsUtility.remindNextDaysExpiringFood(this);
    }


    /**
     * *** only for debug notifications
     * @param view
     */
    public void testTodayNotification(View view) {
        NotificationsUtility.remindTodayExpiringFood(this);
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

        // Initialize the adapter and attach it to the RecyclerView
        foodListAdapter = new FoodListAdapter(this, this,
                FoodListActivity.FOOD_EXPIRING_TODAY);
        foodList.setAdapter(foodListAdapter);

        // Divider decorator
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        foodList.addItemDecoration(decoration);

        // Configure the adpater; it uses LiveData to keep updated on changes
        setupAdapter();

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
     * Livedata/ViewModel recovering Expiring Food list
     * ---------------------------------------------------------------------------------------------
     */
    private void retrieveFoodExpiringToday() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB");

        // Declare my viewModel factory, parametrized with foodlistType
        FoodListsViewModelFactory factory =
                new FoodListsViewModelFactory(foodDb,FoodListActivity.FOOD_EXPIRING_TODAY);

        // Create the viewModel for the food list, based on  foodlistType
        final FoodListsViewModel  viewModel = ViewModelProviders.of(this,factory).get(FoodListsViewModel.class);

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
     * Adding revealing fab button
     * ---------------------------------------------------------------------------------------------
     */
    private void addRevealFabBtn(){
        final FloatingActionButton fab = findViewById(R.id.fab);
        final FABRevealMenu fabMenu = findViewById(R.id.fabMenu);

        try {
            if (fab != null && fabMenu != null) {
                setFabMenu(fabMenu);

                //attach menu to fab
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
     * Revealing fab button menu management
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
