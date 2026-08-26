package eu.indiewalkabout.fridgemanager.feat_ads.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AdsInitializationGateTest {

    @Test
    fun `does not initialize ads when consent does not allow requests`() {
        var initializationCount = 0
        val gate = AdsInitializationGate { initializationCount++ }

        gate.initializeIfAllowed(canRequestAds = false)

        assertEquals(0, initializationCount)
    }

    @Test
    fun `initializes ads once when consent allows requests`() {
        var initializationCount = 0
        val gate = AdsInitializationGate { initializationCount++ }

        gate.initializeIfAllowed(canRequestAds = true)
        gate.initializeIfAllowed(canRequestAds = true)

        assertEquals(1, initializationCount)
    }

    @Test
    fun `can initialize after an earlier denied result`() {
        var initializationCount = 0
        val gate = AdsInitializationGate { initializationCount++ }

        gate.initializeIfAllowed(canRequestAds = false)
        gate.initializeIfAllowed(canRequestAds = true)

        assertEquals(1, initializationCount)
    }
}
