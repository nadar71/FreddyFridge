package eu.indiewalkabout.fridgemanager.core.unityads

import android.util.Log
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView


const val testMode = false
// Initialize the Unity Banner Ad with a margin
val marginDp = 10 // Set your desired margin in dp
const val UNITYTAG = "UnityAdsExample"


// Listener for banner events:
val bannerListener = object : BannerView.IListener {
    override fun onBannerLoaded(bannerAdView: BannerView) {
        Log.v(UNITYTAG, "onBannerLoaded: " + bannerAdView.placementId)
    }

    override fun onBannerFailedToLoad(bannerAdView: BannerView, errorInfo: BannerErrorInfo) {
        Log.e(UNITYTAG, "Unity Ads failed to load banner for " + bannerAdView.placementId
                + " with error: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage)
        // Note that the BannerErrorInfo object can indicate a no fill (refer to the API documentation).
    }

    override fun onBannerClick(bannerAdView: BannerView) {
        Log.v(UNITYTAG, "onBannerClick: " + bannerAdView.placementId)
    }

    override fun onBannerLeftApplication(bannerAdView: BannerView) {
        Log.v(UNITYTAG, "onBannerLeftApplication: " + bannerAdView.placementId)
    }
}