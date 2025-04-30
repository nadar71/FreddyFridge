package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.APP_OPENING_COUNTER
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.DEFAULT_COUNT
import eu.indiewalkabout.fridgemanager.core.domain.navigation.AppNavigation
import eu.indiewalkabout.fridgemanager.core.domain.navigation.NavigationGraph
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars


@AndroidEntryPoint
class MainActivity : AppCompatActivity()  {
    val TAG = "MainActivity"



    private var numPrevOpenings = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Main_activity created")

        // open intro only for the first 3 times
        numPrevOpenings = appOpenings

        setContent {
            FreddyFridgeTheme {
                AppNavigation.NavigationInit()
                NavigationGraph(AppNavigation.appNavHostController)
            }
        }

        hideStatusNavBars(this)

        // TODO : share button
        // TODO : admob/unity ads
        // TODO : go to insert with swipe?

    }


    override fun onStart() {
        super.onStart()
    }



    // Get/Set the number of times the app has been opened
    private var appOpenings: Int
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



    // DEBUG notifications
    /*fun testNotification(view: View?) {
        lateinit var foodEntriesNextDays: List<FoodEntry>
        CoroutineScope(Dispatchers.Main).launch {
            val repository = (eu.indiewalkabout.fridgemanager.FreddyFridgeApplication.getsContext() as eu.indiewalkabout.fridgemanager.FreddyFridgeApplication).repository
            val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(
                DateUtility.normalizedUtcMsForToday
            )
            val expiringDateToBeNotified =
                dataNormalizedAtMidnight + TimeUnit.DAYS.toSeconds(1.toLong()).toInt()
            val foodEntriesNextDays =
                repository!!.loadAllFoodExpiring_no_livedata(expiringDateToBeNotified)

            foodEntriesNextDays.let {
                if (foodEntriesNextDays.size > 0) {
                    NotificationsUtility.remindNextDaysExpiringFood(applicationContext, it)
                }
            }

        }

    }

    // DEBUG notifications
    fun testTodayNotification(view: View?) {
        lateinit var foodEntriesToDay: List<FoodEntry>

        CoroutineScope(Dispatchers.IO).launch {
            val dataNormalizedAtMidnight = DateUtility.getLocalMidnightFromNormalizedUtcDate(
                DateUtility.normalizedUtcMsForToday
            )
            val previousDayDate = dataNormalizedAtMidnight - DateUtility.DAY_IN_MILLIS
            val nextDayDate = dataNormalizedAtMidnight + DateUtility.DAY_IN_MILLIS
            val repository = (eu.indiewalkabout.fridgemanager.FreddyFridgeApplication.getsContext() as eu.indiewalkabout.fridgemanager.FreddyFridgeApplication).repository
            foodEntriesToDay =
                repository!!.loadFoodExpiringToday_no_livedata(previousDayDate, nextDayDate)

            foodEntriesToDay.let {
                if (foodEntriesToDay.size > 0) {
                    NotificationsUtility.remindTodayExpiringFood(applicationContext, it)
                    Log.i(
                        TAG,
                        "Workmanager, doWork: check food expiring  TODAY, notification sent"
                    )
                }
            }
        }


    }*/




}