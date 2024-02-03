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

package com.ngapps.phototime.feature.calendar.calendarEndless.ui.month

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.ngapps.phototime.feature.calendar.calendarEndless.CalendarDates
import com.ngapps.phototime.feature.calendar.calendarEndless.daterange.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvent
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvents
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.color.CalendarColor
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.day.CalendarDay
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.header.CalendarTextConfig
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.header.getTitleText
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Renders a single month view of the Calendar.
 *
 * @param calendarDates The CalendarDates object containing the dates for the month.
 * @param month The month to be rendered.
 * @param year The year of the month.
 * @param events The CalendarEvents object containing the events for the month.
 * @param calendarColor The CalendarColor object defining the colors for the Calendar.
 * @param contentPadding The padding to be applied to the month view content.
 * @param calendarDayConfig The CalendarDayConfig object defining the configuration for the day cells.
 * @param calendarHeaderTextConfig The CalendarTextConfig object defining the configuration for the header text.
 * @param selectedRange The selected day range in the Calendar.
 * @param modifier The modifier to be applied to the month view.
 * @param selectedDate The currently selected date in the Calendar.
 * @param dayContent The composable function to render the content of each day cell.
 * @param headerContent The composable function to render the header content of the month view.
 * @param onDayClick The callback function when a day cell is clicked.
 */
@Composable
internal fun CalendarMonth(
    calendarDates: CalendarDates,
    month: Month,
    year: Int,
    events: CalendarEvents,
    calendarColor: CalendarColor,
    contentPadding: PaddingValues,
    calendarDayConfig: CalendarDayConfig,
    calendarHeaderTextConfig: CalendarTextConfig?,
    selectedRange: CalendarSelectedDayRange?,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    dayContent: @Composable ((LocalDate) -> Unit)? = null,
    headerContent: @Composable ((Month, Int) -> Unit)? = null,
    onDayClick: (LocalDate, List<CalendarEvent>) -> Unit = { _, _ -> },
) {
    val selectedDate = remember { mutableStateOf(selectedDate) }

    Column(
        modifier = modifier
            .padding(contentPadding)
            .background(calendarColor.backgroundColor)
    ) {
        if (headerContent != null) {
            headerContent(month, year)
        } else {
            calendarHeaderTextConfig?.let {
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 12.dp)
                        .wrapContentHeight()
                        .wrapContentWidth(),
                    color = it.calendarTextColor,
                    fontSize = it.calendarTextSize,
                    text = getTitleText(month, year),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start
                )
            }
        }

        calendarDates.dates.fastForEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.fastForEach { date ->
                    date?.let { nonNullDate ->
                        if (dayContent != null) {
                            dayContent(nonNullDate)
                        } else {
                            CalendarDay(
                                date = nonNullDate,
                                selectedDate = selectedDate.value,
                                selectedRange = selectedRange,
                                events = events,
                                onDayClick = { date, events ->
                                    selectedDate.value = date
                                    onDayClick(date, events)
                                },
                                calendarDayConfig = calendarDayConfig,
                                calendarColor = calendarColor,
                            )
                        }
                    } ?: run {
                        Box(modifier = Modifier.size(56.dp))
                    }
                }
            }
        }
    }
}
