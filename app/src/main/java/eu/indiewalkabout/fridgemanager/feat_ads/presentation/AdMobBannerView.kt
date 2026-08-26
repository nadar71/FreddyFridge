package eu.indiewalkabout.fridgemanager.feat_ads.presentation

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
    val adWidth = LocalConfiguration.current.screenWidthDp
    if (FreddyFridgeApp.canRequestAdsFlag) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                AdView(context).apply {
                    setAdSize(getAdaptiveBannerSize(context, adWidth))
                    setAdUnitId(adUnitId)
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}


fun getAdaptiveBannerSize(context: Context, adWidth: Int): AdSize {
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
}
