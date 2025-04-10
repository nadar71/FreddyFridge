package eu.indiewalkabout.fridgemanager.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry


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



    // -------------------> with live data ( temporary retrocompatibility) <-- TODO : to be deleted after no more using
    // retrieve ALL KIND OF FOOD  without regarding expiring date
    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")
    fun loadAllFood(): LiveData<MutableList<FoodEntry>>

    // retrieve EXPIRING FOOD with livedata
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    fun loadAllFoodExpiring(date: Long?): LiveData<MutableList<FoodEntry>>

    // retrieve EXPIRING FOOD TODAY with LiveData
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND " +
            "EXPIRING_AT < :dayafter " + " and done == 0 ORDER BY EXPIRING_AT")
    fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): LiveData<MutableList<FoodEntry>>


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

    //----------------------------------------- INSERT ---------------------------------------------
    @Insert
    fun insertFoodEntry(foodEntry: FoodEntry)


    //------------------------------------------ UPDATE---------------------------------------------
      @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFoodEntry(foodEntry: FoodEntry)

    @Query("UPDATE FOODLIST SET done=:done WHERE id = :id")
    fun updateDoneField(done: Int, id: Int)

    // delete single record
    @Delete
    fun deleteFoodEntry(foodEntry: FoodEntry)


    //------------------------------------------- DROP TABLE ---------------------------------------
    // drop table
    @Query("DELETE FROM FOODLIST")
    fun dropTable()
}
