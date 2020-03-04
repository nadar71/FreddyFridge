package eu.indiewalkabout.fridgemanager.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface FoodDbDao {

    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------
    // retrieve ALL KIND OF FOOD  without regarding exipring date
    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")
    LiveData<List<FoodEntry>> loadAllFood();

    // retrieve EXPIRING FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    LiveData<List<FoodEntry>> loadAllFoodExpiring(Long date);

    // retrieve EXPIRING FOOD TODAY with LiveData
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND EXPIRING_AT < :dayafter " +
            " and done == 0 ORDER BY EXPIRING_AT")
    LiveData<List<FoodEntry>> loadFoodExpiringToday(Long daybefore, Long dayafter);

    // retrieve EXPIRING FOOD TODAY NO LiveData
    /*
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT > :daybefore AND EXPIRING_AT < :dayafter " +
            " and done == 0 ORDER BY EXPIRING_AT")
    List<FoodEntry> loadFoodExpiringTodayNoLiveData(Long daybefore, Long dayafter);
    */

    // retrieve DEAD/EXPIRED FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT < :date and done == 0 ORDER BY EXPIRING_AT")
    LiveData<List<FoodEntry>> loadAllFoodDead(Long date);

    // retrieve DONE/CONSUMED FOOD
    @Query("SELECT * FROM FOODLIST WHERE done == 1 ORDER BY EXPIRING_AT")
    LiveData<List<FoodEntry>> loadAllFoodSaved();

    @Query("SELECT * FROM FOODLIST WHERE id = :id" )
    LiveData<FoodEntry> loadFoodById(int id);

    /*
    @Query("SELECT date('now')*1000")
    String showCurrentDate();
    */

    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    @Insert
    void insertFoodEntry(FoodEntry foodEntry);



    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFoodEntry(FoodEntry foodEntry);

    @Query("UPDATE FOODLIST SET done=:done WHERE id = :id")
    void updateDoneField(int done, int id);

    // delete single record
    @Delete
    void deleteFoodEntry(FoodEntry foodEntry);


    //----------------------------------------------------------------------------------------------
    //  DROP TABLE
    //----------------------------------------------------------------------------------------------
    // drop table
    @Query("DELETE FROM FOODLIST")
    public void dropTable();
}
