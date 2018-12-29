package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;

public class FoodListsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FoodDatabase foodDb;
    private final String foodlistType;

    public FoodListsViewModelFactory(FoodDatabase foodDb, String foodlistType) {
        this.foodDb = foodDb;
        this.foodlistType = foodlistType;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FoodListsViewModel(foodDb,foodlistType);
    }
}

