package eu.indiewalkabout.fridgemanager.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.FoodDbDao
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.feat_food.data.repository.FridgeManagerRepositoryImpl
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