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

package com.ngapps.phototime.feature.calendar.calendar.ui.expanded

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapps.phototime.core.designsystem.component.SitDragHandle
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.feature.calendar.calendar.CalendarTasks
import com.ngapps.phototime.feature.calendar.calendar.CalendarViewModel
import com.ngapps.phototime.feature.calendar.calendar.color.CalendarColors
import com.ngapps.phototime.feature.calendar.calendar.ui.collapsed.util.isLeapYear
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.CalendarDay
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendar.util.MultiplePreviews
import com.ngapps.phototime.feature.calendar.calendar.util.onDayClicked
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.toLocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Internal composable function representing the CalendarExpanded component.
 *
 * @param daySelectionMode The day selection mode in the Calendar.
 * @param modifier The modifier for styling or positioning the Calendar.
 * @param showLabel Determines whether to show labels in the Calendar.
 * @param calendarColors The colors configuration for the Calendar.
 * @param calendarTasks The events associated with the Calendar.
 * @param calendarDayConfig The configuration for each day in the Calendar.
 * @param dayContent Custom content for rendering each day in the Calendar.
 * @param onDayClick Callback invoked when a day is clicked.
 * @param onRangeSelected Callback invoked when a range of days is selected.
 * @param onErrorRangeSelected Callback invoked when an error occurs during range selection.
 */
@Composable
internal fun CalendarExpanded(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    daySelectionMode: DaySelectionMode,
    showLabel: Boolean = true,
    calendarColors: CalendarColors = CalendarColors.default(),
    calendarTasks: CalendarTasks = CalendarTasks(),
    labelFormat: (DayOfWeek) -> String = {
        it.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault(),
        )
    },
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
    onDayClick: (LocalDate) -> Unit = {},
    onRangeSelected: (CalendarSelectedDayRange, List<TaskResource>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {},
    onCollapseClick: () -> Unit,
) {


    val selectedRange = remember { mutableStateOf<CalendarSelectedDayRange?>(null) }
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val displayedMonth by viewModel.displayedMonth.collectAsStateWithLifecycle()
    val displayedYear by viewModel.displayedYear.collectAsStateWithLifecycle()
    val currentMonth = displayedMonth
    val currentYear = displayedYear

    val daysInMonth = currentMonth.length(currentYear.isLeapYear())
    val monthValue = currentMonth.value.toString().padStart(2, '0')
    val startDayOfMonth = "$currentYear-$monthValue-01".toLocalDate()
    val firstDayOfMonth = startDayOfMonth.dayOfWeek

    Column(
        modifier = modifier
            .background(color = calendarColors.color.calendarBackgroundColor)
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(all = 8.dp),
    ) {
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Fixed(7),
            content = {
                if (showLabel) {
                    items(DayOfWeek.values()) { item ->
                        Text(
                            modifier = Modifier,
                            color = calendarDayConfig.textColor,
                            fontSize = calendarDayConfig.textSize,
                            text = labelFormat(item),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                items(DayOfWeek.values()) {
                    Box(
                        modifier = Modifier.height(8.dp),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Divider(color = calendarColors.color.calendarTextColor)
                    }
                }
                items((getFirstDayOfMonth(firstDayOfMonth)..daysInMonth).toList()) {
                    if (it > 0) {
                        val day = calculateDay(it, currentMonth, currentYear)
                        if (dayContent != null) {
                            dayContent(day)
                        } else {
                            CalendarDay(
                                date = day,
                                selectedDate = selectedDate,
                                calendarColors = calendarColors.color,
                                calendarTasks = calendarTasks,
                                calendarDayConfig = calendarDayConfig,
                                selectedRange = selectedRange.value,
                                onDayClick = { clickedDate, tasks ->
                                    onDayClicked(
                                        clickedDate,
                                        tasks,
                                        daySelectionMode,
                                        selectedRange,
                                        onRangeSelected = { range, selectedTasks ->
                                            if (range.end < range.start) {
                                                onErrorRangeSelected(RangeSelectionError.EndIsBeforeStart)
                                            } else {
                                                onRangeSelected(range, selectedTasks)
                                            }
                                        },
                                        onDayClick = { date ->
                                            viewModel.onChangeSelectedDate(date)
                                            onDayClick(date)
                                        },
                                    )
                                },
                            )
                        }
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            SitDragHandle(
                                width = 108.dp,
                                modifier = Modifier
                                    .clickable { onCollapseClick() },
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // NOTE: Add space for the content to clear the "offline" snackbar.
                        // TODO: Check that the Scaffold handles this correctly in SitApp
                        // NOTE: if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                    }
                }
            },
        )
    }
}

/**
 * Calculates the offset to determine the first day of the month based on the provided first day of the month.
 *
 * @param firstDayOfMonth The first day of the month.
 * @return The offset value representing the first day of the month.
 */
private fun getFirstDayOfMonth(firstDayOfMonth: DayOfWeek) = -(firstDayOfMonth.value).minus(2)

/**
 * Calculates a LocalDate object based on the provided day, current month, and current year.
 *
 * @param day The day of the month.
 * @param currentMonth The current month.
 * @param currentYear The current year.
 * @return The LocalDate object representing the specified day, month, and year.
 */
private fun calculateDay(day: Int, currentMonth: Month, currentYear: Int): LocalDate {
    val monthValue = currentMonth.value.toString().padStart(2, '0')
    val dayValue = day.toString().padStart(2, '0')
    return "$currentYear-$monthValue-$dayValue".toLocalDate()
}

@Composable
@MultiplePreviews
private fun CalendarExpandedPreview() {
    CalendarExpanded(
        daySelectionMode = DaySelectionMode.Range,
        onCollapseClick = {},
    )
}
