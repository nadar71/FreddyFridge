package eu.indiewalkabout.fridgemanager

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import androidx.work.WorkerFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import eu.indiewalkabout.fridgemanager.core.reminder.withalarmmanager.AlarmReminderScheduler
import javax.inject.Inject


// Class used for access singletons and application context wherever in the app
// NB : register in manifest in <Application android:name=".App">... </Application>
@HiltAndroidApp
class FreddyFridgeApp : MultiDexApplication(), Configuration.Provider {
    lateinit var alarmReminderScheduler: AlarmReminderScheduler
    @Inject
    lateinit var workerFactory: WorkerFactory

    companion object {
        /*// Implement a function to display an ad if the surfacing is ready:
        fun displayUnityInterstitialAd(activity: Activity, surfacingId: String) {
            if (UnityAds.isReady(surfacingId)) {
                UnityAds.show(activity, surfacingId)
            }
        }*/
    }

    override fun onCreate() {
        super.onCreate()
        // unityId = applicationContext.getString(R.string.unityads_id)

        // start scheduler for notifications reminder
        alarmReminderScheduler = AlarmReminderScheduler(this)
        alarmReminderScheduler.setRepeatingAlarm()

        // Initialize Unity SDK:
        /*UnityAds.initialize(applicationContext,
            applicationContext.getString(R.string.unityads_id), unityAdsTestMode, this)*/

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this) // 👈 IMPORTANT
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
