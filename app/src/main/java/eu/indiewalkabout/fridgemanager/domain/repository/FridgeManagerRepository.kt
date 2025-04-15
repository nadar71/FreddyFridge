package eu.indiewalkabout.fridgemanager.domain.repository

import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry

interface FridgeManagerRepository {
    //----------------------------------- QUERY ----------------------------------------------------
    suspend fun loadAllFood_no_livedata(): MutableList<FoodEntry>
    suspend fun loadAllFoodExpiring_no_livedata(date: Long?): MutableList<FoodEntry>
    suspend fun loadFoodExpiringToday_no_livedata(daybefore: Long?, dayafter: Long?): MutableList<FoodEntry>
    suspend fun loadAllFoodDead_no_livedata(date: Long?): MutableList<FoodEntry>
    suspend fun loadAllFoodSaved_no_livedata(): MutableList<FoodEntry>
    suspend fun loadFoodById_no_livedata(id: Int): FoodEntry
    //----------------------------------------- INSERT ---------------------------------------------
    fun insertFoodEntry(foodEntry: FoodEntry)
    //------------------------------------------ UPDATE---------------------------------------------
    fun updateFoodEntry(foodEntry: FoodEntry)
    fun updateDoneField(done: Int, id: Int)
    fun deleteFoodEntry(foodEntry: FoodEntry)
    //------------------------------------------- DROP TABLE ---------------------------------------
    fun dropTable()


}