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

    // no live data : TODO : to be renamed after deleting live data ones
    @Query("SELECT * FROM FOODLIST WHERE done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadAllFood_no_livedata(): MutableList<FoodEntry>

    // retrieve EXPIRING FOOD no livedata
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadAllFoodExpiring_no_livedata(date: Long?): MutableList<FoodEntry>

    // retrieve EXPIRING FOOD TODAY no LiveData
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND EXPIRING_AT < :dayafter " +
            " and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadFoodExpiringToday_no_livedata(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry>

    // retrieve DEAD/EXPIRED FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT < :date and done == 0 ORDER BY EXPIRING_AT")
    suspend fun loadAllFoodDead_no_livedata(date: Long?): MutableList<FoodEntry>

    // retrieve DONE/CONSUMED FOOD
    @Query("SELECT * FROM FOODLIST WHERE done == 1 ORDER BY EXPIRING_AT")
    suspend fun loadAllFoodSaved_no_livedata(): MutableList<FoodEntry>

    // retrieve single record by id
    @Query("SELECT * FROM FOODLIST WHERE id = :id")
    suspend fun loadFoodById_no_livedata(id: Int): FoodEntry

    //----------------------------------------- INSERT ---------------------------------------------
    @Insert
    suspend fun insertFoodEntry(foodEntry: FoodEntry)


    //------------------------------------------ UPDATE---------------------------------------------
    @Update(onConflict = OnConflictStrategy.REPLACE)
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
