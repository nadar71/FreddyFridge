package eu.indiewalkabout.fridgemanager.feat_ads.presentation

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import eu.indiewalkabout.fridgemanager.FreddyFridgeApp

@Composable
fun AdMobBannerView(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(60.dp),
    adUnitId: String
) {
    if (FreddyFridgeApp.canRequestAdsFlag) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                AdView(context).apply {
                    setAdSize(getAdaptiveBannerSize(context))
                    setAdUnitId(adUnitId)
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}


fun getAdaptiveBannerSize(context: Context): AdSize {
    val display = (context as Activity).windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)

    val density = outMetrics.density
    val adWidth = (outMetrics.widthPixels / density).toInt()

    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
}

