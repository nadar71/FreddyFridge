package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

public class FoodInsertViewModel extends ViewModel {

    // tag for logging
    private static final String TAG = FoodListsViewModel.class.getSimpleName();

    // Livedata var on foodEntry List to populate through ViewModel
    private LiveData<FoodEntry> foodEntry;


    /**
     * Constructor for {@link FoodListsViewModelFactory}, set attribute LiveData<FoodEntry>
     * based on food id
     * @param foodDb
     * @param foodId
     */
    public FoodInsertViewModel(FoodDatabase foodDb, int foodId) {
        foodEntry = foodDb.foodDbDao().loadFoodById(foodId);
    }


    /**
     * Getter for LiveData<FoodEntry>
     * @return
     */
    public LiveData<FoodEntry> getFoodEntry() {
        return foodEntry;
    }
}
