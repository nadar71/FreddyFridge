package eu.indiewalkabout.fridgemanager.presentation.components.viewholder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.util.AppExecutors
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodListActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.food.InsertFoodActivity

class FoodViewRowHolder(
    itemView: View,
    private val foodListAdapter: FoodListAdapter,
    private val thisContext: Context,
    private val foodItemClickListener: FoodListAdapter.ItemClickListener
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

    // Class variables for the task description and priority TextViews
    private var foodName_tv: TextView
    private var expiringDate_tv: TextView
    private var deleteFoodItem_imgBtn: ImageButton
    private var foodConsumed_cb: CheckBox
    private var recyclerview_item: ConstraintLayout


    // alert dialog object for user confirmation
    private var alertDialog: AlertDialog? = null


    // FoodViewRowHolder Constructor
    // @param itemView view inflated in onCreateViewHolder
    init {
        foodName_tv = itemView.findViewById(R.id.foodName_tv)
        expiringDate_tv = itemView.findViewById(R.id.expirationDate_tv)
        deleteFoodItem_imgBtn = itemView.findViewById(R.id.delete_ImgBtn)
        foodConsumed_cb = itemView.findViewById(R.id.consumed_cb)
        recyclerview_item = itemView.findViewById(R.id.recyclerview_item)

        // set correct row layout based on food list type
        if (foodListAdapter.listType == FoodListActivity.FOOD_EXPIRING) {
            foodConsumed_cb.visibility = View.VISIBLE
        } else if (foodListAdapter.listType == FoodListActivity.FOOD_DEAD) {
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
        val elementId = foodListAdapter.adapterFoodEntries!![adapterPosition].id

        // delete button pressed
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


            // consumed food check box pressed
        } else if (view.id == foodConsumed_cb.id) {

            // if we are not in the consumed/done food list :
            if (foodListAdapter.listType != FoodListActivity.FOOD_SAVED) {

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


            // row touch
        } else {
            val foodItemRowClicked = foodListAdapter.getFoodItemAtPosition(this.adapterPosition)

            // go to insert activity in update mode
            val openInsert = Intent(thisContext, InsertFoodActivity::class.java)
            openInsert.putExtra(InsertFoodActivity.ID_TO_BE_UPDATED, foodItemRowClicked.id)
            thisContext.startActivity(openInsert)
        }
        foodItemClickListener.onItemClickListener(elementId)
    }

    // Move food entry to consumed/done food list
    private fun moveToConsumed() {
        // get item at position
        val foodItemConsumed = foodListAdapter.getFoodItemAtPosition(this.adapterPosition)

        // update check boxed food item done field in db to 1 = consumed
        AppExecutors.instance!!.diskIO()
            .execute { foodListAdapter.repository!!.updateDoneField(1, foodItemConsumed.id) }

        // remove object from recycle view list
        foodListAdapter.adapterFoodEntries!!.remove(foodItemConsumed)

        // notify changes to recycleview
        foodListAdapter.swapItems(foodListAdapter.adapterFoodEntries)
        Toast.makeText(
            thisContext,
            "Moved " + foodItemConsumed.name +
                    " to Consumed Food list!",
            Toast.LENGTH_SHORT
        ).show()

        // uncheck the check box because it will be on the next item after refresh
        foodConsumed_cb.isChecked = false
    }

    // Move food entry to NOT consumed/done food list
    private fun moveToNOTConsumed() {
        // get item at position
        val foodItemConsumed = foodListAdapter.getFoodItemAtPosition(this.adapterPosition)

        // update check boxed food item done field in db to 0 = not consumed:
        // it's the meaning assumed in the consumed/dine food list when checked
        AppExecutors.instance!!.diskIO()
            .execute { foodListAdapter.repository!!.updateDoneField(0, foodItemConsumed.id) }

        // remove object from recycle view list
        foodListAdapter.adapterFoodEntries!!.remove(foodItemConsumed)

        // notify changes to recycleview
        foodListAdapter.swapItems(foodListAdapter.adapterFoodEntries)
        Toast.makeText(
            thisContext,
            "Removed " + foodItemConsumed.name +
                    " to Consumed Food list!",
            Toast.LENGTH_SHORT
        ).show()

        // uncheck the check box because it will be on the next item after refresh
        foodConsumed_cb.isChecked = false
    }

    // Delete a food entry from db and recycle view
    private fun deleteFoodEntry() {
        // get item at position
        val foodItemToDelete = foodListAdapter.getFoodItemAtPosition(this.adapterPosition)

        // delete food item in db
        AppExecutors.instance!!.diskIO()
            .execute { foodListAdapter.repository!!.deleteFoodEntry(foodItemToDelete) }

        // remove object from recycle view list
        foodListAdapter.adapterFoodEntries!!.remove(foodItemToDelete)

        // notify changes to recycleview
        foodListAdapter.swapItems(foodListAdapter.adapterFoodEntries)

        // toast notification
        Toast.makeText(
            thisContext,
            " Food  " + foodItemToDelete.name + " Deleted.",
            Toast.LENGTH_SHORT
        ).show()
    }


}