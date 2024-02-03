/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ngapps.phototime.feature.calendar.calendar.util

import androidx.compose.runtime.MutableState
import com.ngapps.phototime.core.converters.isoDateToLocalDate
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.DaySelectionMode
import kotlinx.datetime.LocalDate

/**
 * Internal function invoked when a day is clicked.
 *
 * @param date The clicked date.
 * @param tasks The user tasks associated with the clicked date.
 * @param daySelectionMode The day selection mode.
 * @param selectedRange The state holding the selected day range.
 * @param onRangeSelected Callback invoked when a range of days is selected.
 * @param onDayClick Callback invoked when a day is clicked.
 */
internal fun onDayClicked(
    date: LocalDate,
    tasks: List<TaskResource>,
    daySelectionMode: DaySelectionMode,
    selectedRange: MutableState<CalendarSelectedDayRange?>,
    onRangeSelected: (CalendarSelectedDayRange, List<TaskResource>) -> Unit = { _, _ -> },
    onDayClick: (LocalDate) -> Unit = { }
) {
    when (daySelectionMode) {
        DaySelectionMode.Single -> {
            onDayClick(date)
        }

        DaySelectionMode.Range -> {
            val range = selectedRange.value
            selectedRange.value = when {
                range?.isEmpty() != false -> CalendarSelectedDayRange(start = date, end = date)
                range.isSingleDate() -> CalendarSelectedDayRange(start = range.start, end = date)
                else -> CalendarSelectedDayRange(start = date, end = date)
            }

            selectedRange.value?.let { rangeDates ->
                val selectedEvents = tasks
                    .filter { isoDateToLocalDate(it.scheduledTime.start) in (rangeDates.start..rangeDates.end) }
                    .toList()

                onRangeSelected(rangeDates, selectedEvents)
            }
        }
    }
}
