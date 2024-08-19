package eu.indiewalkabout.fridgemanager

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import dagger.hilt.android.HiltAndroidApp
import eu.indiewalkabout.fridgemanager.core.reminder.withalarmmanager.AlarmReminderScheduler
import eu.indiewalkabout.fridgemanager.core.unityads.UNITYTAG
import eu.indiewalkabout.fridgemanager.core.unityads.unityAdsTestMode
import eu.indiewalkabout.fridgemanager.core.unityads.unityId


// Class used for access singletons and application context wherever in the app
// NB : register in manifest in <Application android:name=".App">... </Application>
@HiltAndroidApp
class FreddyFridgeApplication
    : Application(), Configuration.Provider, IUnityAdsInitializationListener {
    companion object {
        private var sContext: Context? = null
        // Return application context wherever we are in the app
        fun getsContext(): Context? {
            return sContext
        }

        /*// Implement a function to display an ad if the surfacing is ready:
        fun displayUnityInterstitialAd(activity: Activity, surfacingId: String) {
            if (UnityAds.isReady(surfacingId)) {
                UnityAds.show(activity, surfacingId)
            }
        }*/
    }

    /*// Return singleton db instance
    private val database: FoodDatabase?
        get() = FoodDatabase.getDbInstance(this)

    // Return depository singleton instance
    val repository: FridgeManagerRepository?
        get() = database?.let { FridgeManagerRepository.getInstance(it) }*/

    override fun onCreate() {
        super.onCreate()
        sContext = applicationContext
        unityId = applicationContext.getString(R.string.unityads_id)

        // start scheduler for notifications reminder
        // scheduleChargingReminder(this)

        // start scheduler for notifications reminder
        AlarmReminderScheduler().setRepeatingAlarm()

        // Initialize Unity SDK:
        UnityAds.initialize(applicationContext,
            applicationContext.getString(R.string.unityads_id), unityAdsTestMode, this)

    }


    override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.VERBOSE)
                    .build()

    // unity ads init complete
    override fun onInitializationComplete() {
        Log.v(UNITYTAG, "UnityAds init complete")
    }

    // unity ads init failed
    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        Log.v(UNITYTAG, "UnityAds init FAILED")

    }

}
