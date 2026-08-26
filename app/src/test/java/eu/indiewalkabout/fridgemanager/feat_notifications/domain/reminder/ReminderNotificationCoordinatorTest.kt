package eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder

import eu.indiewalkabout.fridgemanager.core.util.DateUtility
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderNotificationCoordinatorTest {

    @Test
    fun `loads next-days and today food using the expected date boundaries`() = runBlocking {
        val expiringSoon = listOf(FoodEntry(id = 1, name = "Milk"))
        val expiringToday = listOf(FoodEntry(id = 2, name = "Yogurt"))
        var requestedCutoff: Long? = null
        var requestedTodayBounds: Pair<Long, Long>? = null
        val coordinator = ReminderNotificationCoordinator(
            loadExpiringSoon = { cutoff ->
                requestedCutoff = cutoff
                expiringSoon
            },
            loadExpiringToday = { dayBefore, dayAfter ->
                requestedTodayBounds = dayBefore to dayAfter
                expiringToday
            },
        )
        val todayMidnight = 10 * DateUtility.DAY_IN_MILLIS

        val result = coordinator.load(
            localTodayMidnight = todayMidnight,
            daysBeforeDeadline = 3,
        )

        assertEquals(todayMidnight + 3 * DateUtility.DAY_IN_MILLIS, requestedCutoff)
        assertEquals(
            todayMidnight - DateUtility.DAY_IN_MILLIS to
                todayMidnight + DateUtility.DAY_IN_MILLIS,
            requestedTodayBounds,
        )
        assertEquals(ReminderNotificationFood(expiringSoon, expiringToday), result)
    }

    @Test
    fun `preserves empty query results`() = runBlocking {
        val coordinator = ReminderNotificationCoordinator(
            loadExpiringSoon = { emptyList() },
            loadExpiringToday = { _, _ -> emptyList() },
        )

        val result = coordinator.load(
            localTodayMidnight = 5 * DateUtility.DAY_IN_MILLIS,
            daysBeforeDeadline = 2,
        )

        assertEquals(ReminderNotificationFood(emptyList(), emptyList()), result)
    }
}
