package eu.indiewalkabout.fridgemanager.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface FoodDbDao {

    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")  // TODO : set < data
    List<FoodEntry> loadAllFoodExpiring();

    @Query("SELECT * FROM FOODLIST ORDER BY EXPIRING_AT")  // TODO : set > data
    List<FoodEntry> loadAllFoodDead();

    @Insert
    void insertFoodEntry(FoodEntry foodEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFoodEntry(FoodEntry foodEntry);

    @Delete
    void deleteFoodEntry(FoodEntry foodEntry);
}
