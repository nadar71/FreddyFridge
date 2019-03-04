package eu.indiewalkabout.fridgemanager;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

/**
 * -------------------------------------------------------------------------------------------------
 * App repository
 * -------------------------------------------------------------------------------------------------
 */
public class FridgeManagerRepository {
    private static final String TAG = FridgeManagerRepository.class.getSimpleName();

    private static FridgeManagerRepository sInstance;

    private final FoodDatabase foodDb;


    private FridgeManagerRepository(FoodDatabase db){
        this.foodDb = db;
    }


    public static FridgeManagerRepository getInstance(final FoodDatabase database) {
        if (sInstance == null) {
            synchronized (FridgeManagerRepository.class) {
                if (sInstance == null) {
                    sInstance = new FridgeManagerRepository(database);
                }
            }
        }
        return sInstance;
    }


    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------
    // retrieve ALL KIND OF FOOD  without regarding expiring date
    public LiveData<List<FoodEntry>> loadAllFood(){
        return foodDb.foodDbDao().loadAllFood();
    }

    // retrieve EXPIRING FOOD
    public LiveData<List<FoodEntry>> loadAllFoodExpiring(Long date){
        return foodDb.foodDbDao().loadAllFoodExpiring(date);
    }

    // retrieve EXPIRING FOOD TODAY with LiveData
    public LiveData<List<FoodEntry>> loadFoodExpiringToday(Long daybefore, Long dayafter){
        return foodDb.foodDbDao().loadFoodExpiringToday(daybefore,dayafter);
    }

    // retrieve DEAD/EXPIRED FOOD
    public LiveData<List<FoodEntry>> loadAllFoodDead(Long date){
        return foodDb.foodDbDao().loadAllFoodDead(date);
    }

    // retrieve DONE/CONSUMED FOOD
    public LiveData<List<FoodEntry>> loadAllFoodSaved(){
        return foodDb.foodDbDao().loadAllFoodSaved();
    }

    public LiveData<FoodEntry> loadFoodById(int id){
        return foodDb.foodDbDao().loadFoodById(id);
    }


    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    public void insertFoodEntry(FoodEntry foodEntry){
        foodDb.foodDbDao().insertFoodEntry(foodEntry);
    }



    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------

    public void updateFoodEntry(FoodEntry foodEntry){
        foodDb.foodDbDao().updateFoodEntry(foodEntry);
    }

    public void updateDoneField(int done, int id){
        foodDb.foodDbDao().updateDoneField(done, id);
    }

    // delete single record
    public void deleteFoodEntry(FoodEntry foodEntry){
        foodDb.foodDbDao().deleteFoodEntry(foodEntry);
    }









}
