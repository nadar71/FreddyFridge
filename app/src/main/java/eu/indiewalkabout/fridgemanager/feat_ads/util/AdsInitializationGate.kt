package eu.indiewalkabout.fridgemanager.feat_ads.util

internal class AdsInitializationGate(
    private val initialize: () -> Unit,
) {
    private var initialized = false

    @Synchronized
    fun initializeIfAllowed(canRequestAds: Boolean) {
        if (!canRequestAds || initialized) return
        initialized = true
        initialize()
    }
}
