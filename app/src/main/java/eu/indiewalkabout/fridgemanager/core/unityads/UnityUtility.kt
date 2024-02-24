package eu.indiewalkabout.fridgemanager.core.unityads

import android.app.Activity
import android.util.Log
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import com.unity3d.scar.adapter.v1920.R.attr.adUnitId
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView


const val unityAdsTestMode = true
// Initialize the Unity Banner Ad with a margin
val marginDp = 10 // Set your desired margin in dp
const val UNITYTAG = "UnityAdsExample"
var unityId: String = ""


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


fun createLoadListener(activity: Activity): IUnityAdsLoadListener {
    return object : IUnityAdsLoadListener {
        override fun onUnityAdsAdLoaded(placementId: String) {
            UnityAds.show(activity, adUnitId.toString(), UnityAdsShowOptions(), showListener)
        }
        override fun onUnityAdsFailedToLoad(placementId: String, error: UnityAds.UnityAdsLoadError, message: String) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for $placementId with error: [$error] $message")
        }
    }
}



val showListener = object : IUnityAdsShowListener {
    override fun onUnityAdsShowFailure(placementId: String, error: UnityAds.UnityAdsShowError, message: String) {
        Log.e(UNITYTAG, "Unity Ads failed to show ad for $placementId with error: [$error] $message")
    }

    override fun onUnityAdsShowStart(placementId: String) {
        Log.v(UNITYTAG, "onUnityAdsShowStart: $placementId")
    }

    override fun onUnityAdsShowClick(placementId: String) {
        Log.v(UNITYTAG, "onUnityAdsShowClick: $placementId")
    }

    override fun onUnityAdsShowComplete(placementId: String, state: UnityAds.UnityAdsShowCompletionState) {
        Log.v(UNITYTAG, "onUnityAdsShowComplete: $placementId")
    }
}


fun displayInterstitialAd(activity: Activity) {
    UnityAds.load(unityId, createLoadListener(activity))
}

