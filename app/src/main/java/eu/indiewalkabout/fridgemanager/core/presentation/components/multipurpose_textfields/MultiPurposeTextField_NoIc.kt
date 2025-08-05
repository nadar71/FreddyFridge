package eu.indiewalkabout.fridgemanager.core.presentation.components.multipurpose_textfields

import eu.indiewalkabout.fridgemanager.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.DEFAULT_DATE_FORMAT
import eu.indiewalkabout.fridgemanager.core.data.locals.Constants.LOCAL_DATE_FORMAT
import eu.indiewalkabout.fridgemanager.core.presentation.components.TimePickerDialog
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.ComposeCalendar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorAccent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText_02
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreyVeryTransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


// Multipurpose text field with functions of drop down, location picker, configurable time picker,
// calendar and time picker too.

// NO LEADING AND TRAILING ICONS VERSION

// Unfortunately parameters leadingIcon and trailingIcon are not conditionally hideable completely:
// Compose reserve space to them before checking condition, so it left space even if they are null
// So necessary to create 4 versions at moment (18022025)...

// NB: use this component inside a ConstraintLayout: the focusRequester seems not working well inside Column, in case you're advised

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiPurposeTextField_NoIc(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    labelText: String,
    labelTextStyle: TextStyle = text_14(colorText, false),
    textStyle: TextStyle = text_14(colorText, false),
    initialText: String = "",
    hintText: String,
    hintTextStyle: TextStyle = text_14(lightGreyVeryTransparent, false),
    lines: Int = 3,
    strokeColor: Color = colorText,
    backgroundColor: Color = backgroundLightGrey,
    isPassword: Boolean = false,  // Activate password input mode
    isCalendar: Boolean = false,  // Activate calendar input mode
    daysDateOffset: Long = 0,   // days +- current date for date min
    isTimeSchedule: Boolean = false,  // Activate time schedule input mode
    isWheelPicker: Boolean = false, // Activate wheel picker input mode
    wheelPickerComponent: @Composable ((onItemSelected: (String) -> Unit) -> Unit)? = null, // Wheel picker component
    isDropDown: Boolean = false,  // Activate dropdown input mode
    dropDownItems: List<String> = emptyList(),
    initialDropDownItem: String? = null,
    onDropDownItemClick: (String) -> Unit = {},

    hasError: Boolean = false,    // External flag for error state, change stroke color on error
    hasHelpIcon: Boolean = false, // Flag to show/hide icon
    helpFieldText: String? = null, // Text for the additional field

    imeAction: ImeAction = ImeAction.Next, // set new line or next as action button in virtual keyboard

    onTextChanged: (String) -> Unit,

    ) {
    // Generic variable for TextField value
    var textFieldValue by remember { mutableStateOf(initialText) }

    var passwordVisible by remember { mutableStateOf(false) }
    val passwordIcon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

    var showAdditionalField by remember { mutableStateOf(false) } // help text

    var showCalendarDialog by rememberSaveable { mutableStateOf(false) }
    // var dateValue by remember { mutableStateOf("") }              // store date and calendar

    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }    // store time picker value

    var showWheelPickerDialog by rememberSaveable { mutableStateOf(false) }
    var wheelPickerValue by remember { mutableStateOf("") }              // store wheel picker value

    var dropDownExpanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(initialDropDownItem ?: "") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // ------------------------ FUNCTIONS OVER THAN TEXT FIELD, LOGIC -----------------------


    // CALENDAR
    if (showCalendarDialog) { // TODO: define different date format based on localization
        ComposeCalendar(
            startDate = LocalDate.now(),
            maxDate = LocalDate.MAX,
            onDone = { it: LocalDate ->
                textFieldValue = it.format(DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT as String?))
                onTextChanged(textFieldValue)
                showCalendarDialog = false
            },
            onDismiss = {
                showCalendarDialog = false
            }
        )

    }


    // TIMER PICKET
    TimePickerDialog(
        showTimePickerDialog = showTimePickerDialog,
        onDismissRequest = { showTimePickerDialog = false },
        onTimeSelected = { time ->
            selectedTime = time
            textFieldValue = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            onTextChanged(textFieldValue)
        }
    )


    // WHEEL PICKER
    if (showWheelPickerDialog && wheelPickerComponent != null) {
        AlertDialog(
            containerColor = secondaryColor,
            onDismissRequest = {
                onTextChanged(wheelPickerValue)
                showWheelPickerDialog = false
            }, // take value selected clicking outside dialog too
            confirmButton = {
                TextButton(
                    onClick = {
                        onTextChanged(wheelPickerValue)
                        showWheelPickerDialog = false
                    }) {
                    Text(
                        text = stringResource(R.string.generic_ok).uppercase(),
                        color = colorText_02
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showWheelPickerDialog = false }) {
                    Text(
                        text = stringResource(id = R.string.generic_cancel),
                        color = colorText_02
                    )
                }
            },
            text = {
                wheelPickerComponent { selectedValue ->
                    wheelPickerValue = selectedValue
                    textFieldValue = wheelPickerValue
                }
            }
        )
    }



    // ------------------------------------------ UI -----------------------------------------------

    Box(modifier = modifier) {
        Column(
            /*modifier = Modifier
                .background(backgroundColor)*/
        ) {
            Row(
                /*modifier = Modifier
                    .background(Color.Transparent)*/
            ) {
                Text(
                    text = labelText,
                    style = labelTextStyle,
                )
                if (hasHelpIcon) {
                    Image(
                        painter = painterResource(R.drawable.ic_help_outline),
                        contentDescription = "Show additional field",
                        modifier = Modifier
                            .size(26.dp)
                            .padding(8.dp, 0.dp, 0.dp, 8.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, // Disable ripple effect
                            ) { showAdditionalField = !showAdditionalField }
                    )
                }
            }


            Spacer(modifier = Modifier.height(4.dp))


            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (hasError) colorAccent else strokeColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // Disable ripple effect
                    ) {
                        when {
                            isCalendar -> showCalendarDialog = true
                            isTimeSchedule -> showTimePickerDialog = true
                            isWheelPicker -> showWheelPickerDialog = true
                            isDropDown -> dropDownExpanded = true
                        }
                    }
            ) {
                TextField(
                    value = if (isDropDown) selectedItem else textFieldValue,
                    textStyle = textStyle,
                    onValueChange = { value ->
                        if (!isCalendar && !isDropDown && !isWheelPicker) {
                            textFieldValue = value
                            onTextChanged(value)
                        }
                    },
                    placeholder = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = hintText,
                            style = hintTextStyle,
                        )
                    },

                    colors = TextFieldDefaults.colors(
                        cursorColor = colorText,
                        errorCursorColor = Color.Transparent,
                        selectionColors = TextSelectionColors(
                            Color.Transparent,
                            Color.Transparent
                        ),
                        focusedTextColor = backgroundLightGrey,
                        focusedContainerColor = if (isCalendar || isDropDown) backgroundColor else backgroundColor,
                        unfocusedContainerColor = if (isCalendar || isDropDown) backgroundColor else backgroundColor,
                        disabledContainerColor = if (isCalendar || isDropDown) backgroundColor else Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
                        imeAction = imeAction,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) },
                    ),
                    visualTransformation = if (isPassword) {
                        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },

                    modifier = Modifier
                        // .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .height((lines * 20).dp)
                        /*.border(
                            width = 1.dp,
                            color = if (hasError) ColorAlert else strokeColor,
                            shape = RoundedCornerShape(8.dp)
                        )*/
                        .padding(0.dp)
                        .focusRequester(focusRequester),
                    readOnly = isDropDown || isCalendar || isTimeSchedule || isWheelPicker,
                    maxLines = lines,
                    singleLine = false
                )

                // Transparent overlay to capture touches
                if (isCalendar || isTimeSchedule || isWheelPicker || isDropDown) {
                    Card(
                        modifier = Modifier
                            .matchParentSize() // Ensures the overlay matches the TextField dimensions
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, // Disable ripple effect
                            ) {
                                when {
                                    isCalendar -> showCalendarDialog = true
                                    isTimeSchedule -> showTimePickerDialog = true
                                    isWheelPicker -> showWheelPickerDialog = true
                                    isDropDown -> dropDownExpanded = true
                                }
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {}
                }
            }


            if (showAdditionalField && helpFieldText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = helpFieldText,
                    style = hintTextStyle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 8.dp, 0.dp, 0.dp)
                )
            }


            DropdownMenu(
                expanded = dropDownExpanded,
                /*offset = DpOffset(150.dp, 0.dp),
                shape = RoundedCornerShape(8.dp),*/
                onDismissRequest = { dropDownExpanded = false },
                modifier = Modifier
                    .background(secondaryColor)     // Set background color
                    .fillMaxWidth()

            ) {
                dropDownItems.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                style = text_14(colorText, false)
                            )
                        },
                        onClick = {
                            selectedItem = item
                            textFieldValue = item
                            onTextChanged(textFieldValue)
                            dropDownExpanded = false
                            onDropDownItemClick(item)
                        })
                }
            }


        } // column end

    } // box end
}






