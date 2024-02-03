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

package com.ngapps.phototime.feature.calendar.calendar.ui.collapsed

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
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.CalendarDay
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.DaySelectionMode
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.RangeSelectionError
import com.ngapps.phototime.feature.calendar.calendar.util.MultiplePreviews
import com.ngapps.phototime.feature.calendar.calendar.util.onDayClicked
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Creates a composable function for the CalendarCollapsed component.
 * @param modifier The modifier for styling or positioning the component.
 * @param daySelectionMode The mode for selecting days in the calendar.
 * @param showLabel Flag indicating whether to show labels for days of the week.
 * @param calendarColors The colors to be used in the calendar.
 * @param calendarTasks The events to be displayed in the calendar.
 * @param labelFormat The format function for generating labels for days of the week.
 * @param calendarDayConfig The configuration for styling individual days in the calendar.
 * @param dayContent The content to be displayed for each day in the calendar.
 * @param onDayClick The callback function when a day is clicked.
 * @param onRangeSelected The callback function when a range of days is selected.
 * @param onErrorRangeSelected The callback function when there is an error in selecting a range of days.
 */
@Composable
internal fun CalendarCollapsed(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    daySelectionMode: DaySelectionMode = DaySelectionMode.Single,
    showLabel: Boolean = true,
    calendarColors: CalendarColors = CalendarColors.default(),
    calendarTasks: CalendarTasks = CalendarTasks(),
    labelFormat: (DayOfWeek) -> String = {
        it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    },
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
    dayContent: @Composable ((LocalDate) -> Unit)? = null,
    onDayClick: (LocalDate) -> Unit = {},
    onRangeSelected: (CalendarSelectedDayRange, List<TaskResource>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {},
    onExpandClick: () -> Unit,
) {
    val weekValue by viewModel.weekValue.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedRange = remember { mutableStateOf<CalendarSelectedDayRange?>(null) }

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
                items(weekValue) { item ->
                    if (dayContent != null) {
                        dayContent(item)
                    } else {
                        CalendarDay(
                            date = item,
                            calendarColors = calendarColors.color,
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
                            selectedDate = selectedDate,
                            calendarTasks = calendarTasks,
                            calendarDayConfig = calendarDayConfig,
                            selectedRange = selectedRange.value,
                        )
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
                                    .clickable { onExpandClick() },
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

@MultiplePreviews
@Composable
fun CalendarCollapsedPreview() {
    CalendarCollapsed(
        daySelectionMode = DaySelectionMode.Single,
        onExpandClick = {},
    )
}
