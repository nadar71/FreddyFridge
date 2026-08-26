package eu.indiewalkabout.fridgemanager.feat_settings.domain.repository

interface SettingsRepository {
    val daysBeforeDeadline: Int
    val dailyNotificationCount: Int

    fun updateDaysBeforeDeadline(value: Int)
    fun updateDailyNotificationCount(value: Int)
    fun clear()
}
