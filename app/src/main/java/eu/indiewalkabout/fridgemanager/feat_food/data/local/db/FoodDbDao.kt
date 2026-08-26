package eu.indiewalkabout.fridgemanager.feat_food.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import kotlinx.coroutines.flow.Flow


@Dao
interface FoodDbDao {

    //------------------------------------  QUERY --------------------------------------------------

    // detect ALL FOOD changes
    @Query("SELECT * FROM FOODLIST WHERE done == 0 ORDER BY EXPIRING_AT")
    fun observeAllFood(): Flow<List<FoodEntry>>

    // detect EXPIRING FOOD changes
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    fun observeAllFoodExpiring(date: Long?): Flow<List<FoodEntry>>

    // get EXPIRING FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadAllFoodExpiring(date: Long?): List<FoodEntry>

    // detect EXPIRING FOOD TODAY changes
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND EXPIRING_AT < :dayafter " +
            " and done == 0 ORDER BY EXPIRING_AT")
    fun observeFoodExpiringToday(daybefore: Long?, dayafter: Long?): Flow<List<FoodEntry>>

    // get EXPIRING FOOD TODAY
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND EXPIRING_AT < :dayafter " +
            " and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): List<FoodEntry>

    // detect DEAD/EXPIRED FOOD changes
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT < :date and done == 0 ORDER BY EXPIRING_AT")
    fun observeAllFoodExpired(date: Long?): Flow<List<FoodEntry>>

    // get DEAD/EXPIRED FOOD
    // detect DONE/CONSUMED FOOD changes
    @Query("SELECT * FROM FOODLIST WHERE done == 1 ORDER BY EXPIRING_AT")
    fun observeAllFoodConsumed(): Flow<List<FoodEntry>>

    //----------------------------------------- INSERT ---------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)


    //------------------------------------------ UPDATE---------------------------------------------
    @Update
    suspend fun updateFoodEntry(foodEntry: FoodEntry)

    // delete single record
    @Delete
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)
}
