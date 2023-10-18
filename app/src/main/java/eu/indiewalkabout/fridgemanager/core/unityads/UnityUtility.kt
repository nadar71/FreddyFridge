package eu.indiewalkabout.fridgemanager.core.unityads

import android.util.Log
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import eu.indiewalkabout.fridgemanager.App
import eu.indiewalkabout.fridgemanager.R


val unityGameID = App.getsContext()!!.getString(R.string.unityads_id)
val testMode = true
// Initialize the Unity Banner Ad with a margin
val marginDp = 10 // Set your desired margin in dp


// Listener for banner events:
val bannerListener = object : BannerView.IListener {
    override fun onBannerLoaded(bannerAdView: BannerView) {
        Log.v("UnityAdsExample", "onBannerLoaded: " + bannerAdView.placementId)
    }

    override fun onBannerFailedToLoad(bannerAdView: BannerView, errorInfo: BannerErrorInfo) {
        Log.e("UnityAdsExample", "Unity Ads failed to load banner for " + bannerAdView.placementId + " with error: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage)
        // Note that the BannerErrorInfo object can indicate a no fill (refer to the API documentation).
    }

    override fun onBannerClick(bannerAdView: BannerView) {
        Log.v("UnityAdsExample", "onBannerClick: " + bannerAdView.placementId)
    }

    override fun onBannerLeftApplication(bannerAdView: BannerView) {
        Log.v("UnityAdsExample", "onBannerLeftApplication: " + bannerAdView.placementId)
    }
}