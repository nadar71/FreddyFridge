package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.Manifest
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Globals.IS_IN_PREVIEW
import eu.indiewalkabout.fridgemanager.core.presentation.components.RoundedCornerButton
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.ComposeCalendar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreyVeryTransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntryUI
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.NumberPickerWithTitle
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.SimpleTextField
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUpdateUiState
import eu.indiewalkabout.fridgemanager.feat_food.presentation.util.VoiceRecognitionManager
import java.time.LocalDate
import java.util.TimeZone


@Composable
fun UpdateFoodOverlay(
    foodViewModel: FoodViewModel = hiltViewModel(),
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
    foodEntryUI: FoodEntryUI,
    onSave: () -> Unit,
    cancelable: Boolean = true,
    onLeftButtonAction: (() -> Unit)? = null
) {

    var TAG = "UpdateFoodOverlay"
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var rmsDb by remember { mutableFloatStateOf(0f) }

    var showCalendarDialog by remember { mutableStateOf(false) }
    var showQuantityWheelPicker by remember { mutableStateOf(false) }

    var localeDateText by remember { mutableStateOf<LocalDate?>(foodEntryUI.expiringAtLocalDate) }
    var localeDateShownText by remember { mutableStateOf(foodEntryUI.expiringAtUI ?: "") }
    var descriptionText by remember { mutableStateOf(foodEntryUI.name) }
    var quantityNumText by remember { mutableStateOf("1") } // always 1

    var foodUpdated by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }

    var isBtnEnabled = localeDateShownText.isNotEmpty() && descriptionText?.isNotEmpty() == true


    // ------------------------------------- LOGIC -------------------------------------------------
    /*val updateUiState by foodViewModel.updateUiState.collectAsState()

    LaunchedEffect(updateUiState) {
        when (updateUiState) {
            is FoodUpdateUiState.Success -> {
                showProgressBar = false
                // foodUpdated = true
                Toast.makeText(context,
                context.getString(R.string.update_food_successfully),
                Toast.LENGTH_SHORT).show()
            foodUpdated = false
                // After Success/Error, reset updateUiState to Idle doesn't re-trigger dialog re-opening
                foodViewModel.resetUpdateUiStateToIdle()
                onSave()
            }
            is FoodUpdateUiState.Error -> {
                showProgressBar = false
                Log.e(TAG, "Error updating food in db")
                foodViewModel.resetUpdateUiStateToIdle()
            }
            is FoodUpdateUiState.Loading -> {
                showProgressBar = true
            }
            is FoodUpdateUiState.Idle -> {
                showProgressBar = false
            }
        }
    }*/

    // show toast in case of food updated successfully
    /*LaunchedEffect(foodUpdated) {
        if (foodUpdated == true) {
            Toast.makeText(context,
                context.getString(R.string.update_food_successfully),
                Toast.LENGTH_SHORT).show()
            foodUpdated = false
        }
    }*/

    // setup voice manager
    val voiceManager = remember {
        if (!IS_IN_PREVIEW) {
            VoiceRecognitionManager(
                context = context,
                onResult = {
                    resultText = it
                    descriptionText = resultText
                    isListening = false
                },
                onErrorCallback = {
                    resultText = it
                    isListening = false
                },
                onRmsCallback = {
                    rmsDb = it
                }
            )
        } else {
            null // disable in preview
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            voiceManager?.destroy()
        }
    }


    // ------------------------------ PERMISSIONS --------------------------------------------------
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager?.startListening()
            isListening = true
        } else {
            Toast.makeText(context,
                context.getString(R.string.permission_denied_title),
                Toast.LENGTH_SHORT).show()
        }
    }

    // ------------------------------------- DIALOG ------------------------------------------------
    // Calendar widget
    if (showCalendarDialog) { // TODO: define different date format based on localization
        ComposeCalendar(
            startDate = LocalDate.now(),
            maxDate = LocalDate.MAX,
            onDone = { it: LocalDate -> // ISO-8601 default format: YYYY-MM-DD
                Log.d(TAG, "InsertFoodBottomSheetContent: date selected : $it")
                localeDateText = it     // for db
                localeDateShownText =
                    it.format(getLocalDateFormat())  // local default date format, i.e. mm/dd/yy
                Log.d(
                    TAG,
                    "InsertFoodBottomSheetContent: date formatted for local default : $localeDateShownText"
                )
                showCalendarDialog = false
            },
            onDismiss = {
                showCalendarDialog = false
            }
        )
    }

    // Quantity wheel picker
    if (showQuantityWheelPicker) {
        NumberPickerWithTitle(
            title = stringResource(R.string.insert_quantity_label),
            max = 50,
            onItemSelected = {
                quantityNumText = it
                Log.d(TAG, "InsertFoodBottomSheetContent: quantity selected : $it")
                showQuantityWheelPicker = false
            },
            onDismiss = {
                showQuantityWheelPicker = false
            }
        )
    }


    // ------------------------------------- UI ----------------------------------------------------

    Dialog(
        onDismissRequest = { if (cancelable) onLeftButtonAction?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = cancelable,
            dismissOnClickOutside = cancelable
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                // .border(1.dp, secondaryColor, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)) // Clip the Box to the rounded shape
                .background(
                    primaryColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                )

        ) {
            // background Image
            Image(
                painter = painterResource(id = R.drawable.food_background_half), // Your image resource
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        primaryColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
            ) {


                TopBar(
                    title = stringResource(R.string.insert_food_title),
                    paddingTop = 16.dp,
                    paddingBottom = 16.dp,
                    backgroundColor = Color.Transparent,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // expiring date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(R.string.insert_expiring_date_label),
                        style = text_16(colorText, true),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SimpleTextField(
                        enabled = false,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        hintText = stringResource(R.string.insert_expiring_date_hint),
                        hintTextStyle = text_14(colorHintText, false),
                        inputTextStyle = text_14(colorText, false),
                        value = localeDateShownText,
                        onValueChange = { localeDateShownText = it },
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_calendar_month),
                        contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 16.dp)
                            .clickable {
                                showCalendarDialog = true
                            }
                    )
                }

                // description
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(R.string.insert_food_description_hint),
                        style = text_16(colorText, true),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {


                    SimpleTextField(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        hintText = stringResource(R.string.insert_food_description_hint),
                        hintTextStyle = text_14(colorHintText, false),
                        inputTextStyle = text_14(colorText, false),
                        value = descriptionText.toString(),
                        onValueChange = { descriptionText = it },
                    )

                    val micColor by animateColorAsState(
                        targetValue = if (isListening) lightGreen else secondaryColor,
                        animationSpec = tween(durationMillis = 500)
                    )

                    val animatedMicScale by animateFloatAsState(
                        targetValue = if (isListening) 1f + (rmsDb / 10f).coerceIn(0f, 0.5f) else 1f,
                        animationSpec = tween(durationMillis = 500)
                    )


                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 8.dp)
                            .size(40.dp)
                            .graphicsLayer {
                                scaleX = animatedMicScale
                                scaleY = animatedMicScale
                            }
                            .background(
                                color = micColor.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .clickable {
                                if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                                    Toast.makeText(
                                        context,
                                        "Speech Recognition not available",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@clickable
                                }
                                if (!isListening) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    voiceManager?.stopListening()
                                    isListening = false

                                }
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic_white),
                            contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                            tint = micColor,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                }

                // quantity
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 16.dp, 0.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = stringResource(R.string.insert_quantity_label),
                        style = text_16(colorText, true),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SimpleTextField(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        hintText = stringResource(R.string.insert_quantity_label),
                        hintTextStyle = text_14(colorHintText, false),
                        inputTextStyle = text_14(colorText, false),
                        isNumeric = true,
                        isDecimal = false,
                        value = quantityNumText,
                        onValueChange = { quantityNumText = it },
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_quantity_add),
                        contentDescription = stringResource(R.string.insert_quantity_icon_description),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 16.dp)
                            .clickable {
                                showQuantityWheelPicker = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                RoundedCornerButton(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        Log.d(TAG, "Save button clicked. isBtnEnabled: $isBtnEnabled")
                        /*if (!isBtnEnabled) return@RoundedCornerButton
                        else {
                            // NB : response detected in parent ( FoodCard )
                            foodViewModel.updateFoodEntry(
                                FoodEntry(
                                    id = foodEntryUI.id,
                                    name = descriptionText,
                                    expiringAt = localeDateText,
                                    consumedAt = foodEntryUI.consumedAtLocalDate,
                                    timezoneId = foodEntryUI.timezoneId,
                                    order_number = quantityNumText.toInt()
                                )
                            )
                        }*/
                        if (!isBtnEnabled) return@RoundedCornerButton
                        else {
                            var quantity = quantityNumText.toInt()
                            if ( quantity <= 1 ) {
                                // NB : response detected in parent ( FoodCard )
                                foodViewModel.updateFoodEntry(
                                    FoodEntry(
                                        name = descriptionText,
                                        expiringAt = localeDateText,
                                        consumedAt = foodEntryUI.consumedAtLocalDate,
                                        timezoneId = TimeZone.getDefault().id,
                                        order_number = 0
                                    )
                                )
                            } else { // a quantity greater then 1 is selected
                                var count = 1
                                // first is updated
                                FoodEntry(
                                    name = descriptionText,
                                    expiringAt = localeDateText,
                                    consumedAt = foodEntryUI.consumedAtLocalDate,
                                    timezoneId = TimeZone.getDefault().id,
                                    order_number = foodEntryUI.order_number
                                )
                                // others exceeding 1 are inserted as new
                                while (quantity > 1) {
                                    insertFoodViewModel.insertFood(
                                        FoodEntry(
                                            name = descriptionText,
                                            expiringAt = localeDateText,
                                            consumedAt = foodEntryUI.consumedAtLocalDate,
                                            timezoneId = TimeZone.getDefault().id,
                                            order_number = count
                                        )
                                    )
                                    count++
                                    quantity--
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(15.dp),
                    elevation = 0,
                    borderStroke = BorderStroke(1.dp, secondaryColor),
                    backgroundColor = if (isBtnEnabled) backgroundLightGrey else lightGreyVeryTransparent,
                    text = stringResource(R.string.generic_save_label),
                    textPadding = 0,
                    style = text_14(Color.White, true)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UpdateFoodOverlayPreview() {
    FreddyFridgeTheme {
        UpdateFoodOverlay(
            foodEntryUI = FoodEntryUI(
                id = 1,
                name = "Expired Food",
                expiringAtLocalDate = LocalDate.now().minusDays(1),
                expiringAtUI = LocalDate.now().minusDays(1).format(getLocalDateFormat()) ?: "",
                order_number = 1
            ),
            onSave = {},
            onLeftButtonAction = {}
        )
    }
}