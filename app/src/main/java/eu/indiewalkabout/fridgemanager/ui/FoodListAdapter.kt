package eu.indiewalkabout.fridgemanager.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import eu.indiewalkabout.fridgemanager.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.App
import eu.indiewalkabout.fridgemanager.App.Companion.getsContext
import eu.indiewalkabout.fridgemanager.data.db.DateConverter
import eu.indiewalkabout.fridgemanager.data.model.FoodEntry
import eu.indiewalkabout.fridgemanager.util.DateUtility
import eu.indiewalkabout.fridgemanager.util.DateUtility.DAY_IN_MILLIS
import java.text.SimpleDateFormat
import java.util.*

class FoodListAdapter(private val thisContext: Context, // Handle item clicks
                      private val foodItemClickListener: ItemClickListener,
                      val listType: String)
    : androidx.recyclerview.widget.RecyclerView.Adapter<FoodViewRowHolder>() {

    companion object {
        val TAG = FoodListAdapter::class.java.simpleName
    }

    val DATE_FORMAT = "dd/MM/yyy"
    // Holds food entries data
    internal var adapterFoodEntries: MutableList<FoodEntry>? = null

    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val repository: FridgeManagerRepository?


    init {
        // TODO : move onClick management to MainActivity
        repository = (getsContext() as App?)!!.repository
    }

    // Inflate row layout.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewRowHolder {
        // Inflate the food_row_layout to each view
        val view = LayoutInflater.from(thisContext)
                .inflate(R.layout.food_row_layout, parent, false)
        return FoodViewRowHolder(view, this,
                thisContext, foodItemClickListener)
    }


     // ----------------------------------------------------------------------------------
     // Display data at a specified position in the Cursor.
     // @param holder   ViewHolder to bind Cursor data to
     // @param position dataposition in Cursor

    override fun onBindViewHolder(holder: FoodViewRowHolder, position: Int) {
        // Determine the values of the wanted data
        val foodEntry = adapterFoodEntries!![position]
        val foodName = foodEntry.name
        val expiringAtDate: Date = foodEntry.expiringAt!!
        val expiringAtString = dateFormat.format(foodEntry.expiringAt!!)

        //Set values
        holder.foodName_tv.text = foodName
        holder.expiringDate_tv.text = expiringAtString

        setColorByExpirationDayDifference(expiringAtDate, foodName, holder)
    }


    // Different color while the date expiration approximates
    private fun setColorByExpirationDayDifference(expiringAtDate: Date, foodName: String?, holder: FoodViewRowHolder) {
        val dataNormalizedAtMidnight = DateUtility
                .getLocalMidnightFromNormalizedUtcDate(DateUtility
                        .normalizedUtcMsForToday)
        val daysBefore = DateConverter.fromDate(expiringAtDate)
                ?.minus(dataNormalizedAtMidnight)
                ?.div(DAY_IN_MILLIS)
                ?.let { Math.floor(it.toDouble()) }

        Log.i(TAG, "onBindViewHolder: for $foodName daysBefore : $daysBefore")

        if (daysBefore != null) {
            when (daysBefore.toInt()) {
                0 -> holder.recyclerview_item.setBackgroundColor(getColor(getsContext()!!, R.color.food_red))
                1 -> holder.recyclerview_item.setBackgroundColor(getColor(getsContext()!!, R.color.food_orange))
                2 -> holder.recyclerview_item.setBackgroundColor(getColor(getsContext()!!, R.color.food_yellow))
                3 -> holder.recyclerview_item.setBackgroundColor(getColor(getsContext()!!, R.color.food_green))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (adapterFoodEntries == null) {
            0
        } else adapterFoodEntries!!.size
    }

    /**
     * ----------------------------------------------------------------------------------
     * Set data for RecycleView as foodEntries list.
     * Used by the activity to init the adapter
     * @param foodEntries
     * ----------------------------------------------------------------------------------
     */
    fun setFoodEntries(foodEntries: MutableList<FoodEntry>?) {
        this.adapterFoodEntries = foodEntries

        // data changed, refresh the view : notify the related observers
        notifyDataSetChanged()
    }

    // ----------------------------------------------------------------------------------
    // Implemented in calling class, e.g. MainActivity
    // ----------------------------------------------------------------------------------
    interface ItemClickListener {
        fun onItemClickListener(itemId: Int)
    }


    // Return a food item in list at defined position
    fun getFoodItemAtPosition(position: Int): FoodEntry {
        return adapterFoodEntries!![position]
    }

    // Get all the food items list
    fun getFoodEntries(): List<FoodEntry>? {
        return adapterFoodEntries
    }

    // Refresh recycleView in case of item modifications
    fun swapItems(newFoodList: MutableList<FoodEntry>?) {
        adapterFoodEntries = newFoodList
        notifyDataSetChanged()
    }




}