package eu.indiewalkabout.fridgemanager.feat_notifications.domain.reminder

import eu.indiewalkabout.fridgemanager.core.util.DateUtility
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadFoodExpiringForNotificationUseCase
import eu.indiewalkabout.fridgemanager.feat_food.domain.use_cases.LoadFoodExpiringTodayForNotificationUseCase
import javax.inject.Inject

data class ReminderNotificationFood(
    val expiringSoon: List<FoodEntry>,
    val expiringToday: List<FoodEntry>,
)

class ReminderNotificationCoordinator internal constructor(
    private val loadExpiringSoon: suspend (Long) -> List<FoodEntry>,
    private val loadExpiringToday: suspend (Long, Long) -> List<FoodEntry>,
) {
    @Inject
    constructor(
        loadExpiringSoonUseCase: LoadFoodExpiringForNotificationUseCase,
        loadExpiringTodayUseCase: LoadFoodExpiringTodayForNotificationUseCase,
    ) : this(
        loadExpiringSoon = { cutoff -> loadExpiringSoonUseCase(cutoff) },
        loadExpiringToday = { dayBefore, dayAfter ->
            loadExpiringTodayUseCase(dayBefore, dayAfter)
        },
    )

    suspend fun load(
        localTodayMidnight: Long,
        daysBeforeDeadline: Int,
    ): ReminderNotificationFood {
        val expiringSoonCutoff =
            localTodayMidnight + daysBeforeDeadline * DateUtility.DAY_IN_MILLIS
        val dayBefore = localTodayMidnight - DateUtility.DAY_IN_MILLIS
        val dayAfter = localTodayMidnight + DateUtility.DAY_IN_MILLIS

        return ReminderNotificationFood(
            expiringSoon = loadExpiringSoon(expiringSoonCutoff),
            expiringToday = loadExpiringToday(dayBefore, dayAfter),
        )
    }
}
