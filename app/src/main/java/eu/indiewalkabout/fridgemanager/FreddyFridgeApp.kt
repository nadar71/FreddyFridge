package eu.indiewalkabout.fridgemanager

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkerFactory
import dagger.hilt.android.HiltAndroidApp
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder.withalarmmanager.AlarmReminderScheduler
import javax.inject.Inject


// Class used for access singletons and application context wherever in the app
// NB : register in manifest in <Application android:name=".App">... </Application>
@HiltAndroidApp
class FreddyFridgeApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: WorkerFactory

    companion object {
        // global variables
        lateinit var alarmReminderScheduler: AlarmReminderScheduler
        /*// Implement a function to display an ad if the surfacing is ready:
        fun displayUnityInterstitialAd(activity: Activity, surfacingId: String) {
            if (UnityAds.isReady(surfacingId)) {
                UnityAds.show(activity, surfacingId)
            }
        }*/
    }

    override fun onCreate() {
        super.onCreate()
        AppPreferences.app_opening_counter = AppPreferences.app_opening_counter + 1
        // unityId = applicationContext.getString(R.string.unityads_id)

        // Initialize Unity SDK:
        /*UnityAds.initialize(applicationContext,
            applicationContext.getString(R.string.unityads_id), unityAdsTestMode, this)*/

    }



    // Assign the injected factory in order to let it manage your Workers.
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()

    // unity ads init complete
    /*override fun onInitializationComplete() {
        Log.v(UNITYTAG, "UnityAds init complete")
    }

    // unity ads init failed
    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        Log.v(UNITYTAG, "UnityAds init FAILED")

    }*/

}
