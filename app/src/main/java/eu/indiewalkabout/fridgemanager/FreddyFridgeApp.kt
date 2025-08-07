package eu.indiewalkabout.fridgemanager

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.HiltAndroidApp
import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder.AlarmReminderScheduler
import java.security.AccessController.getContext


@HiltAndroidApp
class FreddyFridgeApp() : Application() {

    companion object {
        // global variables
        lateinit var TEST_DEVICE_ID: String
        lateinit var appContext: Application
        lateinit var alarmReminderScheduler: AlarmReminderScheduler
        var canRequestAdsFlag: Boolean = false
        /*// Implement a function to display an ad if the surfacing is ready:
        fun displayUnityInterstitialAd(activity: Activity, surfacingId: String) {
            if (UnityAds.isReady(surfacingId)) {
                UnityAds.show(activity, surfacingId)
            }
        }*/
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        TEST_DEVICE_ID = applicationContext.getString(R.string.admob_key_test_device)
        // init admob ads
        MobileAds.initialize(this) {}
        // init app opening counter
        AppPreferences.app_opening_counter = AppPreferences.app_opening_counter + 1
        // unityId = applicationContext.getString(R.string.unityads_id)
        // Initialize Unity SDK:
        /*UnityAds.initialize(applicationContext,
            applicationContext.getString(R.string.unityads_id), unityAdsTestMode, this)*/
    }


    // unity ads init complete
    /*override fun onInitializationComplete() {
        Log.v(UNITYTAG, "UnityAds init complete")
    }

    // unity ads init failed
    override fun onInitializationFailed(p0: UnityAds.UnityAdsInitializationError?, p1: String?) {
        Log.v(UNITYTAG, "UnityAds init FAILED")

    }*/

}
