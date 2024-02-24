package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import eu.indiewalkabout.fridgemanager.core.util.AppExecutors.Companion.instance
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.FreddyFridgeApplication
import eu.indiewalkabout.fridgemanager.FreddyFridgeApplication.Companion.getsContext
import eu.indiewalkabout.fridgemanager.data.local.db.DateConverter.fromDate
import eu.indiewalkabout.fridgemanager.data.local.db.DateConverter.toDate
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.presentation.ui.intromain.MainActivity
// import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.KeyboardUtils.Companion.addKeyboardToggleListener
import eu.indiewalkabout.fridgemanager.core.util.KeyboardUtils.SoftKeyboardToggleListener
import eu.indiewalkabout.fridgemanager.core.util.OnSwipeTouchListener
import eu.indiewalkabout.fridgemanager.databinding.ActivityInsertFoodBinding
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG


import java.util.*

// new food to keep monitored
class InsertFoodActivity : AppCompatActivity(), CalendarView.OnDateChangeListener,
    OnFABMenuSelectedListener {
    private lateinit var binding: ActivityInsertFoodBinding

    // Date picked up reference
    private var myDatePicked: Calendar? = null

    // set the food id as default: will be changed in case of update
    private var foodId = DEFAULT_ID

    // Object foodEntry to change in case of update
    lateinit var foodEntryToChange: LiveData<FoodEntry>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_insert_food)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_insert_food)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        // binding.adView.loadAd(getAdRequest(this@InsertFoodActivity))

        // init views
        initViews()

        // navigation fab
        addRevealFabBtn()

        // restore the task id after rotation, in case savedInstanceState has been created
        // otherwise it remains DEFAULT_TASK_ID set above
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ID)) {
            foodId = savedInstanceState.getInt(INSTANCE_ID, DEFAULT_ID)
        }

        // check if activity requested in update mode
        checkUpdateModeOn()

        hideStatusNavBars(this)

        // goto to home on swipe left/right
        binding.insertFoodLayout.setOnTouchListener(object :
            OnSwipeTouchListener(this@InsertFoodActivity) {
            override fun onSwipeLeft() {
                returnHome()
            }

            override fun onSwipeRight() {
                returnHome()
            }
        })


        // binding.calendarCv.date

        binding.calendarCv.setOnDateChangeListener { view, year, month, dayOfMonth ->
            myDatePicked = GregorianCalendar(year, month, dayOfMonth)
            Log.i(TAG, "onSelectedDayChange: done: $myDatePicked")
        }

        /*
        calendar_cv.setOnTouchListener(object: OnSwipeTouchListener(this@InsertFoodActivity) {
            override fun onSwipeLeft() {
                returnHome()
            }
            override fun onSwipeRight() {
                returnHome()
            }
        })
        */

    }


    override fun onResume() {
        super.onResume()
        addKeyboardToggleListener(this, object : SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                Log.d("keyboard", "keyboard visible: $isVisible")
                if (isVisible) {
                    binding.adView.visibility = View.INVISIBLE
                } else {
                    binding.adView.visibility = View.VISIBLE
                }
            }
        })
    }


    private fun initViews() {
        // set tomorrow selected in calendar widget
        /*val dateTodayNormalizedAtMidnight =
            getLocalMidnightFromNormalizedUtcDate(normalizedUtcMsForToday)
        binding.calendarCv.date = dateTodayNormalizedAtMidnight*/

        // saving editing test click
        binding.saveBtn.setOnClickListener(View.OnClickListener { onSaveBtnClicked() })

        // speaker activation
        binding.speakBtn.setOnClickListener(View.OnClickListener { startVoiceInput() })
    }

    // Toolbar init
    private fun toolBarInit() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    // Get the date picked up by the user from calendar
    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        myDatePicked = GregorianCalendar(year, month, dayOfMonth)
        Log.i(TAG, "onSelectedDayChange: done: $myDatePicked")
    }



    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_ID, foodId)
        super.onSaveInstanceState(outState)
    }

    // Check if the activity has been called with update mode active
    private fun checkUpdateModeOn() {
        // check intent extra
        val intent = intent
        if (intent != null && intent.hasExtra(ID_TO_BE_UPDATED)) {
            Log.d(TAG, "onCreate: Update mode ACTIVE")
            binding.saveBtn.setText(R.string.update_btn_label)

            // if id is the default one insert the new to be updated
            if (foodId == DEFAULT_ID) {
                foodId = intent.getIntExtra(ID_TO_BE_UPDATED, DEFAULT_ID)

                // Create the viewModel for the food entry, based on  foodId
                val viewModel = ViewModelProviders.of(this).get(FoodsViewModel::class.java)

                // Populate the text edit fields and
                // observe changes in data through LiveData: getFoodEntry() actually return 1 LiveData<FoodEntry>
                foodEntryToChange = viewModel.getFoodEntry(foodId)
                foodEntryToChange.observe(this, object : Observer<FoodEntry?> {
                    override fun onChanged(value: FoodEntry?) {
                        // dispose observer do not need it no more
                        foodEntryToChange.removeObserver(this)

                        // update UI with new data from db
                        populateUI(value)
                    }
                })
            }
        }
    }


    fun onSaveBtnClicked() {
        Log.d(TAG, "onSaveBtnClicked")
        val foodName = binding.foodNameEt.text.toString()

        // validate item number
        val itemsNumStr = binding.howmanyEt.text.toString()
        val itemsNum = if (itemsNumStr == "") {
            1
        } else {
            itemsNumStr.toInt()
        }

        // validate entry : name
        if (foodName.isEmpty()) {
            Toast.makeText(
                this@InsertFoodActivity,
                resources.getString(R.string.food_description_alert), Toast.LENGTH_SHORT
            ).show()
            return
        }


        // validate entry : date
        if (foodId == DEFAULT_ID && myDatePicked == null) {
            Toast.makeText(
                this@InsertFoodActivity,
                resources.getString(R.string.food_expdate_alert),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        Toast.makeText(
            this,
            foodName + resources.getString(R.string.food_inserted_alert),
            Toast.LENGTH_SHORT
        ).show()

        // Update db using executor
        instance!!.diskIO().execute {
            if (foodId == DEFAULT_ID) {     // save a new task
                Log.d(TAG, "run: Save new food entry")
                val expiringDate = myDatePicked?.time
                Log.d(TAG, "foodName : $foodName")
                Log.d(TAG, "expiringDate : $expiringDate")


                // repo insert, n time as item's number
                val repository = (getsContext() as FreddyFridgeApplication?)!!.repository
                for (num in 1..itemsNum) {
                    // create a new food obj and init with data inserted by user
                    val foodEntry = FoodEntry(0, "${foodName}  n. ${num}", expiringDate!!)
                    repository!!.insertFoodEntry(foodEntry)
                }
                finish()
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
                    expiringDate_l = binding.calendarCv.date
                    foodEntry = FoodEntry(0, foodName, toDate(expiringDate_l)!!)
                }

                // set id to the task to update
                foodEntry.id = foodId

                // keep if the has been already consumed
                foodEntry.done = foodEntryToChange.value!!.done

                // update task on db
                val repository = (getsContext() as FreddyFridgeApplication?)!!.repository
                repository!!.updateFoodEntry(foodEntry)

                finish()
            }
        }
    }

    // Called when in update mode to re-populate UI
    private fun populateUI(foodEntry: FoodEntry?) {
        if (foodEntry == null) {
            return
        }
        binding.foodNameEt.setText(foodEntry.name)
        binding.calendarCv.date = fromDate(foodEntry.expiringAt)!!
    }

    // Voice input
    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
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
                    binding.foodNameEt.setText(result?.get(0) ?: "retry")
                }
            }
        }
    }

    // Set datePicked from outside
    fun setDatePicked(calendar: Calendar?) {
        if (calendar != null) {
            myDatePicked = calendar
        }
    }

    // Set expiration date from outside
    fun setDateExpir_cv(date: Date?) {
        binding.calendarCv.date = fromDate(date)!!
    }


    // ---------------------------------------------------------------------------------------------
    // MENU STUFF
    // ---------------------------------------------------------------------------------------------
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
        if (binding.insertFoodFabMenu.isShowing) {
            binding.insertFoodFabMenu.closeMenu()
        }
    }

    // Adding revealing main_fab button
    private fun addRevealFabBtn() {
        val fab = findViewById<FloatingActionButton>(R.id.insert_food_fab)

        // remove insert menu item
        binding.insertFoodFabMenu.removeItem(R.id.menu_insert)
        try {
            if (fab != null) {
                //attach menu to main_fab
                binding.insertFoodFabMenu.bindAnchorView(fab)

                //set menu selection listener
                binding.insertFoodFabMenu.setOnFABMenuSelectedListener(this@InsertFoodActivity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.insertFoodFabMenu.menuDirection = Direction.LEFT
    }

    // Revealing main_fab button menu management
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_expiring_food) {
            showExpiringFood()
        } else if (id == R.id.menu_consumed_food) {
            showSavedFood()
        } else if (id == R.id.menu_dead_food) {
            showDeadFood()
        } else if (id == R.id.menu_home) {
            returnHome()
        }
    }

    private fun showExpiringFood() {
        val showExpiringFood = Intent(this@InsertFoodActivity, FoodListActivity::class.java)
        showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)
        startActivity(showExpiringFood)
    }

    private fun showSavedFood() {
        val showSavedFood = Intent(this@InsertFoodActivity, FoodListActivity::class.java)
        showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED)
        startActivity(showSavedFood)
    }

    private fun showDeadFood() {
        val showDeadFood = Intent(this@InsertFoodActivity, FoodListActivity::class.java)
        showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD)
        startActivity(showDeadFood)
    }

    private fun returnHome() {
        val returnHome = Intent(this@InsertFoodActivity, MainActivity::class.java)
        startActivity(returnHome)
    }

    companion object {
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