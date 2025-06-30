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
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.SimpleTextField
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Globals.IS_IN_PREVIEW
import eu.indiewalkabout.fridgemanager.core.presentation.components.RoundedCornerButton
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.ComposeCalendar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreyVeryTransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.NumberPickerWithTitle
import eu.indiewalkabout.fridgemanager.feat_food.presentation.util.VoiceRecognitionManager
import java.time.LocalDate
import eu.indiewalkabout.fridgemanager.feat_food.presentation.state.FoodUiState
import java.util.TimeZone


@Composable
fun InsertFoodBottomSheetContent(
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
    onSave: () -> Unit,
    descriptionText: String,
    onDescriptionChange: (String) -> Unit
) {

    var TAG = "InsertFoodBottomSheetContent"
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var rmsDb by remember { mutableFloatStateOf(0f) }

    var showCalendarDialog by remember { mutableStateOf(false) }
    var showQuantityWheelPicker by remember { mutableStateOf(false) }

    var localeDateText by remember { mutableStateOf<LocalDate?>(null) }
    var localeDateShownText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var quantityNumText by remember { mutableStateOf("1") }

    var isBtnEnabled = localeDateShownText.isNotEmpty() && descriptionText.isNotEmpty()

    var foodInserted by remember { mutableStateOf(false) }
    var showProgressBar by remember { mutableStateOf(false) }


    // ------------------------------------- LOGIC -------------------------------------------------
    val unitUiState by insertFoodViewModel.unitUiState.collectAsState()

    LaunchedEffect(unitUiState) {
        when (unitUiState) {
            is FoodUiState.Success -> {
                showProgressBar = false
                foodInserted = true
                Toast.makeText(
                    context,
                    context.getString(R.string.insert_food_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                onSave()
            }

            is FoodUiState.Error -> {
                showProgressBar = false
                Log.e(TAG, "Error inserting food in db")
            }

            is FoodUiState.Loading -> {
                showProgressBar = true
            }

            is FoodUiState.Idle -> {
                showProgressBar = false
            }
        }
    }

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
            Toast.makeText(
                context,
                context.getString(R.string.permission_denied_title),
                Toast.LENGTH_SHORT
            ).show()
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
                    value = descriptionText,
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
                    if (!isBtnEnabled) return@RoundedCornerButton
                    else {
                        insertFoodViewModel.insertFood(
                            FoodEntry(
                                name = descriptionText,
                                expiringAt = localeDateText,
                                quantity = quantityNumText.toInt(),
                                timezoneId = TimeZone.getDefault().id,
                            )
                        )
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


@Preview(showBackground = true)
@Composable
fun InsertFoodBottomSheetContentPreview() {
    IS_IN_PREVIEW = true
    InsertFoodBottomSheetContent(
        descriptionText = "",
        onDescriptionChange = {},
        onSave = {}
    )
}
