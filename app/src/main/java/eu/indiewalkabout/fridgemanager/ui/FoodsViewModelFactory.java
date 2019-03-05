package eu.indiewalkabout.fridgemanager.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class FoodsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final String foodlistType;

    public FoodsViewModelFactory(String foodlistType) {
        this.foodlistType = foodlistType;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FoodsViewModel(foodlistType);
    }
}

