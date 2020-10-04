package eu.indiewalkabout.fridgemanager.ui

import android.app.Application
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdView
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.view.FABRevealMenu
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.reminder.ReminderScheduler.scheduleChargingReminder
import eu.indiewalkabout.fridgemanager.ui.FoodListAdapter.ItemClickListener
import eu.indiewalkabout.fridgemanager.util.ConsentSDK
import eu.indiewalkabout.fridgemanager.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.util.ConsentSDK.ConsentCallback

class MainActivity : AppCompatActivity(), ItemClickListener, OnFABMenuSelectedListener {
    // admob banner ref
    lateinit var mAdView: AdView
    lateinit var applicationRef: Application

    // UI item reference
    lateinit var settingsBtn: ImageView
    lateinit var helpBtn: ImageView
    lateinit var emptyListText: TextView
    var fabMenu: FABRevealMenu? = null
    private var fab: FloatingActionButton? = null

    // recycle View stuff
    lateinit var foodList: androidx.recyclerview.widget.RecyclerView
    lateinit var foodListAdapter: FoodListAdapter

    // vars utils for testing
    private var numPrevOpenings = 0

    /**
     * ---------------------------------------------------------------------------------------------
     * Return the reference to ConsentSDK instance to beused by test classes
     * ---------------------------------------------------------------------------------------------
     * @return
     */
    var consentObjReference: ConsentSDK? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // open intro only for the first 3 times
        numPrevOpenings = appOpenings

        // set consent sdk for gdpr true by default
        // setConsentSDKNeed(true);
        val callingActivity = intent
        val comingFromIntro = callingActivity.getBooleanExtra("ComingFromIntro", false)
        if (numPrevOpenings < NUM_MAX_OPENINGS && !comingFromIntro) {
            appOpenings = numPrevOpenings + 1
            goToIntro()
        }


        // empty view for empty list message
        emptyListText = findViewById(R.id.empty_view)
        settingsBtn = findViewById(R.id.settingsIcon_img)
        settingsBtn.setOnClickListener(View.OnClickListener {
            val settingsIntent = Intent(applicationContext, MainSettingsActivity::class.java)
            startActivity(settingsIntent)
        })
        helpBtn = findViewById(R.id.help_img)
        helpBtn.setOnClickListener(View.OnClickListener {
            val introIntent = Intent(applicationContext, IntroActivity::class.java)
            startActivity(introIntent)
        })


        // add main_fab revealing menu
        addRevealFabBtn()

        // Recycle view
        initRecycleView()

        // start scheduler for notifications reminder
        scheduleChargingReminder(this)

        // make bottom navigation bar and status bar hide
        hideStatusNavBars()
    }



    override fun onStart() {
        super.onStart()
        checkConsentActive = consentSDKNeed
        if (checkConsentActive == true) {
            // Initialize ConsentSDK
            consentObjReference = ConsentSDK.Builder(this) // .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // redminote 5 // Add your test device id "Remove addTestDeviceId on production!"
                    // .addTestDeviceId("9978A5F791A259430A0156313ED9C6A2")
                    .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                    .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                    .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                    .build()

            // To check the consent and load ads
            consentObjReference!!.checkConsent(object : ConsentCallback() {
                override fun onResult(isRequestLocationInEeaOrUnknown: Boolean) {
                    Log.i("gdpr_TAG", "onResult: isRequestLocationInEeaOrUnknown : $isRequestLocationInEeaOrUnknown")
                    // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
                    mAdView.loadAd(getAdRequest(this@MainActivity))
                }
            })

            // request ad banner
            mAdView = findViewById(R.id.adView)

            // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
            mAdView.loadAd(getAdRequest(this@MainActivity))
        }
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

    /**
     * ---------------------------------------------------------------------------------------------
     * Go to intro activity
     * ---------------------------------------------------------------------------------------------
     */
    fun goToIntro() {
        val introActivityIntent = Intent(baseContext, IntroActivity::class.java)
        startActivity(introActivityIntent)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Get the number of times the app has been opened
     * ---------------------------------------------------------------------------------------------
     */
    /**
     * ---------------------------------------------------------------------------------------------
     * Set the number of times the app has been opened
     * ---------------------------------------------------------------------------------------------
     */
    var appOpenings: Int
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            return prefs.getInt(APP_OPENING_COUNTER, DEFAULT_COUNT)
        }
        set(count) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            editor.putInt(APP_OPENING_COUNTER, count)
            editor.apply()
        }

    /**
     * ---------------------------------------------------------------------------------------------
     * Get if consent gdpr must be asked or not
     * ---------------------------------------------------------------------------------------------
     */
    /**
     * ---------------------------------------------------------------------------------------------
     * Set if consent gdpr must be asked or not
     * ---------------------------------------------------------------------------------------------
     */
    var consentSDKNeed: Boolean
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            return prefs.getBoolean(APP_CONSENT_NEED, DEFAULT_CONSENT_NEED)
        }
        set(isNeeded) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            editor.putBoolean(APP_CONSENT_NEED, isNeeded)
            editor.apply()
        }

    /**
     * *** only for debug notifications
     * @param view
     */
    fun testNotification(view: View?) {
        // NotificationsUtility.remindNextDaysExpiringFood(this);
    }

    /**
     * *** only for debug notifications
     * @param view
     */
    fun testTodayNotification(view: View?) {
        // NotificationsUtility.remindTodayExpiringFood(this);
    }

    // Recycle touch an item callback to update/modify task
    override fun onItemClickListener(itemId: Int) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.")
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Recycle view list init
     * ---------------------------------------------------------------------------------------------
     */
    private fun initRecycleView() {
        // Set the RecyclerView's view
        foodList = findViewById(R.id.main_today_food_list_recycleView)

        // Set LinearLayout
        foodList.setLayoutManager(LinearLayoutManager(this))

        // retrieve application context for viewmodel
        // application = (Application) getApplicationContext();
        applicationRef = getApplication()

        // Initialize the adapter and attach it to the RecyclerView
        foodListAdapter = FoodListAdapter(this, this,
                FoodListActivity.FOOD_EXPIRING_TODAY)
        foodList.setAdapter(foodListAdapter)

        // Divider decorator
        val decoration = DividerItemDecoration(applicationContext, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
        foodList.addItemDecoration(decoration)

        // Configure the adapter; it uses LiveData to keep updated on changes
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
        retrieveFoodExpiringToday()
        Log.d(TAG, "setupAdapter: FOOD_TYPE : " + FoodListActivity.FOOD_EXPIRING_TODAY)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Livedata/ViewModel recovering Expiring Today Food list
     * ---------------------------------------------------------------------------------------------
     */
    private fun retrieveFoodExpiringToday() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB")

        // Declare my viewModel factory, parametrized with foodlistType
        val factory = FoodsViewModelFactory(FoodListActivity.FOOD_EXPIRING_TODAY)

        // Create the viewModel for the food list, based on  foodlistType
        val viewModel = ViewModelProviders.of(this, factory).get(FoodsViewModel::class.java)

        // Observe changes in data through LiveData: getFoodList() actually return LiveData<List<FoodEntry>>
        val foods = viewModel.foodList
            foods!!.observe(this, Observer { foodEntries ->
                Log.d(TAG, "Receiving database " + FoodListActivity.FOOD_EXPIRING_TODAY + " LiveData")
                // foodList.setVisibility(View.VISIBLE);
                foodListAdapter.adapterFoodEntries = foodEntries
                if (foodEntries!!.size > 0) {
                    emptyListText.visibility = View.INVISIBLE
                } else {
                    emptyListText.visibility = View.VISIBLE
                }
            })
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Set directly in flag the need for consent for admob ads
     * ---------------------------------------------------------------------------------------------
     */
    fun setConsentSdkFlag(flag: Boolean) {
        checkConsentActive = flag
    }

    // ---------------------------------------------------------------------------------------------
    //                                          MENU STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainactivity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val settingsIntent = Intent(this, MainSettingsActivity::class.java)
            startActivity(settingsIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
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
        fab = findViewById(R.id.main_fab)
        fabMenu = findViewById(R.id.main_fabMenu)
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
        fabMenu!!.setMenuDirection(Direction.LEFT)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Revealing main_fab button menu management
     * ---------------------------------------------------------------------------------------------
     */
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_insert) {
            val toInsertFood = Intent(this@MainActivity, InsertFoodActivity::class.java)
            startActivity(toInsertFood)
        } else if (id == R.id.menu_expiring_food) {
            val showExpiringFood = Intent(this@MainActivity, FoodListActivity::class.java)
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)
            startActivity(showExpiringFood)
        } else if (id == R.id.menu_consumed_food) {
            val showSavedFood = Intent(this@MainActivity, FoodListActivity::class.java)
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED)
            startActivity(showSavedFood)
        } else if (id == R.id.menu_dead_food) {
            val showDeadFood = Intent(this@MainActivity, FoodListActivity::class.java)
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD)
            startActivity(showDeadFood)
        }
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        private const val APP_OPENING_COUNTER = "app-opening-counter"
        private const val DEFAULT_COUNT = 0
        private const val APP_CONSENT_NEED = "consent_requested"
        private const val DEFAULT_CONSENT_NEED = true
        private const val NUM_MAX_OPENINGS = 2
        private var checkConsentActive = true
    }
}