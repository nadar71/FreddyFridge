package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.indiewalkabout.fridgemanager.data.repository.FridgeManagerRepositoryImpl
import javax.inject.Inject


@HiltViewModel
class FoodExpiringViewModel @Inject constructor(
    val context: Application,
    private val repository: FridgeManagerRepositoryImpl
): ViewModel() {



}