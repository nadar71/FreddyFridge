package eu.indiewalkabout.fridgemanager.core.presentation.components



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor

import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    showTimePickerDialog: Boolean,
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    if (showTimePickerDialog) {
        // Initialize TimePickerState with default time
        val timePickerState = rememberTimePickerState(
            initialHour = 0,
            initialMinute = 0
        )

        AlertDialog(
            containerColor = secondaryColor,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = {
                    // Create LocalTime from selected values in TimePickerState
                    val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    onTimeSelected(selectedTime)
                    onDismissRequest()
                }) {
                    Text(
                        text = stringResource(id = R.string.generic_ok) ,
                        color = primaryColor
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(
                        text = stringResource(id = R.string.generic_cancel),
                        color = primaryColor
                    )
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TimePickerDefaults.colors(
                        clockDialColor = primaryColor.copy(alpha = 0.3f),
                        clockDialSelectedContentColor = secondaryColor,
                        clockDialUnselectedContentColor = backgroundLightGrey,

                        selectorColor = primaryColor,
                        containerColor = primaryColor.copy(alpha = 0.3f),
                        periodSelectorSelectedContainerColor = primaryColor,
                        periodSelectorSelectedContentColor = secondaryColor,

                        timeSelectorSelectedContainerColor = primaryColor,
                        timeSelectorUnselectedContainerColor = primaryColor.copy(alpha = 0.3f),
                        timeSelectorSelectedContentColor = secondaryColor,
                        timeSelectorUnselectedContentColor = secondaryColor,
                    ),
                    layoutType = TimePickerDefaults.layoutType()
                )
            }
        )
    }
}



@Preview (showBackground = true)
@Composable
fun MainScreen() {
    var showTimePickerDialog by remember { mutableStateOf(true) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "No time selected",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = { showTimePickerDialog = true }) {
            Text("Select Time")
        }

        TimePickerDialog(
            showTimePickerDialog = showTimePickerDialog,
            onDismissRequest = { showTimePickerDialog = false },
            onTimeSelected = { time ->
                selectedTime = time
            }
        )
    }
}

