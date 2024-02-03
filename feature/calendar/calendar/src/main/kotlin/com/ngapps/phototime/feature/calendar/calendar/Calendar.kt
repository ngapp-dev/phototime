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

package com.ngapps.phototime.feature.calendar.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapps.phototime.core.designsystem.component.PtDivider
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.feature.calendar.calendar.color.CalendarColors
import com.ngapps.phototime.feature.calendar.calendar.ui.collapsed.CalendarCollapsed
import com.ngapps.phototime.feature.calendar.calendar.ui.collapsed.util.getNext7Dates
import com.ngapps.phototime.feature.calendar.calendar.ui.collapsed.util.getPrevious7Dates
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendar.ui.component.header.CalendarHeader
import com.ngapps.phototime.feature.calendar.calendar.ui.component.header.CalendarTextConfig
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.CalendarExpanded
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.DaySelectionMode
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.RangeSelectionError
import com.ngapps.phototime.feature.calendar.calendarEndless.MIN_DAY
import com.ngapps.phototime.feature.calendar.calendarEndless.WEEK_LENGTH
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import java.time.Month

/**
 * Composable function that represents a calendar component.
 *
 * @param modifier The modifier for styling or positioning the calendar.
 * @param showLabel Determines whether to show the labels for days of the week.
 * @param calendarTasks The events to be displayed in the calendar.
 * @param calendarHeaderTextConfig The configuration for the header text in the calendar.
 * @param calendarColors The colors used for styling the calendar.
 * @param calendarDayConfig The configuration for each day cell in the calendar.
 * @param dayContent The content to be displayed inside each day cell of the calendar.
 * @param daySelectionMode The mode for selecting days in the calendar.
 * @param onDayClick Callback function triggered when a day cell is clicked.
 * @param onRangeSelected Callback function triggered when a range of days is selected.
 * @param onErrorRangeSelected Callback function triggered when an error occurs during range selection.
 */
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    calendarTasks: CalendarTasks = CalendarTasks(),
    calendarHeaderTextConfig: CalendarTextConfig? = null,
    calendarColors: CalendarColors = CalendarColors.default(),
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
    daySelectionMode: DaySelectionMode = DaySelectionMode.Single,
    onDayClick: (LocalDate) -> Unit = {},
    onRangeSelected: (CalendarSelectedDayRange, List<TaskResource>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {}
) {

    CalendarContent(
        modifier = modifier,
        daySelectionMode = daySelectionMode,
        showLabel = showLabel,
        calendarTasks = calendarTasks,
        calendarHeaderTextConfig = calendarHeaderTextConfig,
        calendarColors = calendarColors,
        calendarDayConfig = calendarDayConfig,
        onDayClick = onDayClick,
        dayContent = dayContent,
        onRangeSelected = onRangeSelected,
        onErrorRangeSelected = onErrorRangeSelected,
    )
}

/**
 * Composable function that represents a calendar component.
 *
 * @param modifier The modifier for styling or positioning the calendar.
 * @param showLabel Determines whether to show the labels for days of the week.
 * @param calendarTasks The events to be displayed in the calendar.
 * @param calendarHeaderTextConfig The configuration for the header text in the calendar.
 * @param calendarColors The colors used for styling the calendar.
 * @param calendarDayConfig The configuration for each day cell in the calendar.
 * @param daySelectionMode The mode for selecting days in the calendar.
 * @param dayContent The content to be displayed inside each day cell of the calendar.
 * @param onDayClick Callback function triggered when a day cell is clicked.
 * @param onRangeSelected Callback function triggered when a range of days is selected.
 * @param onErrorRangeSelected Callback function triggered when an error occurs during range selection.
 */
@Composable
fun CalendarContent(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    showLabel: Boolean = true,
    calendarTasks: CalendarTasks = CalendarTasks(),
    calendarHeaderTextConfig: CalendarTextConfig? = null,
    calendarColors: CalendarColors = CalendarColors.default(),
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
    daySelectionMode: DaySelectionMode = DaySelectionMode.Single,
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
    onDayClick: (LocalDate) -> Unit = {},
    onRangeSelected: (CalendarSelectedDayRange, List<TaskResource>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {}
) {

    val weekValue by viewModel.weekValue.collectAsStateWithLifecycle()
    val displayedMonth by viewModel.displayedMonth.collectAsStateWithLifecycle()
    val displayedYear by viewModel.displayedYear.collectAsStateWithLifecycle()

    val currentMonthValue = displayedMonth
    val currentYear = displayedYear
    val newHeaderTextConfig = calendarHeaderTextConfig ?: CalendarTextConfig.previewDefault()
    val calendarExpanded by viewModel.calendarExpanded.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.animateContentSize(),
    ) {
        PtDivider(color = MaterialTheme.colorScheme.outline)
        CalendarHeader(
            month = currentMonthValue,
            year = currentYear,
            calendarTextConfig = newHeaderTextConfig,
            calendarColors = calendarColors,
            onPreviousClick = {
                if (!calendarExpanded) {
                    val firstDayOfDisplayedWeek = weekValue.first()
                    viewModel.onChangeWeekValue(firstDayOfDisplayedWeek.getPrevious7Dates())
                } else {
                    val year = if (currentMonthValue == Month.JANUARY) {
                        displayedYear - 1
                    } else {
                        displayedYear
                    }
                    viewModel.onChangeDisplayedYear(year)
                    viewModel.onChangeDisplayedMonth(displayedMonth - 1)
                }
            },
            onNextClick = {
                if (!calendarExpanded) {
                    val lastDayOfDisplayedWeek = weekValue.last().plus(1, DateTimeUnit.DAY)
                    viewModel.onChangeWeekValue(lastDayOfDisplayedWeek.getNext7Dates())
                } else {
                    val year = if (currentMonthValue == Month.DECEMBER) {
                        displayedYear + 1
                    } else {
                        displayedYear
                    }
                    viewModel.onChangeDisplayedYear(year)
                    viewModel.onChangeDisplayedMonth(displayedMonth + 1)
                }
            },
        )
        if (!calendarExpanded) {
            CalendarCollapsed(
                modifier = modifier,
                showLabel = showLabel,
                calendarColors = calendarColors,
                calendarTasks = calendarTasks,
                calendarDayConfig = calendarDayConfig,
                onDayClick = onDayClick,
                dayContent = dayContent,
                daySelectionMode = daySelectionMode,
                onRangeSelected = onRangeSelected,
                onErrorRangeSelected = onErrorRangeSelected,
                onExpandClick = { viewModel.onChangeCalendarExpanded(true) },
            )
        } else {
            CalendarExpanded(
                modifier = modifier,
                showLabel = showLabel,
                calendarColors = calendarColors,
                calendarTasks = calendarTasks,
                calendarDayConfig = calendarDayConfig,
                onDayClick = onDayClick,
                dayContent = dayContent,
                daySelectionMode = daySelectionMode,
                onRangeSelected = onRangeSelected,
                onErrorRangeSelected = onErrorRangeSelected,
                onCollapseClick = { viewModel.onChangeCalendarExpanded(false) },
            )
        }
    }
}

private fun generateMonths(currentMonth: Int): List<Month> {
    val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val current = currentDate.month
    val list = Month.values().toList()
    return List(12) { index ->
        current.plus((index - 2).toLong())
    }
}


private fun generateDates(currentWeek: Int): List<LocalDate> {
    val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val startOfWeek = currentDate.minus(
        (currentDate.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal).toLong(),
        DateTimeUnit.DAY,
    )
    val weekStartDate = startOfWeek.plus(currentWeek * WEEK_LENGTH.toLong(), DateTimeUnit.DAY)
    return List(WEEK_LENGTH) { index ->
        weekStartDate.plus((index - MIN_DAY).toLong(), DateTimeUnit.DAY)
    }
}
