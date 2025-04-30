package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepositoryImpl
import eu.indiewalkabout.fridgemanager.domain.repository.FridgeManagerRepository
import javax.inject.Inject


@HiltViewModel
class FoodConsumedViewModel @Inject constructor(
    val context: Application,
    private val repository: FridgeManagerRepository
): ViewModel() {

}