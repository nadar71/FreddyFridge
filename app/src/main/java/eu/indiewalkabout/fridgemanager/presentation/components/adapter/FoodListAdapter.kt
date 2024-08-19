package eu.indiewalkabout.fridgemanager.presentation.components.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import eu.indiewalkabout.fridgemanager.FreddyFridgeApplication.Companion.getsContext
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.util.DateUtility
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.DAY_IN_MILLIS
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.data.local.db.DateConverter
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.presentation.components.viewholder.FoodViewRowHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodListAdapter(
    private val thisContext: Context, // Handle item clicks
    private val foodItemClickListener: ItemClickListener,
    val listType: String
) : androidx.recyclerview.widget.RecyclerView.Adapter<FoodViewRowHolder>() {


    private val DATE_FORMAT = "dd/MM/yyy"

    // Holds food entries data
    internal var adapterFoodEntries: MutableList<FoodEntry>? = null

    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    // val repository: FridgeManagerRepository? = (getsContext() as FreddyFridgeApplication?)!!.repository


    // Inflate row layout.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewRowHolder {
        // Inflate the food_row_layout to each view
        val view = LayoutInflater.from(thisContext)
            .inflate(R.layout.food_row_layout, parent, false)
        return FoodViewRowHolder(
            view, this,
            thisContext, foodItemClickListener
        )
    }


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
    private fun setColorByExpirationDayDifference(
        expiringAtDate: Date,
        foodName: String?,
        holder: FoodViewRowHolder
    ) {
        val dataNormalizedAtMidnight = DateUtility
            .getLocalMidnightFromNormalizedUtcDate(
                DateUtility
                    .normalizedUtcMsForToday
            )
        val daysBefore = DateConverter.fromDate(expiringAtDate)
            ?.minus(dataNormalizedAtMidnight)
            ?.div(DAY_IN_MILLIS)
            ?.let { Math.floor(it.toDouble()) }

        Log.i(TAG, "onBindViewHolder: for $foodName daysBefore : $daysBefore")

        if (daysBefore != null) {
            when (daysBefore.toInt()) {
                0 -> holder.recyclerview_item.background =
                    getDrawable(getsContext()!!, R.drawable.rounded_rect_red_item)

                1 -> holder.recyclerview_item.background =
                    getDrawable(getsContext()!!, R.drawable.rounded_rect_orange_item)

                2 -> holder.recyclerview_item.background =
                    getDrawable(getsContext()!!, R.drawable.rounded_rect_yellow_item)

                3 -> holder.recyclerview_item.background =
                    getDrawable(getsContext()!!, R.drawable.rounded_rect_green_item)

                else -> holder.recyclerview_item.background =
                    getDrawable(getsContext()!!, R.drawable.rounded_rect_green_item)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (adapterFoodEntries == null) {
            0
        } else adapterFoodEntries!!.size
    }

    // Set data for RecycleView as foodEntries list.
    // Used by the activity to init the adapter
    // @param foodEntries
    fun setFoodEntries(foodEntries: MutableList<FoodEntry>?) {
        this.adapterFoodEntries = foodEntries
        // data changed, refresh the view : notify the related observers
        notifyDataSetChanged()
    }

    // Implemented in calling class, e.g. MainActivity
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