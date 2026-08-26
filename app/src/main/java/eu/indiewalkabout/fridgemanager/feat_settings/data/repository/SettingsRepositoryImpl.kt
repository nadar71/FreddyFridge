package eu.indiewalkabout.fridgemanager.feat_settings.data.repository

import eu.indiewalkabout.fridgemanager.core.data.locals.AppPreferences
import eu.indiewalkabout.fridgemanager.feat_settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor() : SettingsRepository {
    override val daysBeforeDeadline: Int
        get() = AppPreferences.days_before_deadline

    override val dailyNotificationCount: Int
        get() = AppPreferences.daily_notifications_number

    override fun updateDaysBeforeDeadline(value: Int) {
        AppPreferences.days_before_deadline = value
    }

    override fun updateDailyNotificationCount(value: Int) {
        AppPreferences.daily_notifications_number = value
    }

    override fun clear() {
        AppPreferences.clear()
    }
}
