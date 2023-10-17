package eu.indiewalkabout.fridgemanager

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.UnityAds
import eu.indiewalkabout.fridgemanager.core.reminder.withalarmmanager.AlarmReminderScheduler
import eu.indiewalkabout.fridgemanager.core.unityads.testMode
import eu.indiewalkabout.fridgemanager.core.unityads.unityGameID
import eu.indiewalkabout.fridgemanager.data.local.db.FoodDatabase
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepository


// Class used for access singletons and application context wherever in the app
// NB : register in manifest in <Application android:name=".App">... </Application>
class App : Application(), Configuration.Provider, IUnityAdsInitializationListener {
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

    // Return singleton db instance
    private val database: FoodDatabase?
        get() = FoodDatabase.getsDbInstance(this)

    // Return depository singleton instance
    val repository: FridgeManagerRepository?
        get() = database?.let { FridgeManagerRepository.getInstance(it) }

    override fun onCreate() {
        super.onCreate()
        sContext = applicationContext

        // start scheduler for notifications reminder
        // scheduleChargingReminder(this)

        // start scheduler for notifications reminder
        AlarmReminderScheduler().setRepeatingAlarm()

        // UNITY
        // Initialize the SDK:
        UnityAds.initialize(applicationContext, unityGameID, testMode, this)
    }

    override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.VERBOSE)
                    .build()

    // unity ads init complete
    override fun onInitializationComplete() {
        // TODO("Not yet implemented")
    }

    // unity ads init failed
    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        // TODO("Not yet implemented")
    }

}
