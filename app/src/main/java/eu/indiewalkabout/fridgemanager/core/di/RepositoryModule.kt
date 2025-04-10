package eu.indiewalkabout.fridgemanager.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.fridgemanager.data.local.db.FoodDatabase
import eu.indiewalkabout.fridgemanager.data.local.db.FoodDbDao
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.domain.repository.FridgeManagerRepositoryImpl
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
}