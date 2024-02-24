package eu.indiewalkabout.fridgemanager.presentation.ui.intromain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewmodel@Inject constructor(
                            val context: Application
): AndroidViewModel(context) {

}