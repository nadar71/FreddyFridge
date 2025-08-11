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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.daterange.DateRange
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.daterange.DateRangeStep
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.daterange.rangeTo
import eu.indiewalkabout.fridgemanager.core.presentation.components.composecalendar.model.DateWrapper
import java.time.LocalDate

@Composable
internal fun CalendarGrid(
    pagerDate: LocalDate,
    dateRange: DateRange,
    selectedDate: LocalDate,
    onSelected: (LocalDate) -> Unit,
) {
    // LogCompositions("CalendarGrid")

    val gridSpacing = 4.dp
    val today = LocalDate.now()

    val firstWeekDayOfMonth = pagerDate.dayOfWeek
    val pagerMonth = pagerDate.month

    val gridStartDay = pagerDate
        .minusDays(firstWeekDayOfMonth.value.toLong() - 1)
    val gridEndDay = gridStartDay.plusDays(41)

    val dates = (gridStartDay.rangeTo(gridEndDay) step DateRangeStep.Day()).map {
        val isCurrentMonth = it.month == pagerMonth
        val isCurrentDay = it == today
        val isSelectedDay = it == selectedDate
        val isInDateRange = it in dateRange

        DateWrapper(
            it,
            isSelectedDay,
            isCurrentDay,
            isCurrentMonth,
            isInDateRange,
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        verticalArrangement = Arrangement.spacedBy(gridSpacing)
    ) {
        items(dates) {
            CalendarDay(
                date = it,
                onSelected = onSelected
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun CalendarGridPreview() {
    val today = LocalDate.now()
    val dateRange = DateRange(today.minusDays(7), today.plusDays(7))

    CalendarGrid(
        pagerDate = today,
        dateRange = dateRange,
        selectedDate = today,
        onSelected = { selectedDate ->
            // Handle date selection
            println("Selected date: $selectedDate")
        }
    )
}
