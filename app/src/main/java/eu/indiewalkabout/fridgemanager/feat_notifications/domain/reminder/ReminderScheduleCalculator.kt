package eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder

import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

object ReminderScheduleCalculator {
    private val startTime = LocalTime.of(8, 0)
    private val endTime = LocalTime.of(21, 0)
    private val pastSlotGracePeriod = Duration.ofMinutes(30)

    fun calculate(
        now: Instant,
        zoneId: ZoneId,
        notificationsPerDay: Int,
    ): List<Instant> {
        if (notificationsPerDay <= 0) return emptyList()

        val nowInZone = now.atZone(zoneId)
        val scheduleDate = if (nowInZone.toLocalTime() >= endTime) {
            nowInZone.toLocalDate().plusDays(1)
        } else {
            nowInZone.toLocalDate()
        }
        val windowMinutes = Duration.between(startTime, endTime).toMinutes()
        val earliestAllowedSlot = now.minus(pastSlotGracePeriod)

        return List(notificationsPerDay) { index ->
            val minuteOffset = index * windowMinutes / notificationsPerDay
            scheduleDate.atTime(startTime).plusMinutes(minuteOffset).atZone(zoneId).toInstant()
        }.filterNot { it.isBefore(earliestAllowedSlot) }
    }
}
