package eu.indiewalkabout.fridgemanager.core.di

import androidx.work.WorkerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.fridgemanager.core.reminder.withworkmanager.FoodReminderWorkerFactory
import javax.inject.Singleton

// Bind the custom worker factory with the Hilt scope
@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerBindingModule {
    @Binds
    @Singleton
    abstract fun bindWorkerFactory(foodReminderWorkerFactory: FoodReminderWorkerFactory): WorkerFactory
}