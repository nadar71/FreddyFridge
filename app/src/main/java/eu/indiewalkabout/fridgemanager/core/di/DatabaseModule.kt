package eu.indiewalkabout.fridgemanager.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.fridgemanager.feat_food.data.local.db.FoodDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFoodDatabase(
        @ApplicationContext context: Context
    ): FoodDatabase {
        return FoodDatabase.getDbInstance(context)
    }

    @Provides
    @Singleton
    fun provideFoodDao(foodDatabase: FoodDatabase) = foodDatabase.foodDbDao()

}

