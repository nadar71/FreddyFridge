package eu.indiewalkabout.fridgemanager.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import 	android.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;
import eu.indiewalkabout.fridgemanager.util.AppExecutors;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewRowHolder> {

    // Date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Handle item clicks
    final private ItemClickListener foodItemClickListener;

    // Holds food entries data
    private List<FoodEntry> foodEntries;
    private Context         thisContext;
    private String          listType;


    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    // TODO : move onClick management to MainActivity
    // Db ref
    FoodDatabase foodDb;


    /**
     * ----------------------------------------------------------------------------------
     * Constructor :
     * @param context  the current Context
     * @param listener the ItemClickListener
     * ----------------------------------------------------------------------------------
     */
    public FoodListAdapter(Context context, ItemClickListener listener, String listType) {
        thisContext           = context;
        foodItemClickListener = listener;
        this.listType         = listType;

        // DEBUG : dummy list for debug
        /*
        foodEntries = new ArrayList<FoodEntry>();
        foodEntries.add(new FoodEntry("meat",new GregorianCalendar(2018, Calendar.DECEMBER, 12).getTime()) );
        foodEntries.add(new FoodEntry("meat 2",new GregorianCalendar(2018, Calendar.DECEMBER, 2).getTime())  );
        foodEntries.add(new FoodEntry("vegetables",new GregorianCalendar(2018, Calendar.DECEMBER, 20).getTime())  );
        */

        // TODO : move onClick management to MainActivity
        foodDb = FoodDatabase.getsDbInstance(thisContext);

    }

    /**
     * ----------------------------------------------------------------------------------
     * Inflate list's each view/row layout.
     * @return new FoodViewRowHolder, holds task_layout view for each task
     * ----------------------------------------------------------------------------------
     */
    @NonNull
    @Override
    public FoodViewRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the food_row_layout to each view
        View view = LayoutInflater.from(thisContext)
                .inflate(R.layout.food_row_layout, parent, false);

        return new FoodViewRowHolder(view);
    }


    /**
     * ----------------------------------------------------------------------------------
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     * @param holder   ViewHolder to bind Cursor data to
     * @param position dataposition in Cursor
     * ----------------------------------------------------------------------------------
     */
    @Override
    public void onBindViewHolder(@NonNull FoodViewRowHolder holder, int position) {
        // Determine the values of the wanted data
        FoodEntry foodEntry = foodEntries.get(position);
        String foodName     = foodEntry.getName();
        String expiringAt   = dateFormat.format(foodEntry.getExpiringAt());

        //Set values
        holder.foodName_tv.setText(foodName);
        holder.expiringDate_tv.setText(expiringAt);


    }


    @Override
    public int getItemCount() {
        if (foodEntries == null) {
            return 0;
        }
        return foodEntries.size();


    }



    /**
     * ----------------------------------------------------------------------------------
     * Set data for RecycleView as foodEntries list.
     * Used by the activity to init the adapter
     * @param foodEntries
     * ----------------------------------------------------------------------------------
     */
    public void setFoodEntries(List<FoodEntry> foodEntries) {
        this.foodEntries = foodEntries;

        // data changed, refresh the view : notify the related observers
        notifyDataSetChanged();
    }

    // ----------------------------------------------------------------------------------
    // Implemented in calling class, e.g. MainActivity
    // ----------------------------------------------------------------------------------
    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // ----------------------------------------------------------------------------------
    // Inner class for creating ViewHolders
    // ----------------------------------------------------------------------------------
    class FoodViewRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView    foodName_tv;
        TextView    expiringDate_tv;
        ImageButton deleteFoodItem_imgBtn;
        CheckBox    foodConsumed_cb;

        // alert dialog object for user confirmation
        AlertDialog alertDialog;


        // FoodViewRowHolder Constructor
        // @param itemView view inflated in onCreateViewHolder
        public FoodViewRowHolder(View itemView) {
            super(itemView);

            foodName_tv           = itemView.findViewById(R.id.foodName_tv);
            expiringDate_tv       = itemView.findViewById(R.id.expirationDate_tv);
            deleteFoodItem_imgBtn = itemView.findViewById(R.id.delete_ImgBtn);
            foodConsumed_cb       = itemView.findViewById(R.id.consumed_cb);

            // set correct row layout based on food list type
            if (listType.equals(FoodListActivity.FOOD_EXPIRING)) {
                foodConsumed_cb.setVisibility(View.VISIBLE);

            } else if (listType.equals(FoodListActivity.FOOD_SAVED)) {
                foodConsumed_cb.setVisibility(View.INVISIBLE);

            } else if (listType.equals(FoodListActivity.FOOD_DEAD)) {
                foodConsumed_cb.setVisibility(View.VISIBLE);

            }

            // row click listener
            itemView.setOnClickListener(this);

            // delete imgBtn click listener
            deleteFoodItem_imgBtn.setOnClickListener(this);

            // check box click listener
            foodConsumed_cb.setOnClickListener(this);
        }


        // TODO : implements onClick in MainActivity  with implements FoodListAdapter.onClick or something similar

        /**
         * ------------------------------------------------------------------------------------
         * Manage click on recycle view row and the widgets inside that
         * @param view
         * ------------------------------------------------------------------------------------
         */
        @Override
        public void onClick(View view) {
            int elementId = foodEntries.get(getAdapterPosition()).getId();

            //----------------------------
            // delete button pressed
            //----------------------------
            if( view.getId() == deleteFoodItem_imgBtn.getId()){

                // user dialog confirm
                alertDialog = new AlertDialog.Builder(thisContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure you want to DELETE ?")
                        .setMessage("")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteFoodEntry();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do nothing
                            }
                        })
                        .show();




            // ---------------------------------
            // consumed food check box pressed
            // ---------------------------------
            } else if( view.getId() == foodConsumed_cb.getId()){

                // user dialog confirm
                alertDialog = new AlertDialog.Builder(thisContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Food Consumed, are you sure ?")
                        .setMessage("")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                moveToConsumed();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                foodConsumed_cb.setChecked(false);
                                // notify changes to recycleview
                                // swapItems(foodEntries);
                            }
                        })
                        .show();



            //----------------------------
            // row touch
            //----------------------------
            } else   {
                final FoodEntry foodItemRowClicked = getFoodItemAtPosition(this.getAdapterPosition());

                // go to insert activity in update mode
                Intent openInsert = new Intent(thisContext,InsertFoodActivity.class);
                openInsert.putExtra(InsertFoodActivity.ID_TO_BE_UPDATED,foodItemRowClicked.getId());
                thisContext.startActivity(openInsert);

            }

            foodItemClickListener.onItemClickListener(elementId);
        }



        /**
         * ------------------------------------------------------------------------------------
         * Move food entry to consumed list
         * ------------------------------------------------------------------------------------
         */
        private void moveToConsumed() {
            // get item at position
            final FoodEntry foodItemConsumed = getFoodItemAtPosition(this.getAdapterPosition());

            // update check boxed food item done field in db to 1 = consumed
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    foodDb.foodDbDao().updateDoneField(1,foodItemConsumed.getId());
                }
            });

            // remove object from recycle view list
            foodEntries.remove(foodItemConsumed);

            // notify changes to recycleview
            swapItems(foodEntries);

            Toast.makeText(thisContext,
                    "Moved " + foodItemConsumed.getName() +
                    " to Consumed Food list!",
                    Toast.LENGTH_SHORT).show();

            // uncheck the check box because it will be on the next item after refresh
            foodConsumed_cb.setChecked(false);
        }


        /**
         * ------------------------------------------------------------------------------------
         * Delete a food entry from db and recycle view
         * ------------------------------------------------------------------------------------
         */
        private void deleteFoodEntry() {
            // get item at position
            final FoodEntry foodItemToDelete = getFoodItemAtPosition(this.getAdapterPosition());

            // delete food item in db
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    foodDb.foodDbDao().deleteFoodEntry(foodItemToDelete);
                }
            });

            // remove object from recycle view list
            foodEntries.remove(foodItemToDelete);

            // notify changes to recycleview
            swapItems(foodEntries);

            // toast notification
            Toast.makeText(thisContext,
                    " Food  " + foodItemToDelete.getName() + " Deleted.",
                    Toast.LENGTH_SHORT).show();
        }



    } // End Inner class FoodViewHolder --------------------------------------------------------


    /**
     * ----------------------------------------------------------------------------------
     * Return an food item in list at defined position
     * ----------------------------------------------------------------------------------
     */
    public FoodEntry getFoodItemAtPosition(int position){
        return foodEntries.get(position);
    }

    /**
     * ----------------------------------------------------------------------------------
     * Get all the food items list
     * ----------------------------------------------------------------------------------
     */
    public List<FoodEntry> getFoodEntries(){
        return foodEntries;
    }


    /**
     * ----------------------------------------------------------------------------------
     * Used to refresh recycleView in case of item modifications
     * ----------------------------------------------------------------------------------
     */
    public void swapItems(List<FoodEntry> newFoodList){
        this.foodEntries = newFoodList;
        notifyDataSetChanged();
    }





}
