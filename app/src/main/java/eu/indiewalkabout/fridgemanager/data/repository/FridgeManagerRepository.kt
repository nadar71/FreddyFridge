package eu.indiewalkabout.fridgemanager.data.repository

import androidx.lifecycle.LiveData
import eu.indiewalkabout.fridgemanager.data.local.db.FoodDatabase
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry

class FridgeManagerRepository private constructor(private val foodDb: FoodDatabase) {


    //----------------------------------------------------------------------------------------------
    //  QUERY
    //----------------------------------------------------------------------------------------------

    // retrieve ALL KIND OF FOOD  without regarding expiring date
    fun loadAllFood(): LiveData<MutableList<FoodEntry>> {
        return foodDb.foodDbDao().loadAllFood()
    }

    // retrieve EXPIRING FOOD
    fun loadAllFoodExpiring(date: Long?): LiveData<MutableList<FoodEntry>> {
        return foodDb.foodDbDao().loadAllFoodExpiring(date)
    }

    // retrieve EXPIRING FOOD no livedata
    suspend fun loadAllFoodExpiring_no_livedata(date: Long?): MutableList<FoodEntry> {
        return foodDb.foodDbDao().loadAllFoodExpiring_no_livedata(date)
    }

    // retrieve EXPIRING FOOD TODAY with LiveData
    fun loadFoodExpiringToday(daybefore: Long?, dayafter: Long?): LiveData<MutableList<FoodEntry>> {
        return foodDb.foodDbDao().loadFoodExpiringToday(daybefore, dayafter)
    }

    // retrieve EXPIRING FOOD TODAY no LiveData
    suspend fun loadFoodExpiringToday_no_livedata(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry> {
        return foodDb.foodDbDao().loadFoodExpiringToday_no_livedata(daybefore, dayafter)
    }

    // retrieve DEAD/EXPIRED FOOD
    fun loadAllFoodDead(date: Long?): LiveData<MutableList<FoodEntry>> {
        return foodDb.foodDbDao().loadAllFoodDead(date)
    }

    // retrieve DONE/CONSUMED FOOD
    fun loadAllFoodSaved(): LiveData<MutableList<FoodEntry>> {
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