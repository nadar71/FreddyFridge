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

package eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorAccent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor

@Composable
internal fun CalendarYear(
    year: Int,
    isSelectedYear: Boolean,
    isCurrentYear: Boolean,
    setSelectedYear: (Int) -> Unit
) {
    // LogCompositions("CalendarYear")

    val modifier = Modifier
        // .background(colorAccent)
        .fillMaxWidth()

    if (isSelectedYear) {
        Button(
            modifier = modifier,
            onClick = { setSelectedYear(year) },
            colors = ButtonDefaults.buttonColors(containerColor = colorAccent)
        ) {
            Text(text = "$year", color = primaryColor, maxLines = 1)
        }
    } else if (isCurrentYear) {
        OutlinedButton(
            modifier = modifier,
            onClick = { setSelectedYear(year) },
            border = BorderStroke(1.dp, colorAccent),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            )
        ) {
            Text(text = "$year", color = colorText, maxLines = 1)
        }
    } else {
        TextButton(
            modifier = modifier,
            onClick = { setSelectedYear(year) },
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text(text = "$year", color = colorText, maxLines = 1)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CalendarYearPreview_selected() {
    CalendarYear(
        year = 2023,
        isSelectedYear = true,
        isCurrentYear = false,
        setSelectedYear = { year ->
            // Handle year selection
            println("Selected year: $year")
        }
    )
}


@Preview(showBackground = true)
@Composable
fun CalendarYearPreview_current() {
    CalendarYear(
        year = 2023,
        isSelectedYear = false,
        isCurrentYear = true,
        setSelectedYear = { year ->
            // Handle year selection
            println("Selected year: $year")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CalendarYearPreview_normal() {
    CalendarYear(
        year = 2023,
        isSelectedYear = false,
        isCurrentYear = false,
        setSelectedYear = { year ->
            // Handle year selection
            println("Selected year: $year")
        }
    )
}