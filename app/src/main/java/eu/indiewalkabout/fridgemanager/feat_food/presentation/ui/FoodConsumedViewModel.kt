package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.feat_food.domain.repository.FridgeManagerRepository
import javax.inject.Inject


@HiltViewModel
class FoodConsumedViewModel @Inject constructor(
    val context: Application,
    private val repository: FridgeManagerRepository
): ViewModel() {

}