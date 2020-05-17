package eu.indiewalkabout.fridgemanager.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.Date


@Dao
interface FoodDbDao {

    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------
    // retrieve ALL KIND OF FOOD  without regarding expiring date
    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")
    fun loadAllFood(): LiveData<MutableList<FoodEntry>>

    // retrieve EXPIRING FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    fun loadAllFoodExpiring(date: Long?): LiveData<MutableList<FoodEntry>>

    // retrieve EXPIRING FOOD TODAY with LiveData
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND " +
            "EXPIRING_AT < :dayafter " + " and done == 0 ORDER BY EXPIRING_AT")
    fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): LiveData<MutableList<FoodEntry>>

    // retrieve EXPIRING FOOD TODAY NO LiveData
    /*
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND EXPIRING_AT < :dayafter " +
            " and done == 0 ORDER BY EXPIRING_AT")
    List<FoodEntry> loadFoodExpiringTodayNoLiveData(Long daybefore, Long dayafter);
    */

    // retrieve DEAD/EXPIRED FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT < :date and done == 0 ORDER BY EXPIRING_AT")
    fun loadAllFoodDead(date: Long?): LiveData<MutableList<FoodEntry>>

    // retrieve DONE/CONSUMED FOOD
    @Query("SELECT * FROM FOODLIST WHERE done == 1 ORDER BY EXPIRING_AT")
    fun loadAllFoodSaved(): LiveData<MutableList<FoodEntry>>

    @Query("SELECT * FROM FOODLIST WHERE id = :id")
    fun loadFoodById(id: Int): LiveData<FoodEntry>

    /*
    @Query("SELECT date('now')*1000")
    String showCurrentDate();
    */

    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    @Insert
    fun insertFoodEntry(foodEntry: FoodEntry)


    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFoodEntry(foodEntry: FoodEntry)

    @Query("UPDATE FOODLIST SET done=:done WHERE id = :id")
    fun updateDoneField(done: Int, id: Int)

    // delete single record
    @Delete
    fun deleteFoodEntry(foodEntry: FoodEntry)


    //----------------------------------------------------------------------------------------------
    //  DROP TABLE
    //----------------------------------------------------------------------------------------------
    // drop table
    @Query("DELETE FROM FOODLIST")
    fun dropTable()
}
