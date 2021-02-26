package eu.indiewalkabout.fridgemanager.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import eu.indiewalkabout.fridgemanager.util.AppExecutors.Companion.instance
import eu.indiewalkabout.fridgemanager.repository.FridgeManagerRepository
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.App
import eu.indiewalkabout.fridgemanager.App.Companion.getsContext
import eu.indiewalkabout.fridgemanager.data.db.DateConverter
import eu.indiewalkabout.fridgemanager.data.model.FoodEntry
import eu.indiewalkabout.fridgemanager.ui.FoodListAdapter.FoodViewRowHolder
import eu.indiewalkabout.fridgemanager.util.DateUtility
import eu.indiewalkabout.fridgemanager.util.DateUtility.DAY_IN_MILLIS
import java.text.SimpleDateFormat
import java.util.*

class FoodListAdapter(private val thisContext: Context, // Handle item clicks
                      private val foodItemClickListener: ItemClickListener,
                      private val listType: String)
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
        return FoodViewRowHolder(view, listType, thisContext, foodItemClickListener)
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

    // ----------------------------------------------------------------------------------
    // Inner class for creating ViewHolders
    // ----------------------------------------------------------------------------------
    inner class FoodViewRowHolder(itemView: View,
                                  val listType: String,
                                  val thisContext: Context,
                                  val foodItemClickListener: ItemClickListener)
        : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

        // Class variables for the task description and priority TextViews
        var foodName_tv: TextView
        var expiringDate_tv: TextView
        var deleteFoodItem_imgBtn: ImageButton
        var foodConsumed_cb: CheckBox
        var recyclerview_item: ConstraintLayout


        // alert dialog object for user confirmation
        var alertDialog: AlertDialog? = null


        // FoodViewRowHolder Constructor
        // @param itemView view inflated in onCreateViewHolder
        init {
            foodName_tv = itemView.findViewById(R.id.foodName_tv)
            expiringDate_tv = itemView.findViewById(R.id.expirationDate_tv)
            deleteFoodItem_imgBtn = itemView.findViewById(R.id.delete_ImgBtn)
            foodConsumed_cb = itemView.findViewById(R.id.consumed_cb)
            recyclerview_item = itemView.findViewById(R.id.recyclerview_item)

            // set correct row layout based on food list type
            if (listType == FoodListActivity.FOOD_EXPIRING) {
                foodConsumed_cb.visibility = View.VISIBLE
            } else if (listType == FoodListActivity.FOOD_DEAD) {
                foodConsumed_cb.visibility = View.VISIBLE
            }
            /*
            else if (listType.equals(FoodListActivity.FOOD_SAVED)) {
                foodConsumed_cb.setVisibility(View.INVISIBLE);

            }
             */


            // row click listener
            itemView.setOnClickListener(this)

            // delete imgBtn click listener
            deleteFoodItem_imgBtn.setOnClickListener(this)

            // check box click listener
            foodConsumed_cb.setOnClickListener(this)
        }

        // TODO : implements onClick in MainActivity  with implementing FoodListAdapter.onClick or something similar
        // Manage click on recycle view row and the widgets inside that
        override fun onClick(view: View) {
            val elementId = adapterFoodEntries!![adapterPosition].id

            //----------------------------
            // delete button pressed
            //----------------------------
            if (view.id == deleteFoodItem_imgBtn.id) {

                // user dialog confirm
                val builder = AlertDialog.Builder(thisContext)
                val dialogLayout = LayoutInflater.from(thisContext)
                        .inflate(R.layout.custom_confirm_dialog, null)
                val alert = dialogLayout.findViewById<TextView>(R.id.confirm_dialog_tv)
                alert.setText(R.string.confirm_if_deleting_text)
                val yesBtn = dialogLayout.findViewById<Button>(R.id.confirm_dialog_yes_btn)
                val noBtn = dialogLayout.findViewById<Button>(R.id.confirm_dialog_no_btn)
                yesBtn.setOnClickListener {
                    alertDialog!!.dismiss()
                    deleteFoodEntry()
                }
                noBtn.setOnClickListener { alertDialog!!.dismiss() }
                builder.setView(dialogLayout)
                alertDialog = builder.show()


                // ---------------------------------
                // consumed food check box pressed
                // ---------------------------------
            } else if (view.id == foodConsumed_cb.id) {

                // if we are not in the consumed/done food list :
                if (listType != FoodListActivity.FOOD_SAVED) {


                    // user dialog confirm
                    val builder = AlertDialog.Builder(thisContext)
                    val dialogLayout = LayoutInflater.from(thisContext)
                            .inflate(R.layout.custom_confirm_dialog, null)
                    val alert = dialogLayout.findViewById<TextView>(R.id.confirm_dialog_tv)
                    alert.setText(R.string.confirm_if_consumed_text)
                    val yesBtn = dialogLayout.findViewById<Button>(R.id.confirm_dialog_yes_btn)
                    val noBtn = dialogLayout.findViewById<Button>(R.id.confirm_dialog_no_btn)
                    yesBtn.setOnClickListener {
                        alertDialog!!.dismiss()
                        moveToConsumed()
                    }
                    noBtn.setOnClickListener {
                        alertDialog!!.dismiss()
                        foodConsumed_cb.isChecked = false
                    }
                    builder.setView(dialogLayout)
                    alertDialog = builder.show()

                } else { // return food to the not consumed/done ones list
                    // user dialog confirm


                    // user dialog confirm
                    val builder = AlertDialog.Builder(thisContext)
                    val dialogLayout = LayoutInflater.from(thisContext)
                            .inflate(R.layout.custom_confirm_dialog, null)
                    val alert = dialogLayout.findViewById<TextView>(R.id.confirm_dialog_tv)
                    alert.setText(R.string.confirm_if_not_consumed_text)
                    val yesBtn = dialogLayout.findViewById<Button>(R.id.confirm_dialog_yes_btn)
                    val noBtn = dialogLayout.findViewById<Button>(R.id.confirm_dialog_no_btn)
                    yesBtn.setOnClickListener {
                        alertDialog!!.dismiss()
                        moveToNOTConsumed()
                    }
                    noBtn.setOnClickListener {
                        alertDialog!!.dismiss()
                        foodConsumed_cb.isChecked = false
                    }
                    builder.setView(dialogLayout)
                    alertDialog = builder.show()
                }


                //----------------------------
                // row touch
                //----------------------------
            } else {
                val foodItemRowClicked = getFoodItemAtPosition(this.adapterPosition)

                // go to insert activity in update mode
                val openInsert = Intent(thisContext, InsertFoodActivity::class.java)
                openInsert.putExtra(InsertFoodActivity.ID_TO_BE_UPDATED, foodItemRowClicked.id)
                thisContext.startActivity(openInsert)
            }
            foodItemClickListener.onItemClickListener(elementId)
        }

        /**
         * ------------------------------------------------------------------------------------
         * Move food entry to consumed/done food list
         * ------------------------------------------------------------------------------------
         */
        private fun moveToConsumed() {
            // get item at position
            val foodItemConsumed = getFoodItemAtPosition(this.adapterPosition)

            // update check boxed food item done field in db to 1 = consumed
            instance!!.diskIO().execute { repository!!.updateDoneField(1, foodItemConsumed.id) }

            // remove object from recycle view list
            adapterFoodEntries!!.remove(foodItemConsumed)

            // notify changes to recycleview
            swapItems(adapterFoodEntries)
            Toast.makeText(thisContext,
                    "Moved " + foodItemConsumed.name +
                            " to Consumed Food list!",
                    Toast.LENGTH_SHORT).show()

            // uncheck the check box because it will be on the next item after refresh
            foodConsumed_cb.isChecked = false
        }

        /**
         * ------------------------------------------------------------------------------------
         * Move food entry to NOT consumed/done food list
         * ------------------------------------------------------------------------------------
         */
        private fun moveToNOTConsumed() {
            // get item at position
            val foodItemConsumed = getFoodItemAtPosition(this.adapterPosition)

            // update check boxed food item done field in db to 0 = not consumed:
            // it's the meaning assumed in the consumed/dine food list when checked
            instance!!.diskIO().execute { repository!!.updateDoneField(0, foodItemConsumed.id) }

            // remove object from recycle view list
            adapterFoodEntries!!.remove(foodItemConsumed)

            // notify changes to recycleview
            swapItems(adapterFoodEntries)
            Toast.makeText(thisContext,
                    "Removed " + foodItemConsumed.name +
                            " to Consumed Food list!",
                    Toast.LENGTH_SHORT).show()

            // uncheck the check box because it will be on the next item after refresh
            foodConsumed_cb.isChecked = false
        }

        /**
         * ------------------------------------------------------------------------------------
         * Delete a food entry from db and recycle view
         * ------------------------------------------------------------------------------------
         */
        private fun deleteFoodEntry() {
            // get item at position
            val foodItemToDelete = getFoodItemAtPosition(this.adapterPosition)

            // delete food item in db
            instance!!.diskIO().execute { repository!!.deleteFoodEntry(foodItemToDelete) }

            // remove object from recycle view list
            adapterFoodEntries!!.remove(foodItemToDelete)

            // notify changes to recycleview
            swapItems(adapterFoodEntries)

            // toast notification
            Toast.makeText(thisContext,
                    " Food  " + foodItemToDelete.name + " Deleted.",
                    Toast.LENGTH_SHORT).show()
        }


    } // End Inner class FoodViewHolder --------------------------------------------------------



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