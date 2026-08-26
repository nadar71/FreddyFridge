package eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ReminderScheduleCalculatorTest {

    private val zone = ZoneId.of("Europe/Rome")

    @Test
    fun `zero notifications produces no schedule`() {
        val result = ReminderScheduleCalculator.calculate(
            now = instantAt(2026, 8, 26, 7, 0),
            zoneId = zone,
            notificationsPerDay = 0,
        )

        assertEquals(emptyList<Instant>(), result)
    }

    @Test
    fun `daily slots are distributed through the notification window`() {
        val result = ReminderScheduleCalculator.calculate(
            now = instantAt(2026, 8, 26, 7, 0),
            zoneId = zone,
            notificationsPerDay = 3,
        )

        assertEquals(
            listOf(
                instantAt(2026, 8, 26, 8, 0),
                instantAt(2026, 8, 26, 12, 20),
                instantAt(2026, 8, 26, 16, 40),
            ),
            result,
        )
    }

    @Test
    fun `slot within thirty minute grace period is retained`() {
        val result = ReminderScheduleCalculator.calculate(
            now = instantAt(2026, 8, 26, 8, 20),
            zoneId = zone,
            notificationsPerDay = 1,
        )

        assertEquals(listOf(instantAt(2026, 8, 26, 8, 0)), result)
    }

    @Test
    fun `late day startup schedules the next day`() {
        val result = ReminderScheduleCalculator.calculate(
            now = instantAt(2026, 8, 26, 21, 0),
            zoneId = zone,
            notificationsPerDay = 2,
        )

        assertEquals(
            listOf(
                instantAt(2026, 8, 27, 8, 0),
                instantAt(2026, 8, 27, 14, 30),
            ),
            result,
        )
    }

    @Test
    fun `schedule keeps local wall clock times across daylight saving transition`() {
        val result = ReminderScheduleCalculator.calculate(
            now = instantAt(2026, 10, 24, 21, 0),
            zoneId = zone,
            notificationsPerDay = 2,
        )

        assertEquals(
            listOf(
                instantAt(2026, 10, 25, 8, 0),
                instantAt(2026, 10, 25, 14, 30),
            ),
            result,
        )
    }

    private fun instantAt(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Instant = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, zone).toInstant()
}
