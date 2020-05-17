package eu.indiewalkabout.fridgemanager.ui

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.gms.ads.AdView
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.view.FABRevealMenu
import eu.indiewalkabout.fridgemanager.AppExecutors.Companion.instance
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.SingletonProvider
import eu.indiewalkabout.fridgemanager.SingletonProvider.Companion.getsContext
import eu.indiewalkabout.fridgemanager.data.DateConverter
import eu.indiewalkabout.fridgemanager.data.DateConverter.fromDate
import eu.indiewalkabout.fridgemanager.data.DateConverter.toDate
import eu.indiewalkabout.fridgemanager.data.FoodEntry
import eu.indiewalkabout.fridgemanager.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.util.DateUtility.getLocalMidnightFromNormalizedUtcDate
import eu.indiewalkabout.fridgemanager.util.DateUtility.normalizedUtcMsForToday
import eu.indiewalkabout.fridgemanager.util.KeyboardUtils.Companion.addKeyboardToggleListener
import eu.indiewalkabout.fridgemanager.util.KeyboardUtils.SoftKeyboardToggleListener
import java.util.*

/**
 * ---------------------------------------------------------------------------------------------
 * InsertFoodActivity
 * Get a new food to keep monitored
 * ---------------------------------------------------------------------------------------------
 */
class InsertFoodActivity : AppCompatActivity(), CalendarView.OnDateChangeListener, OnFABMenuSelectedListener {
    // Views ref
    lateinit var save_btn: Button
    lateinit var speakerBtn: ImageView
    lateinit var foodName_et: EditText
    lateinit var dateExpir_cv: CalendarView

    // private Toolbar        foodInsertToolbar;
    var fabMenu: FABRevealMenu? = null

    // admob banner ref
    lateinit var mAdView: AdView

    // Date picked up reference
    var myDatePicked: Calendar? = null

    // set the food id as default: will be changed in case of update
    private var foodId = DEFAULT_ID

    // Object foodEntry to change in case of update
    lateinit var foodEntryToChange: LiveData<FoodEntry>

    /**
     * ---------------------------------------------------------------------------------------------
     * onCreate
     * @param savedInstanceState
     * ---------------------------------------------------------------------------------------------
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_food)


        // load ad banner
        mAdView = findViewById(R.id.adView)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(getAdRequest(this@InsertFoodActivity))

        // init views
        initViews()

        // init toolbar
        // toolBarInit();

        // navigation fab
        addRevealFabBtn()

        // restore the task id after rotation, in case savedInstanceState has been created
        // otherwise it remains DEFAULT_TASK_ID set above
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ID)) {
            foodId = savedInstanceState.getInt(INSTANCE_ID, DEFAULT_ID)
        }

        // check if activity requested in update mode
        checkUpdateModeOn()

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

    override fun onResume() {
        super.onResume()
        addKeyboardToggleListener(this, object : SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                Log.d("keyboard", "keyboard visible: $isVisible")
                if (isVisible == true) {
                    mAdView.visibility = View.INVISIBLE
                } else {
                    mAdView.visibility = View.VISIBLE
                }
            }
        })
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Views init
     * ---------------------------------------------------------------------------------------------
     */
    private fun initViews() {
        save_btn = findViewById(R.id.save_btn)
        foodName_et = findViewById(R.id.foodName_et)
        dateExpir_cv = findViewById(R.id.calendar_cv)
        speakerBtn = findViewById(R.id.speak_btn)
        dateExpir_cv.setOnDateChangeListener(this)

        // set tomorro selected in calendar widget
        val dateTodayNormalizedAtMidnight = getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
        // dateExpir_cv.setDate(dateTodayNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS);
        dateExpir_cv.setDate(dateTodayNormalizedAtMidnight)

        // saving editing test click
        save_btn.setOnClickListener(View.OnClickListener { onSaveBtnClicked() })

        // speaker activation
        speakerBtn.setOnClickListener(View.OnClickListener { startVoiceInput() })
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Toolbar init
     * ---------------------------------------------------------------------------------------------
     */
    private fun toolBarInit() {
        // get a support action bar
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
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
    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(TAG, "onSelectedDayChange: done")
        myDatePicked = GregorianCalendar(year, month, dayOfMonth)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * save insert data in case of rotations
     * @param outState
     * ---------------------------------------------------------------------------------------------
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_ID, foodId)
        super.onSaveInstanceState(outState)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Check if the activity has been called with update mode active
     * ---------------------------------------------------------------------------------------------
     */
    fun checkUpdateModeOn() {
        // check intent extra
        val intent = intent
        if (intent != null && intent.hasExtra(ID_TO_BE_UPDATED)) {
            Log.d(TAG, "onCreate: Update mode ACTIVE")
            save_btn.setText(R.string.update_btn_label)

            // if id is the default one insert the new to be updated
            if (foodId == DEFAULT_ID) {
                foodId = intent.getIntExtra(ID_TO_BE_UPDATED, DEFAULT_ID)

                // Create the viewModel for the food entry, based on  foodId
                val viewModel = ViewModelProviders.of(this).get(FoodsViewModel::class.java)

                // Populate the text edit fields and
                // observe changes in data through LiveData: getFoodEntry() actually return 1 LiveData<FoodEntry>
                foodEntryToChange = viewModel.getFoodEntry(foodId)
                foodEntryToChange.observe(this, object : Observer<FoodEntry?> {
                    override fun onChanged(foodEntry: FoodEntry?) {
                        // dispose observer do not need it no more
                        foodEntryToChange.removeObserver(this)

                        // update UI with new data from db
                        populateUI(foodEntry)
                    }
                })
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Save/Update Button
     * ---------------------------------------------------------------------------------------------
     */
    fun onSaveBtnClicked() {
        Log.d(TAG, "onSaveBtnClicked")
        val foodName = foodName_et.text.toString()

        // validate entry : name
        if (foodName.isEmpty()) {
            Toast.makeText(this@InsertFoodActivity,
                    resources.getString(R.string.food_description_alert), Toast.LENGTH_SHORT).show()
            return
        }

        // validate entry : date
        if (foodId == DEFAULT_ID && myDatePicked == null) {
            Toast.makeText(this@InsertFoodActivity, resources.getString(R.string.food_expdate_alert), Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, foodName + resources.getString(R.string.food_inserted_alert), Toast.LENGTH_SHORT).show()

        // ----------------------------------------
        // Update db using executor
        // ----------------------------------------
        instance!!.diskIO().execute {
            if (foodId == DEFAULT_ID) {     // save a new task
                Log.d(TAG, "run: Save new food entry")
                val expiringDate = myDatePicked?.time
                Log.d(TAG, "foodName : $foodName")
                Log.d(TAG, "expiringDate : $expiringDate")

                // create a new food obj and init with data inserted by user
                val foodEntry = FoodEntry(0, foodName, expiringDate!!)

                // repo insert
                val repository = (getsContext() as SingletonProvider?)!!.repository
                repository!!.insertFoodEntry(foodEntry)

                // end activity
                finish()

                // restart it for new insertion if any
                startActivity(intent)
            } else {                               // update a previous task
                Log.d(TAG, "run: update old food entry")

                // create a new food obj and init with data inserted by user
                val foodEntry: FoodEntry

                // date picked in case of date change
                val expiringDate: Date

                // date get from food selected unchanged
                val expiringDate_l: Long

                // get date from user selection if new
                // or the previous date unchanged
                if (myDatePicked != null) {
                    expiringDate = myDatePicked!!.time
                    foodEntry = FoodEntry(0, foodName, expiringDate)
                } else {
                    expiringDate_l = dateExpir_cv.date
                    foodEntry = FoodEntry(0, foodName, toDate(expiringDate_l)!!)
                }

                // set id to the task to update
                foodEntry.id = foodId

                // keep if the has been already consumed
                foodEntry.done = foodEntryToChange.value!!.done

                // update task on db
                val repository = (getsContext() as SingletonProvider?)!!.repository
                repository!!.updateFoodEntry(foodEntry)

                // end activity
                finish()
            }
        }
    }

    /**
     * -----------------------------------------------------------------------------
     * Called when in update mode to re-populate UI
     * @param foodEntry
     * -----------------------------------------------------------------------------
     */
    private fun populateUI(foodEntry: FoodEntry?) {
        if (foodEntry == null) {
            return
        }
        foodName_et.setText(foodEntry.name)
        dateExpir_cv.date = fromDate(foodEntry.expiringAt)!!
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Voice input
     * ---------------------------------------------------------------------------------------------
     */
    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?")
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    foodName_et.setText(result[0])
                }
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Set datePicked from outside
     * @param calendar
     * ---------------------------------------------------------------------------------------------
     */
    fun setDatePicked(calendar: Calendar?) {
        if (calendar != null) {
            myDatePicked = calendar
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Set dateExpir_cv from outside
     * @param date
     * ---------------------------------------------------------------------------------------------
     */
    fun setDateExpir_cv(date: Date?) {
        dateExpir_cv.date = fromDate(date)!!
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
            // show interstitial ads
            // showInterstitialAd();

            // go home
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
        val fab = findViewById<FloatingActionButton>(R.id.insert_food_fab)
        var fabMenu = findViewById<FABRevealMenu>(R.id.insert_food_fabMenu)

        // remove insert menu item
        fabMenu!!.removeItem(R.id.menu_insert)
        try {
            if (fab != null && fabMenu != null) {
                fabMenu = fabMenu

                //attach menu to main_fab
                fabMenu.bindAnchorView(fab)

                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(this@InsertFoodActivity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fabMenu!!.menuDirection = Direction.LEFT
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Revealing main_fab button menu management
     * ---------------------------------------------------------------------------------------------
     */
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_expiring_food) {
            val showExpiringFood = Intent(this@InsertFoodActivity, FoodListActivity::class.java)
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)
            startActivity(showExpiringFood)
        } else if (id == R.id.menu_consumed_food) {
            val showSavedFood = Intent(this@InsertFoodActivity, FoodListActivity::class.java)
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED)
            startActivity(showSavedFood)
        } else if (id == R.id.menu_dead_food) {
            val showDeadFood = Intent(this@InsertFoodActivity, FoodListActivity::class.java)
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD)
            startActivity(showDeadFood)
        } else if (id == R.id.menu_home) {
            val returnHome = Intent(this@InsertFoodActivity, MainActivity::class.java)
            startActivity(returnHome)
        }
    }

    companion object {
        private val TAG = InsertFoodActivity::class.java.simpleName

        // flag for open activity in udate mode
        // public static final String UPDATEMODE = "update_mode";
        // key for id item to be changed for extra content when activity invoked in update mode
        const val ID_TO_BE_UPDATED = "id"

        // Extra for the food item ID to be received after device rotation
        const val INSTANCE_ID = "instanceFoodId"

        // default value of ID_TO_BE_UPDATED as requested in getIntExtra
        const val DEFAULT_ID = -1

        // unique code for system service request
        private const val REQ_CODE_SPEECH_INPUT = 100
    }
}