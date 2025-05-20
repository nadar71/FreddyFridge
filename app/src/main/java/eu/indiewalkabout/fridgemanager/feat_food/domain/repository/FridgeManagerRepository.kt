package eu.indiewalkabout.fridgemanager.feat_food.domain.repository

import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry

interface FridgeManagerRepository {
    //----------------------------------- QUERY ----------------------------------------------------
    suspend fun loadAllFood_no_livedata(): MutableList<FoodEntry>
    suspend fun loadAllFoodExpiring_no_livedata(date: Long?): MutableList<FoodEntry>
    suspend fun loadFoodExpiringToday_no_livedata(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry>
    suspend fun loadAllFoodDead_no_livedata(date: Long?): MutableList<FoodEntry>
    suspend fun loadAllFoodSaved_no_livedata(): MutableList<FoodEntry>
    suspend fun loadFoodById_no_livedata(id: Int): FoodEntry
    //----------------------------------------- INSERT ---------------------------------------------
    suspend fun insertFoodEntry(foodEntry: FoodEntry)
    //------------------------------------------ UPDATE---------------------------------------------
    suspend fun updateFoodEntry(foodEntry: FoodEntry)
    suspend fun updateDoneField(done: Int, id: Int)
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)
    //------------------------------------------- DROP TABLE ---------------------------------------
    suspend fun dropTable()


}