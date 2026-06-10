package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.Manifest
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Globals.IS_IN_PREVIEW
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.ComposeCalendar
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.NumberPickerWithTitle
import eu.indiewalkabout.fridgemanager.feat_food.presentation.util.VoiceRecognitionManager
import java.time.LocalDate

@Composable
fun FoodEditorExperience(
    initialState: FoodEditorUiState,
    titleResId: Int,
    onSubmit: (FoodEditorUiState) -> Unit,
) {
    val context = LocalContext.current
    var isListening by remember(initialState) { mutableStateOf(false) }
    var rmsDb by remember(initialState) { mutableFloatStateOf(0f) }
    var showCalendarDialog by remember(initialState) { mutableStateOf(false) }
    var showQuantityWheelPicker by remember(initialState) { mutableStateOf(false) }
    var editorState by remember(initialState) { mutableStateOf(initialState) }

    val voiceManager = remember {
        if (!IS_IN_PREVIEW) {
            VoiceRecognitionManager(
                context = context,
                onResult = {
                    editorState = editorState.copy(description = it)
                    isListening = false
                },
                onErrorCallback = {
                    isListening = false
                },
                onRmsCallback = { rmsDb = it }
            )
        } else {
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager?.destroy()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager?.startListening()
            isListening = true
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.permission_denied_title),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    if (showCalendarDialog) {
        ComposeCalendar(
            startDate = LocalDate.now(),
            maxDate = LocalDate.MAX,
            onDone = {
                editorState = editorState.copy(
                    expiringAt = it,
                    expiringAtText = it.format(getLocalDateFormat())
                )
                showCalendarDialog = false
            },
            onDismiss = {
                showCalendarDialog = false
            }
        )
    }

    if (showQuantityWheelPicker) {
        NumberPickerWithTitle(
            title = context.getString(R.string.insert_quantity_label),
            max = 50,
            onItemSelected = {
                editorState = editorState.copy(quantityText = it)
                showQuantityWheelPicker = false
            },
            onDismiss = {
                showQuantityWheelPicker = false
            }
        )
    }

    FoodEditorFormContent(
        titleResId = titleResId,
        state = editorState,
        isListening = isListening,
        rmsDb = rmsDb,
        onDateClick = {
            showCalendarDialog = true
        },
        onDescriptionChange = {
            editorState = editorState.copy(description = it)
        },
        onMicClick = {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                Toast.makeText(context, "Speech Recognition not available", Toast.LENGTH_SHORT)
                    .show()
            } else if (!isListening) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                voiceManager?.stopListening()
                isListening = false
            }
        },
        onQuantityChange = {
            editorState = editorState.copy(quantityText = it)
        },
        onQuantityPickerClick = {
            showQuantityWheelPicker = true
        },
        onSaveClick = {
            onSubmit(editorState)
        }
    )
}
