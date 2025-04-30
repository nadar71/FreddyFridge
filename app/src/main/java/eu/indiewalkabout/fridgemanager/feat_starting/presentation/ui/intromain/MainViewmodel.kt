package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.intromain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewmodel @Inject constructor(
                            val context: Application
): AndroidViewModel(context) {

}