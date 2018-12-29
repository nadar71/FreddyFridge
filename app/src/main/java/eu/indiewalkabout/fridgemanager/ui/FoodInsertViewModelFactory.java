package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;

public class FoodInsertViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final FoodDatabase foodDb;
    private final int          foodId;

    public FoodInsertViewModelFactory(FoodDatabase foodDb, int foodId) {
        this.foodDb = foodDb;
        this.foodId = foodId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FoodInsertViewModel(foodDb,foodId);
    }
}
