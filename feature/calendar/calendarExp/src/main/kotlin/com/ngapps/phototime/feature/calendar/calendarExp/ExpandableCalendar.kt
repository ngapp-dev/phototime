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

package com.ngapps.phototime.feature.calendar.calendarExp

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.feature.calendar.calendarExp.core.CalendarExpIntent
import com.ngapps.phototime.feature.calendar.calendarExp.core.Period
import com.ngapps.phototime.feature.calendar.calendarExp.utils.getWeekStartDate
import com.ngapps.phototime.feature.calendar.calendarExp.component.InlineCalendar
import com.ngapps.phototime.feature.calendar.calendarExp.component.MonthText
import com.ngapps.phototime.feature.calendar.calendarExp.component.MonthViewCalendar
import com.ngapps.phototime.feature.calendar.calendarExp.component.ToggleExpandCalendarButton
import com.ngapps.phototime.feature.calendar.calendarExp.core.CalendarTheme
import com.ngapps.phototime.feature.calendar.calendarExp.core.calendarDefaultTheme
import java.time.LocalDate

@Composable
fun ExpandableCalendar(
    onDayClick: (LocalDate) -> Unit,
    theme: CalendarTheme = calendarDefaultTheme
) {
    val viewModel: CalendarExpViewModel = viewModel()
    val loadedDates = viewModel.visibleDates.collectAsState()
    val selectedDate = viewModel.selectedDate.collectAsState()
    val calendarExpanded = viewModel.calendarExpanded.collectAsState()
    val currentMonth = viewModel.currentMonth.collectAsState()

    ExpandableCalendar(
        loadedDates = loadedDates.value,
        selectedDate = selectedDate.value,
        currentMonth = currentMonth.value,
        onIntent = viewModel::onIntent,
        calendarExpanded = calendarExpanded.value,
        theme = theme,
        onDayClick = onDayClick,
    )
}

@Composable
private fun ExpandableCalendar(
    loadedDates: Array<List<LocalDate>>,
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    onIntent: (CalendarExpIntent) -> Unit,
    calendarExpanded: Boolean,
    theme: CalendarTheme,
    onDayClick: (LocalDate) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .animateContentSize()
            .background(theme.backgroundColor),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .background(theme.headerBackgroundColor),
        ) {
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    imageVector = PtIcons.KeyboardArrowLeft,
                    contentDescription = "Back",
                )
            }
            Spacer(Modifier.weight(1f))
            MonthText(selectedMonth = currentMonth, theme = theme)
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {

                },
            ) {
                Icon(
                    imageVector = PtIcons.KeyboardArrowRight,
                    contentDescription = "Next",
                )
            }
        }
        Column {
            if (calendarExpanded) {
                MonthViewCalendar(
                    loadedDates,
                    selectedDate,
                    theme = theme,
                    currentMonth = currentMonth,
                    loadDatesForMonth = { yearMonth ->
                        onIntent(
                            CalendarExpIntent.LoadNextDates(
                                yearMonth.atDay(
                                    1,
                                ),
                                period = Period.MONTH,
                            ),
                        )
                    },
                    onDayClick = {
                        onIntent(CalendarExpIntent.SelectDate(it))
                        onDayClick(it)
                    },
                )
            } else {
                InlineCalendar(
                    loadedDates,
                    selectedDate,
                    theme = theme,
                    loadNextWeek = { nextWeekDate ->
                        onIntent(
                            CalendarExpIntent.LoadNextDates(
                                nextWeekDate,
                            ),
                        )
                    },
                    loadPrevWeek = { endWeekDate ->
                        onIntent(
                            CalendarExpIntent.LoadNextDates(
                                endWeekDate.minusDays(1).getWeekStartDate(),
                            ),
                        )
                    },
                    onDayClick = {
                        onIntent(CalendarExpIntent.SelectDate(it))
                        onDayClick(it)
                    },
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                ToggleExpandCalendarButton(
                    isExpanded = calendarExpanded,
                    expand = { onIntent(CalendarExpIntent.ExpandCalendarExp) },
                    collapse = { onIntent(CalendarExpIntent.CollapseCalendarExp) },
                    color = theme.headerTextColor,
                )
            }
        }
    }
}








