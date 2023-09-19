package eu.indiewalkabout.fridgemanager.presentation.ui.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FoodsViewModelFactory(private val foodlistType: String) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FoodsViewModel(foodlistType) as T
    }

}