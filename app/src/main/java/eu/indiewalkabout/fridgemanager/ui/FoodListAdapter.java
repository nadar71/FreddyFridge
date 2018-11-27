package eu.indiewalkabout.fridgemanager.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


import eu.indiewalkabout.fridgemanager.R;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

import java.util.Date;
import java.util.GregorianCalendar;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewRowHolder> {

    // Date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Handle item clicks
    final private ItemClickListener foodItemClickListener;

    // Holds food entries data
    private List<FoodEntry> foodEntries ;
    private Context         thisContext;

    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


    /**
     * ----------------------------------------------------------------------------------
     * Constructor :
     * @param context  the current Context
     * @param listener the ItemClickListener
     * ----------------------------------------------------------------------------------
     */
    public FoodListAdapter(Context context, ItemClickListener listener) {
        thisContext           = context;
        foodItemClickListener = listener;

        // DEBUG : dummy list for debug
        /*
        foodEntries = new ArrayList<FoodEntry>();
        foodEntries.add(new FoodEntry("meat",new GregorianCalendar(2018, Calendar.DECEMBER, 12).getTime()) );
        foodEntries.add(new FoodEntry("meat 2",new GregorianCalendar(2018, Calendar.DECEMBER, 2).getTime())  );
        foodEntries.add(new FoodEntry("vegetables",new GregorianCalendar(2018, Calendar.DECEMBER, 20).getTime())  );
        */
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

        // ----------------------------------------------------
        // TODO : with ViewModel/LiveData
        // data changed, refresh the view : notify the related observers
        // notifyDataSetChanged();
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
        TextView foodName_tv;
        TextView expiringDate_tv;


        // FoodViewRowHolder Constructor
        // @param itemView view inflated in onCreateViewHolder
        public FoodViewRowHolder(View itemView) {
            super(itemView);

            foodName_tv     = itemView.findViewById(R.id.foodName_tv);
            expiringDate_tv = itemView.findViewById(R.id.expirationDate_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = foodEntries.get(getAdapterPosition()).getId();
            foodItemClickListener.onItemClickListener(elementId);
        }
    }


    /**
     * ----------------------------------------------------------------------------------
     * Return an food item in list at defined position
     * ----------------------------------------------------------------------------------
     */
    public FoodEntry getTaskAtPosition(int position){
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

}
