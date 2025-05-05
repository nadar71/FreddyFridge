/*
 * Copyright 2022 Matteo Miceli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// https://github.com/lampione/ComposeCalendar
package eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar

import eu.indiewalkabout.fridgemanager.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.composable.CalendarContent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import java.text.DateFormat
import java.time.LocalDate

@Composable
fun ComposeCalendar(
    startDate: LocalDate = LocalDate.now(),
    minDate: LocalDate = LocalDate.MIN,
    maxDate: LocalDate = LocalDate.MAX,
    showSelectedDate: Boolean = true,
    selectedDateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT),
    onDone: (millis: LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    AlertDialog(
        modifier = Modifier
            .background(Color.Transparent),
        containerColor = primaryColor,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDone(selectedDate)
            }) {
                Text(stringResource(id = R.string.generic_ok), color = colorText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.generic_cancel), color = colorText)
            }
        },
        text = {
            CalendarContent(
                startDate = startDate,
                minDate = minDate,
                maxDate = maxDate,
                onDateSelected = { selectedDate = it },
                showSelectedDate = showSelectedDate,
                selectedDateFormat = selectedDateFormat
            )
        }
    )
}


@Preview(showBackground = true)
@Composable
fun ComposeCalendarPreview() {
    var selectedDateText by remember { mutableStateOf("") }
    var dismissText by remember { mutableStateOf("") }

    ComposeCalendar(
        onDone = { selectedDate ->
            selectedDateText = "Selected date: $selectedDate"
        },
        onDismiss = {
            dismissText = "Calendar dismissed"
        }
    )
}