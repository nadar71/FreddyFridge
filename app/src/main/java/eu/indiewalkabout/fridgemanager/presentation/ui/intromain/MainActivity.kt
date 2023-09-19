package eu.indiewalkabout.fridgemanager.presentation.ui.intromain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.App
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.core.reminder.withworkmanager.FoodReminderWorker
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter.ItemClickListener
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodListActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodsViewModel
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodsViewModelFactory
import eu.indiewalkabout.fridgemanager.presentation.ui.food.InsertFoodActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.settings.MainSettingsActivity
import eu.indiewalkabout.fridgemanager.util.*
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.ConsentCallback
import eu.indiewalkabout.fridgemanager.core.util.DateUtility
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.NotificationsUtility
import eu.indiewalkabout.fridgemanager.core.util.OnSwipeTouchListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), ItemClickListener, OnFABMenuSelectedListener {

    // recycle View stuff
    lateinit var foodListAdapter: FoodListAdapter
    var foodListForShare: String = ""

    // vars utils for testing
    private var numPrevOpenings = 0

    // ---------------------------------------------------------------------------------------------
    // Return the reference to ConsentSDK instance to be used by test classes

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


        settingsIcon_img.setOnClickListener {
            val settingsIntent = Intent(applicationContext, MainSettingsActivity::class.java)
            startActivity(settingsIntent)
        }


        help_img.setOnClickListener {
            val introIntent = Intent(applicationContext, IntroActivity::class.java)
            startActivity(introIntent)
        }

        share_img.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = foodListForShare
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_today_title))
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }


        addRevealFabBtn()
        initRecycleView()




        hideStatusNavBars(this)

        // goto to insert on swipe left/right
        home_activity_layout.setOnTouchListener(object: OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                toInsertFood()
            }
            override fun onSwipeRight() {
                toInsertFood()
            }
        })

        today_food_list_recycleView.setOnTouchListener(object: OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                toInsertFood()
            }
            override fun onSwipeRight() {
                toInsertFood()
            }
        })

    }



    override fun onStart() {
        super.onStart()
        showAds()
    }



    private fun showAds() {
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
                    adView.loadAd(getAdRequest(this@MainActivity))
                }
            })

            // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
            adView.loadAd(getAdRequest(this@MainActivity))
        }
    }


    // Go to intro activity
    fun goToIntro() {
        val introActivityIntent = Intent(baseContext, IntroActivity::class.java)
        startActivity(introActivityIntent)
    }



    // Get/Set the number of times the app has been opened
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

    // Get/Set if consent gdpr must be asked or not
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
        lateinit var foodEntriesNextDays: List<FoodEntry>
        CoroutineScope(Dispatchers.Main).launch {
            val repository = (App.getsContext() as App).repository
            val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(
                DateUtility.normalizedUtcMsForToday)
            val expiringDateToBeNotified = dataNormalizedAtMidnight + TimeUnit.DAYS.toSeconds(1.toLong()).toInt()
            val foodEntriesNextDays = repository!!.loadAllFoodExpiring_no_livedata(expiringDateToBeNotified)

            foodEntriesNextDays.let {
                if (foodEntriesNextDays.size > 0) {
                    NotificationsUtility.remindNextDaysExpiringFood(applicationContext, it)
                }
            }

        }

    }

    /**
     * *** only for debug notifications
     * @param view
     */
    fun testTodayNotification(view: View?) {
        lateinit var foodEntriesToDay: List<FoodEntry>

        CoroutineScope(Dispatchers.IO).launch {
            val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(
                DateUtility.normalizedUtcMsForToday)
            val previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS
            val nextDayDate = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS
            val repository = (App.getsContext() as App).repository
            foodEntriesToDay = repository!!.loadFoodExpiringToday_no_livedata(previousDayDate, nextDayDate)

            foodEntriesToDay.let {
                if (foodEntriesToDay.size > 0) {
                    NotificationsUtility.remindTodayExpiringFood(applicationContext, it)
                    Log.i(FoodReminderWorker.TAG, "Workmanager, doWork: check food expiring  TODAY, notification sent")
                }
            }
        }


    }

    // Recycle touch an item callback to update/modify task
    override fun onItemClickListener(itemId: Int) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.")
    }



    // Recycle view list init
    private fun initRecycleView() {
        today_food_list_recycleView.setLayoutManager(LinearLayoutManager(this))

        foodListAdapter = FoodListAdapter(this, this,
            FoodListActivity.FOOD_EXPIRING_TODAY
        )
        today_food_list_recycleView.setAdapter(foodListAdapter)

        setupAdapter()

        // make fab button hide when scrolling list
        today_food_list_recycleView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && main_fab!!.isShown) main_fab!!.hide()
            }

            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) main_fab!!.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }



    // Used to reload from db the tasks list and update the list view in screen
    private fun setupAdapter() {
        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ")
        retrieveFoodExpiringToday()
        Log.d(TAG, "setupAdapter: FOOD_TYPE : " + FoodListActivity.FOOD_EXPIRING_TODAY)
    }



    // Livedata/ViewModel recovering Expiring Today Food list
    private fun retrieveFoodExpiringToday() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB")

        val factory = FoodsViewModelFactory(FoodListActivity.FOOD_EXPIRING_TODAY)
        val viewModel = ViewModelProvider(this, factory).get(FoodsViewModel::class.java)

        // Observe changes in data through LiveData: getFoodList() actually return LiveData<List<FoodEntry>>
        val foods = viewModel.foodList
            foods!!.observe(this, Observer { foodEntries ->
                Log.d(TAG, "Receiving database " + FoodListActivity.FOOD_EXPIRING_TODAY + " LiveData")
                foodListAdapter.adapterFoodEntries = foodEntries
                if (foodEntries!!.size > 0) {
                    emptyListText.visibility = View.INVISIBLE
                    share_img.visibility = View.VISIBLE
                    // fill the list for sharing
                    foodListForShare += "${getString(R.string.share_today_title)} \n"
                    for(item in foodEntries) {
                        foodListForShare += "${item.name} \n"
                    }
                } else {
                    emptyListText.visibility = View.VISIBLE
                    share_img.visibility = View.INVISIBLE
                }
            })
    }



    // Set directly in flag the need for consent for admob ads
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
        if (main_fabMenu != null) {
            if (main_fabMenu!!.isShowing) {
                main_fabMenu!!.closeMenu()
            }
        }
    }



    // Adding revealing main_fab button
    private fun addRevealFabBtn() {
        try {
            if (main_fab != null && main_fabMenu != null) {
                //attach menu to main_fab
                main_fabMenu!!.bindAnchorView(main_fab!!)

                //set menu selection listener
                main_fabMenu!!.setOnFABMenuSelectedListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        main_fabMenu!!.setMenuDirection(Direction.LEFT)
    }


    // Revealing main_fab button menu management
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_insert) {
            toInsertFood()

        } else if (id == R.id.menu_expiring_food) {
            showExpiringFood()

        } else if (id == R.id.menu_consumed_food) {
            showSavedFood()

        } else if (id == R.id.menu_dead_food) {
            showDeadFood()
        }
    }

    private fun showDeadFood() {
        val showDeadFood = Intent(this@MainActivity, FoodListActivity::class.java)
        showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD)
        startActivity(showDeadFood)
    }

    private fun showSavedFood() {
        val showSavedFood = Intent(this@MainActivity, FoodListActivity::class.java)
        showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED)
        startActivity(showSavedFood)
    }

    private fun showExpiringFood() {
        val showExpiringFood = Intent(this@MainActivity, FoodListActivity::class.java)
        showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)
        startActivity(showExpiringFood)
    }

    private fun toInsertFood() {
        val toInsertFood = Intent(this@MainActivity, InsertFoodActivity::class.java)
        startActivity(toInsertFood)
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