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

package com.ngapps.phototime.feature.calendar.calendarEndless

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.feature.calendar.calendarEndless.daterange.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvent
import com.ngapps.phototime.feature.calendar.calendarEndless.paging.CalendarPagingController
import com.ngapps.phototime.feature.calendar.calendarEndless.paging.rememberCalendarPagingController
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.color.CalendarColors
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.header.CalendarTextConfig
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvents
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

/**
 * Displays a Calendar widget that allows selecting and displaying dates.
 *
 * @param modifier The modifier to be applied to the Calendar.
 * @param showLabel Determines whether to show labels for days of the week.
 * @param pagingController The paging controller for the Calendar.
 * @param calendarHeaderTextConfig The configuration for the Calendar header text.
 * @param calendarColors The colors to be used for styling the Calendar.
 * @param events The events to be displayed in the Calendar.
 * @param calendarDayConfig The configuration for individual days in the Calendar.
 * @param contentPadding The padding to be applied to the entire Calendar.
 * @param monthContentPadding The padding to be applied to each month in the Calendar.
 * @param dayContent The content composable for customizing the display of each day.
 * @param weekValueContent The content composable for customizing the display of the week values.
 * @param headerContent The content composable for customizing the header of each month.
 * @param daySelectionMode The mode for selecting days in the Calendar.
 * @param onDayClicked The callback function to be invoked when a day is clicked.
 * @param onRangeSelected The callback function to be invoked when a range of days is selected.
 * @param onErrorRangeSelected The callback function to handle errors during range selection.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EndlessCalendar(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    pagingController: CalendarPagingController = rememberCalendarPagingController(),
    calendarType: CalendarType = CalendarType.Horizontal,
    calendarHeaderTextConfig: CalendarTextConfig? = null,
    calendarColors: CalendarColors = CalendarColors.default(),
    events: CalendarEvents = CalendarEvents(),
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    monthContentPadding: PaddingValues = PaddingValues(4.dp),
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
    weekValueContent: (@Composable () -> Unit)? = null,
    headerContent: (@Composable (Month, Int) -> Unit)? = null,
    daySelectionMode: DaySelectionMode = DaySelectionMode.Single,
    onDayClicked: (LocalDate, List<CalendarEvent>) -> Unit = { _, _ -> },
    onRangeSelected: (CalendarSelectedDayRange, List<CalendarEvent>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {}
) {
    if (calendarType == CalendarType.Vertical) {
        CalendarEndless(
            modifier = modifier,
            showLabel = showLabel,
            pagingController = pagingController,
            calendarHeaderTextConfig = calendarHeaderTextConfig,
            calendarColors = calendarColors,
            onDayClick = onDayClicked,
            events = events,
            calendarDayConfig = calendarDayConfig,
            contentPadding = contentPadding,
            monthContentPadding = monthContentPadding,
            dayContent = dayContent,
            weekValueContent = weekValueContent,
            daySelectionMode = daySelectionMode,
            onRangeSelected = onRangeSelected,
            onErrorRangeSelected = onErrorRangeSelected,
            headerContent = headerContent
        )
    } else {
        CalendarSmall()
    }
}
