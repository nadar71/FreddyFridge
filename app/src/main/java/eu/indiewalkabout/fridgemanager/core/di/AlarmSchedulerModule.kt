package eu.indiewalkabout.fridgemanager.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.fridgemanager.core.reminder.withalarmmanager.AlarmReminderScheduler
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AlarmSchedulerModule {
}