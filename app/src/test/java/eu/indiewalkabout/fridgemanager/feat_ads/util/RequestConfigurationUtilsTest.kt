package eu.indiewalkabout.fridgemanager.feat_ads.util

import org.junit.Assert.assertEquals
import org.junit.Test

class RequestConfigurationUtilsTest {

    @Test
    fun `get test device ids returns configured device in debug builds`() {
        val testDeviceIds = RequestConfigurationUtils.getTestDeviceIds(
            isDebug = true,
            testDeviceId = "TEST-DEVICE-ID",
        )

        assertEquals(listOf("TEST-DEVICE-ID"), testDeviceIds)
    }

    @Test
    fun `get test device ids returns empty list in release builds`() {
        val testDeviceIds = RequestConfigurationUtils.getTestDeviceIds(
            isDebug = false,
            testDeviceId = "TEST-DEVICE-ID",
        )

        assertEquals(emptyList<String>(), testDeviceIds)
    }
}
