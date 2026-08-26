package eu.indiewalkabout.fridgemanager.feat_ads.util

import android.content.Context
import com.google.android.gms.ads.MobileAds

object MobileAdsInitializer {
    private val gate = AdsInitializationGate {
        appContext?.let { MobileAds.initialize(it) }
    }
    private var appContext: Context? = null

    fun initializeIfAllowed(context: Context, canRequestAds: Boolean) {
        appContext = context.applicationContext
        gate.initializeIfAllowed(canRequestAds)
    }
}
