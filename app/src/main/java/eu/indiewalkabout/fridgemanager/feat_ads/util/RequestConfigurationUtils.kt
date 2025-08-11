package eu.indiewalkabout.fridgemanager.feat_ads.util

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp.Companion.TEST_DEVICE_ID


class RequestConfigurationUtils {

    companion object {
        fun setTestDeviceIds() {
            val testDeviceIds = listOf(TEST_DEVICE_ID)
            val configuration =
                RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
        }
    }
}