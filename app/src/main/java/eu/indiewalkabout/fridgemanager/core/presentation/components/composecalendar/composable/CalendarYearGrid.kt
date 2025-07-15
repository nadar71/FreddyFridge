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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.daterange.DateRange
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import java.time.LocalDate

@Composable
internal fun CalendarYearGrid(
    gridState: LazyGridState,
    dateRangeByYear: DateRange,
    selectedYear: Int,
    currentYear: Int,
    onYearSelected: (Int) -> Unit
) {

    LazyVerticalGrid(
        modifier = Modifier.background(primaryColor),
        columns = GridCells.Fixed(3),
        state = gridState,
        horizontalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        dateRangeByYear.forEach {
            item {
                CalendarYear(
                    year = it.year,
                    isSelectedYear = it.year == selectedYear,
                    isCurrentYear = it.year == currentYear,
                    setSelectedYear = { year ->
                        onYearSelected(year)
                    }
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CalendarYearGridPreview() {
    val gridState = rememberLazyGridState()
    val today = LocalDate.now()
    val currentYear = today.year
    val dateRangeByYear = DateRange(today.minusYears(5), today.plusYears(5))

    CalendarYearGrid(
        gridState = gridState,
        dateRangeByYear = dateRangeByYear,
        selectedYear = currentYear,
        currentYear = currentYear,
        onYearSelected = { selectedYear ->
            // Handle year selection
            println("Selected year: $selectedYear")
        }
    )
}