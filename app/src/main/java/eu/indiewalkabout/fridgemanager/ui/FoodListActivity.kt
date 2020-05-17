package eu.indiewalkabout.fridgemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.view.FABRevealMenu
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.data.FoodEntry
import eu.indiewalkabout.fridgemanager.ui.FoodListActivity
import eu.indiewalkabout.fridgemanager.ui.FoodListAdapter.ItemClickListener
import eu.indiewalkabout.fridgemanager.ui.FoodsViewModel
import eu.indiewalkabout.fridgemanager.ui.InsertFoodActivity
import eu.indiewalkabout.fridgemanager.ui.MainActivity
import eu.indiewalkabout.fridgemanager.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.util.GenericUtility.randRange_ApiCheck

/**
 * ---------------------------------------------------------------------------------------------
 * FoodListActivity
 * Show list of food depending on FOOD_TYPE
 * ---------------------------------------------------------------------------------------------
 */
class FoodListActivity : AppCompatActivity(), ItemClickListener, OnFABMenuSelectedListener {
    // Views ref
    var foodsListToolbar: Toolbar? = null
    lateinit var foodList: androidx.recyclerview.widget.RecyclerView
    var foodListAdapter: FoodListAdapter? = null
    lateinit var emptyListText: TextView
    lateinit var toolbarTitle: TextView
    var fabMenu: FABRevealMenu? = null
    var fab: FloatingActionButton? = null

    // admob banner ref
    lateinit var mAdView: AdView

    // admob banner interstitial ref
    lateinit var mInterstitialAd: InterstitialAd

    // Hold the type of food to show in list, use here and by adapter
    // TODO : find a better way to pass this info to adapter
    private var foodlistType: String? = null

    /**
     * ---------------------------------------------------------------------------------------------
     * onCreate
     * @param savedInstanceState
     * ---------------------------------------------------------------------------------------------
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)
        mAdView = findViewById(R.id.adView)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(getAdRequest(this@FoodListActivity))


        // empty view for empty list message
        emptyListText = findViewById(R.id.empty_view)


        // TODO : use this in another way
        // get intent extra for configuring list type
        val intent = intent
        if (intent != null && intent.hasExtra(FOOD_TYPE)) {
            foodlistType = intent.getStringExtra(FOOD_TYPE)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + foodlistType.toString())
        }

        // init toolbar
        toolBarInit()

        // navigation fab btn
        addRevealFabBtn()

        // init recycle view list
        initRecycleView()

        // make bottom navigation bar and status bar hide
        hideStatusNavBars()
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        val decorView = window.decorView

        // hide status bar
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else if (Build.VERSION.SDK_INT >= 16) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        // hide navigation bar
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }

    // Recycle touch an item callback to update/modify task
    override fun onItemClickListener(itemId: Int) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.")
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Toolbar init
     * ---------------------------------------------------------------------------------------------
     */
    private fun toolBarInit() {

        // get the toolbar
        foodsListToolbar = findViewById<View>(R.id.food_list_toolbar) as Toolbar
        setToolBarTitle()

        // place toolbar in place of action bar
        setSupportActionBar(foodsListToolbar)

        // get a support action bar
        val actionBar = supportActionBar

        // up button
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Set Toolbar title
     * ---------------------------------------------------------------------------------------------
     */
    private fun setToolBarTitle() {
        toolbarTitle = foodsListToolbar!!.findViewById(R.id.toolbar_title_tv)

        // set correct title
        if (foodlistType == FOOD_EXPIRING) {
            // foodsListToolbar.setTitle(R.string.foodExpiring_activity_title);
            toolbarTitle.setText(R.string.foodExpiring_activity_title)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodExpiring_activity_title)

        } else if (foodlistType == FOOD_SAVED) {
            // foodsListToolbar.setTitle(R.string.R.string.foodSaved_activity_title);
            toolbarTitle.setText(R.string.foodSaved_activity_title)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodSaved_activity_title)

        } else if (foodlistType == FOOD_DEAD) {
            // foodsListToolbar.setTitle(R.string.foodDead_activity_title);
            toolbarTitle.setText(R.string.foodDead_activity_title)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodDead_activity_title)
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Show an interstitial on ui request
     * ---------------------------------------------------------------------------------------------
     */
    private fun showInterstitialAd() {


        /*
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

        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "Ad interstitial did not load", Toast.LENGTH_SHORT).show();
        }
        */
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.admob_key_interstitial)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mInterstitialAd.loadAd(getAdRequest(this))
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Toast.makeText(FoodListActivity.this, "Ad Interstitial LOADED", Toast.LENGTH_SHORT).show();
                // Show interstitial
                mInterstitialAd.show()
                super.onAdLoaded()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                // Toast.makeText(FoodListActivity.this, "Ad Interstitial   FAILED TO LOAD", Toast.LENGTH_SHORT).show();
                // Log.i(TAG, "onAdFailedToLoad: ConsentSDK.getAdRequest(MainActivity.this) : "
                //        +ConsentSDK.getAdRequest(MainActivity.this));
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Recycle view list init
     * ---------------------------------------------------------------------------------------------
     */
    private fun initRecycleView() {
        // Set the RecyclerView's view
        foodList = findViewById(R.id.food_list_recycleView)
        if (foodList == null) {
            Log.d(TAG, "onCreate: foodList == null ")
        }

        // Set LinearLayout
        foodList.setLayoutManager(LinearLayoutManager(this))


        // Initialize the adapter and attach it to the RecyclerView
        foodListAdapter = FoodListAdapter(this, this, foodlistType!!)
        foodList.setAdapter(foodListAdapter)

        // Divider decorator
        val decoration = DividerItemDecoration(applicationContext, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
        foodList.addItemDecoration(decoration)

        // Configure the adpater; it uses LiveData to keep updated on changes
        setupAdapter()

        // make fab button hide when scrolling list
        foodList.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && fab!!.isShown) fab!!.hide()
            }

            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) fab!!.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Used to reload from db the tasks list and update the list view in screen
     * ---------------------------------------------------------------------------------------------
     */
    private fun setupAdapter() {
        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ")
        retrieveAllFood()
        Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Livedata/ViewModel recovering Expiring Food list
     * ---------------------------------------------------------------------------------------------
     */
    private fun retrieveAllFood() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB")

        // Instance factory FoodsViewModelFactory, parametrized with foodlistType
        val factory = FoodsViewModelFactory(foodlistType!!)

        // Create the FoodsViewModel  for the food list, based on  foodlistType
        val viewModel = ViewModelProviders.of(this, factory).get(FoodsViewModel::class.java)

        // Observe changes in data through LiveData: getFoodList() actually return LiveData<List<FoodEntry>>
        val foods: LiveData<MutableList<FoodEntry>>? = viewModel.foodList
        foods!!.observe(this, Observer<MutableList<FoodEntry>?> { foodEntries ->
            Log.d(TAG, "Receiving database $foodlistType LiveData")
            foodListAdapter!!.setFoodEntries(foodEntries)
            if (foodEntries!!.size > 0) {
                emptyListText.visibility = View.INVISIBLE
            } else {
                emptyListText.visibility = View.VISIBLE
            }
        })
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {
            // show interstitial ad on back home only 50% of times
            val guess = randRange_ApiCheck(1, 10)
            if (guess <= 4) {
                showInterstitialAd()
            }

            // showInterstitialAd();
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()
        if (fabMenu != null) {
            if (fabMenu!!.isShowing) {
                fabMenu!!.closeMenu()
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Adding revealing main_fab button
     * ---------------------------------------------------------------------------------------------
     */
    private fun addRevealFabBtn() {
        fab = findViewById(R.id.food_list_fab)
        fabMenu = findViewById(R.id.food_list_fabMenu)

        // choose what menu item must be seen based on type of list
        if (foodlistType == FOOD_EXPIRING) {
            fabMenu?.removeItem(R.id.menu_expiring_food)

        } else if (foodlistType == FOOD_DEAD) {
            fabMenu?.removeItem(R.id.menu_dead_food)

        } else if (foodlistType == FOOD_SAVED) {
            fabMenu?.removeItem(R.id.menu_consumed_food)
        }
        try {
            if (fab != null && fabMenu != null) {
                fabMenu = fabMenu

                //attach menu to main_fab
                fabMenu!!.bindAnchorView(fab!!)

                //set menu selection listener
                fabMenu!!.setOnFABMenuSelectedListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fabMenu?.setMenuDirection(Direction.LEFT)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Revealing main_fab button menu management
     * ---------------------------------------------------------------------------------------------
     */
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_insert) {
            val toInsertFood = Intent(this@FoodListActivity, InsertFoodActivity::class.java)
            startActivity(toInsertFood)
        } else if (id == R.id.menu_expiring_food) {
            val showExpiringFood = Intent(this@FoodListActivity, FoodListActivity::class.java)
            showExpiringFood.putExtra(FOOD_TYPE, FOOD_EXPIRING)
            startActivity(showExpiringFood)
        } else if (id == R.id.menu_consumed_food) {
            val guess = randRange_ApiCheck(1, 10)
            if (guess <= 3) {
                showInterstitialAd()
            }
            val showSavedFood = Intent(this@FoodListActivity, FoodListActivity::class.java)
            showSavedFood.putExtra(FOOD_TYPE, FOOD_SAVED)
            startActivity(showSavedFood)
        } else if (id == R.id.menu_dead_food) {
            val guess = randRange_ApiCheck(1, 10)
            if (guess <= 3) {
                showInterstitialAd()
            }
            val showDeadFood = Intent(this@FoodListActivity, FoodListActivity::class.java)
            showDeadFood.putExtra(FOOD_TYPE, FOOD_DEAD)
            startActivity(showDeadFood)
        } else if (id == R.id.menu_home) {
            // show interstitial ad on back home only 50% of times
            val guess = randRange_ApiCheck(1, 10)
            if (guess <= 4) {
                showInterstitialAd()
            }

            // showInterstitialAd();
            val returnHome = Intent(this@FoodListActivity, MainActivity::class.java)
            startActivity(returnHome)
        }
    }

    companion object {
        val TAG = FoodListActivity::class.java.simpleName

        // Key constant to use as key for intent extra
        // get the type of list to show from intent content extra
        const val FOOD_TYPE = "food_type"

        // Values to use for passing intent extras in key/value pair
        // expiring food flag
        const val FOOD_EXPIRING = "ExpiringFood"

        // expiring food Today flag
        const val FOOD_EXPIRING_TODAY = "ExpiringFoodToday"

        // saved food flag
        const val FOOD_SAVED = "SavedFood"

        // dead food flag
        const val FOOD_DEAD = "DeadFood"
    }
}