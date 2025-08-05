package eu.indiewalkabout.fridgemanager.feat_food.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry


@Dao
interface FoodDbDao {

    //------------------------------------  QUERY --------------------------------------------------

    @Query("SELECT * FROM FOODLIST WHERE done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadAllFood(): MutableList<FoodEntry>

    // get EXPIRING FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadAllFoodExpiring(date: Long?): MutableList<FoodEntry>

    // get EXPIRING FOOD TODAY
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND EXPIRING_AT < :dayafter " +
            " and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry>

    // get DEAD/EXPIRED FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT < :date and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadAllFoodDead(date: Long?): MutableList<FoodEntry>

    // get DONE/CONSUMED FOOD
    @Query("SELECT * FROM FOODLIST WHERE done == 1 ORDER BY EXPIRING_AT")
    suspend fun loadAllFoodSaved(): MutableList<FoodEntry>

    // get single record by id
    @Query("SELECT * FROM FOODLIST WHERE id = :id")
    suspend fun loadFoodById(id: Int): FoodEntry

    //----------------------------------------- INSERT ---------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)


    //------------------------------------------ UPDATE---------------------------------------------
    @Update
    suspend fun updateFoodEntry(foodEntry: FoodEntry)

    @Query("UPDATE FOODLIST SET done=:done WHERE id = :id")
    suspend fun updateDoneField(done: Int, id: Int)

    // delete single record
    @Delete
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)


    //------------------------------------------- DROP TABLE ---------------------------------------
    // drop table
    @Query("DELETE FROM FOODLIST")
    suspend fun dropTable()
}
