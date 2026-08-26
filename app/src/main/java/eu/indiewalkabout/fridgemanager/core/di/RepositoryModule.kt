package eu.indiewalkabout.fridgemanager.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.FoodDbDao
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.feat_food.data.repository.FridgeManagerRepositoryImpl
import eu.indiewalkabout.fridgemanager.feat_settings.data.repository.SettingsRepositoryImpl
import eu.indiewalkabout.fridgemanager.feat_settings.domain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFridgeManagerRepository(
        foodDbDao: FoodDbDao
    ): FridgeManagerRepository {
        return FridgeManagerRepositoryImpl(foodDbDao)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl()
    }
}
