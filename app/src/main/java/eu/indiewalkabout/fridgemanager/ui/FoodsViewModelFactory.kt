package eu.indiewalkabout.fridgemanager.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class FoodsViewModelFactory(private val foodlistType: String) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FoodsViewModel(foodlistType) as T
    }
}

