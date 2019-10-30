package eu.indiewalkabout.fridgemanager


import android.arch.lifecycle.LiveData

import eu.indiewalkabout.fridgemanager.data.FoodDatabase
import eu.indiewalkabout.fridgemanager.data.FoodEntry

/**
 * -------------------------------------------------------------------------------------------------
 * App repository
 * -------------------------------------------------------------------------------------------------
 */
class FridgeManagerRepository private constructor(private val foodDb: FoodDatabase) {


    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------
    // retrieve ALL KIND OF FOOD  without regarding expiring date
    fun loadAllFood(): LiveData<List<FoodEntry>> {
        return foodDb.foodDbDao().loadAllFood()
    }

    // retrieve EXPIRING FOOD
    fun loadAllFoodExpiring(date: Long?): LiveData<List<FoodEntry>> {
        return foodDb.foodDbDao().loadAllFoodExpiring(date)
    }

    // retrieve EXPIRING FOOD TODAY with LiveData
    fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): LiveData<List<FoodEntry>> {
        return foodDb.foodDbDao().loadFoodExpiringToday(daybefore, dayafter)
    }

    // retrieve DEAD/EXPIRED FOOD
    fun loadAllFoodDead(date: Long?): LiveData<List<FoodEntry>> {
        return foodDb.foodDbDao().loadAllFoodDead(date)
    }

    // retrieve DONE/CONSUMED FOOD
    fun loadAllFoodSaved(): LiveData<List<FoodEntry>> {
        return foodDb.foodDbDao().loadAllFoodSaved()
    }

    fun loadFoodById(id: Int): LiveData<FoodEntry> {
        return foodDb.foodDbDao().loadFoodById(id)
    }


    //----------------------------------------------------------------------------------------------
    //  INSERT
    //----------------------------------------------------------------------------------------------
    fun insertFoodEntry(foodEntry: FoodEntry) {
        foodDb.foodDbDao().insertFoodEntry(foodEntry)
    }


    //----------------------------------------------------------------------------------------------
    //  UPDATE
    //----------------------------------------------------------------------------------------------

    fun updateFoodEntry(foodEntry: FoodEntry) {
        foodDb.foodDbDao().updateFoodEntry(foodEntry)
    }

    fun updateDoneField(done: Int, id: Int) {
        foodDb.foodDbDao().updateDoneField(done, id)
    }

    // delete single record
    fun deleteFoodEntry(foodEntry: FoodEntry) {
        foodDb.foodDbDao().deleteFoodEntry(foodEntry)
    }

    companion object {
        private val TAG = FridgeManagerRepository::class.java.simpleName

        private var sInstance: FridgeManagerRepository? = null


        fun getInstance(database: FoodDatabase): FridgeManagerRepository? {
            if (sInstance == null) {
                synchronized(FridgeManagerRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = FridgeManagerRepository(database)
                    }
                }
            }
            return sInstance
        }
    }


}