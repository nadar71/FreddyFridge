package eu.indiewalkabout.fridgemanager.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.Date;


@Dao
public interface FoodDbDao {

    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------
    // retrieve ALL KIND OF FOOD  without regarding exipring date
    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")
    List<FoodEntry> loadAllFood();

    // retrieve EXPIRING FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date and done == 0 ORDER BY EXPIRING_AT")
    List<FoodEntry> loadAllFoodExpiring(Long date);

    // retrieve DEAD/EXPIRED FOOD
    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT < :date and done == 0 ORDER BY EXPIRING_AT")
    List<FoodEntry> loadAllFoodDead(Long date);

    // retrieve DONE/CONSUMED FOOD
    @Query("SELECT * FROM FOODLIST WHERE done == 1 ORDER BY EXPIRING_AT")
    List<FoodEntry> loadAllFoodSaved();

    /*
    @Query("SELECT date('now')*1000")
    String showCurrentDate();
    */

    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    @Insert
    void insertFoodEntry(FoodEntry foodEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFoodEntry(FoodEntry foodEntry);


    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------
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
