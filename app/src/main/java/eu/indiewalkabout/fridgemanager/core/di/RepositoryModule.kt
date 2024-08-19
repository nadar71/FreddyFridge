package eu.indiewalkabout.fridgemanager.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.fridgemanager.data.local.db.FoodDatabase
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFridgeManagerRepository(database: FoodDatabase): FridgeManagerRepository {
        return FridgeManagerRepository(database)
    }
}