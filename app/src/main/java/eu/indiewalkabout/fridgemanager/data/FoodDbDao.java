package eu.indiewalkabout.fridgemanager.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.Date;


// using strftime('%s','now')*1000 or date('now') ?!?

@Dao
public interface FoodDbDao {

    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")
    List<FoodEntry> loadAllFood();



    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT >= :date ORDER BY EXPIRING_AT")
    List<FoodEntry> loadAllFoodExpiring(Long date);

    @Query("SELECT * FROM FOODLIST WHERE EXPIRING_AT < :date ORDER BY EXPIRING_AT")
    List<FoodEntry> loadAllFoodDead(Long date);


    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")  // TODO : where consumed flag true
    List<FoodEntry> loadAllFoodSaved();

    @Query("SELECT date('now')*1000")  // TODO : where consumed flag true
    String showCurrentDate();

    @Insert
    void insertFoodEntry(FoodEntry foodEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFoodEntry(FoodEntry foodEntry);

    @Delete
    void deleteFoodEntry(FoodEntry foodEntry);

    // drop table
    @Query("DELETE FROM FOODLIST")
    public void dropTable();
}
