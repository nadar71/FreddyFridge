package eu.indiewalkabout.fridgemanager.ui;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

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

