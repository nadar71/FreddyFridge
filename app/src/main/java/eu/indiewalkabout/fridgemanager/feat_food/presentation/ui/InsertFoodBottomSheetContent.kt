package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.SimpleTextField
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.LOCAL_DATE_FORMAT
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.ComposeCalendar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.NumberPickerWithTitle
import eu.indiewalkabout.fridgemanager.feat_food.presentation.util.VoiceRecognitionManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun InsertFoodBottomSheetContent(
    descriptionText: String,
    onDescriptionChange: (String) -> Unit
) {

    var TAG = "InsertFoodBottomSheetContent"
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    var showCalendarDialog by remember { mutableStateOf(false) }
    var showQuantityWheelPicker by remember { mutableStateOf(false) }

    var localeDateText by remember { mutableStateOf("") }
    var quantityNumText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }

    // ------------------------------------- LOGIC -------------------------------------------------
    val voiceManager = remember {
        VoiceRecognitionManager(
            context = context,
            onResult = {
                resultText = it
                Log.d(TAG, "InsertFoodBottomSheetContent: result speech to text$resultText")
                // onSpeechResult(it)
                isListening = false
            },
            onError = {
                resultText = it
                isListening = false
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
        }
    }

    // ------------------------------------- DIALOG ------------------------------------------------

    // Calendar widget
    if (showCalendarDialog) { // TODO: define different date format based on localization
        ComposeCalendar(
            startDate = LocalDate.now(),
            maxDate = LocalDate.MAX,
            onDone = { it: LocalDate ->
                Log.d(TAG, "InsertFoodBottomSheetContent: date selected : $it")
                /*textFieldValue = it.format(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT as String?))
                onTextChanged(textFieldValue)*/
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
        )
    }



    // ------------------------------------- UI ----------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                primaryColor.copy(alpha = 0.5f), // You can keep this background if you want a fallback or overlay color
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp)) // Clip the Box to the rounded shape
    ) {
        // 1. Background Image
        Image(
            painter = painterResource(id = R.drawable.food_background_half), // Your image resource
            contentDescription = null, // Content description is not needed for a background image
            modifier = Modifier.matchParentSize(),
                //.fillMaxSize(),
            // Make the image fill the size of the parent Box
            // Or .fillMaxSize() if you want it to potentially exceed the Box bounds slightly to cover it
            contentScale = ContentScale.Crop, // Scale the image to fill the bounds without distortion
            alpha = 0.5f // Optional: Adjust transparency of the background image
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
                backgroundColor = primaryColor,
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
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    hintText = stringResource(R.string.insert_expiring_date_hint),
                    hintTextStyle = text_14(colorHintText, false),
                    inputTextStyle = text_14(colorText, false),
                    value = descriptionText,
                    onValueChange = onDescriptionChange,
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
                    onValueChange = onDescriptionChange,
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_mic_white),
                    contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 16.dp)
                        .clickable {
                            if (!isListening) {
                                voiceManager.startListening()
                                isListening = true
                            } else {
                                voiceManager.stopListening()
                                isListening = false
                            }
                        }
                )
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
                    value = descriptionText,
                    onValueChange = onDescriptionChange,
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_quantity_add),
                    contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 16.dp)
                        .clickable {
                            showQuantityWheelPicker = true
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))



        }
    }
}


@Preview(showBackground = true)
@Composable
fun InsertFoodBottomSheetContentPreview() {
    InsertFoodBottomSheetContent(
        descriptionText = "",
        onDescriptionChange = {},
    )
}
