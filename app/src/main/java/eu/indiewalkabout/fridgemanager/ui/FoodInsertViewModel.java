package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import eu.indiewalkabout.fridgemanager.ApplicationProvider;
import eu.indiewalkabout.fridgemanager.FridgeManagerRepository;
import eu.indiewalkabout.fridgemanager.data.FoodDatabase;
import eu.indiewalkabout.fridgemanager.data.FoodEntry;

public class FoodInsertViewModel extends ViewModel {

    // tag for logging
    private static final String TAG = FoodListsViewModel.class.getSimpleName();

    // Livedata var on foodEntry List to populate through ViewModel
    private LiveData<FoodEntry> foodEntry;

    // repository ref
    private FridgeManagerRepository repository;


    /**
     * Constructor for {@link FoodListsViewModelFactory}, set attribute LiveData<FoodEntry>
     * based on food id
     * @param foodId
     */
    public FoodInsertViewModel( int foodId) {
        // get repository instance

        repository = ((ApplicationProvider) ApplicationProvider.getsContext()).getRepository();

        foodEntry = repository.loadFoodById(foodId);
    }


    /**
     * Getter for LiveData<FoodEntry>
     * @return
     */
    public LiveData<FoodEntry> getFoodEntry() {
        return foodEntry;
    }
}
