package eu.indiewalkabout.fridgemanager

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.FinishState
import com.unity3d.ads.UnityAds.UnityAdsError
import eu.indiewalkabout.fridgemanager.data.db.FoodDatabase
import eu.indiewalkabout.fridgemanager.reminder.withalarmmanager.AlarmReminderScheduler
import eu.indiewalkabout.fridgemanager.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.util.AppExecutors


// -------------------------------------------------------------------------------------------------
// Class used for access singletons and application context wherever in the app
// NB : register in manifest in <Application android:name=".App">... </Application>

class App : Application(), Configuration.Provider {
    val mAppExecutors: AppExecutors? = null
    val unityGameID = "4029837"
    val testMode = false

    companion object {
        private var sContext: Context? = null

        // Return application context wherever we are in the app
        fun getsContext(): Context? {
            return sContext
        }

        // Implement a function to display an ad if the surfacing is ready:
        fun displayUnityInterstitialAd(activity: Activity, surfacingId: String) {
            if (UnityAds.isReady(surfacingId)) {
                UnityAds.show(activity, surfacingId)
            }
        }
    }



    // Return singleton db instance
    val database: FoodDatabase?
        get() = FoodDatabase.getsDbInstance(this)


    // Return depository singleton instance
    val repository: FridgeManagerRepository?
        get() = database?.let { FridgeManagerRepository.getInstance(it) }

    override fun onCreate() {
        super.onCreate()
        // mAppExecutors = new AppExecutors.getInstance();

        sContext = applicationContext

        // start scheduler for notifications reminder
        // scheduleChargingReminder(this)

        // start scheduler for notifications reminder
        AlarmReminderScheduler().setRepeatingAlarm()

        // UNITY
        // Declare a new listener:
        val myAdsListener = UnityAdsListener()

        // Add the listener to the SDK:
        UnityAds.addListener(myAdsListener)

        // Initialize the SDK:
        UnityAds.initialize(this, unityGameID, testMode)
    }

    override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.VERBOSE)
                    .build()


    // Implement the IUnityAdsListener interface methods:
    private class UnityAdsListener : IUnityAdsListener {
        override fun onUnityAdsReady(surfacingId: String) {
            // Implement functionality for an ad being ready to show.
        }

        override fun onUnityAdsStart(surfacingId: String) {
            // Implement functionality for a user starting to watch an ad.
        }

        override fun onUnityAdsFinish(surfacingId: String, finishState: FinishState) {
            // Implement functionality for a user finishing an ad.
        }

        override fun onUnityAdsError(error: UnityAdsError, message: String) {
            // Implement functionality for a Unity Ads service error occurring.
        }
    }





}
