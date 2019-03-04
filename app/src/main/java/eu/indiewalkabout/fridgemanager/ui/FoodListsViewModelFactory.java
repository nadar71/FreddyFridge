package eu.indiewalkabout.fridgemanager.ui;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import eu.indiewalkabout.fridgemanager.data.FoodDatabase;

public class FoodListsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FoodDatabase foodDb;
    private final String foodlistType;
    private final Application application;

    public FoodListsViewModelFactory(FoodDatabase foodDb, String foodlistType, Application application) {
        this.foodDb       = foodDb;
        this.foodlistType = foodlistType;
        this.application  = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new FoodListsViewModel(foodDb, foodlistType, application);
    }
}

