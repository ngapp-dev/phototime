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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.ngapps.phototime.feature.calendar.calendarEndless.daterange.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvent
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvents
import com.ngapps.phototime.feature.calendar.calendarEndless.paging.CalendarModelEntity
import com.ngapps.phototime.feature.calendar.calendarEndless.paging.CalendarPagingController
import com.ngapps.phototime.feature.calendar.calendarEndless.paging.rememberCalendarPagingController
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.color.CalendarColors
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.header.CalendarTextConfig
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.month.CalendarMonth
import com.ngapps.phototime.feature.calendar.calendarEndless.util.onDayClicked
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

private val WeekDays = listOf("M", "T", "W", "T", "F", "S", "S")

/**
 * Displays an endless Calendar widget that allows selecting and displaying dates.
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
 * @param onDayClick The callback function to be invoked when a day is clicked.
 * @param onRangeSelected The callback function to be invoked when a range of days is selected.
 * @param onErrorRangeSelected The callback function to handle errors during range selection.
 */
@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun CalendarEndless(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    pagingController: CalendarPagingController = rememberCalendarPagingController(),
    calendarHeaderTextConfig: CalendarTextConfig? = null,
    calendarColors: CalendarColors = CalendarColors.default(),
    events: CalendarEvents = CalendarEvents(),
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    monthContentPadding: PaddingValues = PaddingValues(4.dp),
    dayContent: (@Composable (LocalDate) -> Unit)? = null,
    weekValueContent: (@Composable () -> Unit)? = null,
    headerContent: (@Composable (Month, Int) -> Unit)? = null,
    daySelectionMode: DaySelectionMode = DaySelectionMode.Range,
    onDayClick: (LocalDate, List<CalendarEvent>) -> Unit = { _, _ -> },
    onRangeSelected: (CalendarSelectedDayRange, List<CalendarEvent>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {}
) {
    val calendarItems = pagingController.calendarItems.collectAsLazyPagingItems()
    val selectedRange = remember { mutableStateOf<CalendarSelectedDayRange?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        content = {
            if (weekValueContent != null) {
                stickyHeader {
                    weekValueContent()
                }
            } else {
                if (showLabel) {
                    stickyHeader {
                        CalendarStickerHeader(
                            calendarDayConfig.textColor,
                            calendarDayConfig.textSize
                        )
                    }
                }
            }
            items(
                count = calendarItems.itemCount,
                key = calendarItems.itemKey(),
                contentType = calendarItems.itemContentType()
            ) { index ->
                val calendarModel: CalendarModelEntity? = calendarItems[index]
                val dates: List<List<LocalDate?>>? = calendarModel?.dates?.chunked(7)
                if (dates != null) {
                    val currentMonthIndex = calendarModel.month.value.minus(1)
                    val defaultHeaderColor = CalendarTextConfig.default(
                        color = calendarColors.color[currentMonthIndex].headerTextColor
                    )
                    val headerTextKonfig = calendarHeaderTextConfig ?: defaultHeaderColor
                    CalendarMonth(
                        calendarDates = dates.toCalendarDates(),
                        month = calendarModel.month,
                        year = calendarModel.year,
                        selectedRange = selectedRange.value,
                        contentPadding = monthContentPadding,
                        dayContent = dayContent,
                        calendarDayConfig = calendarDayConfig,
                        onDayClick = { clickedDate, event ->
                            onDayClicked(
                                clickedDate,
                                event,
                                daySelectionMode,
                                selectedRange,
                                onRangeSelected = { range, events ->
                                    if (range.end < range.start) {
                                        onErrorRangeSelected(RangeSelectionError.EndIsBeforeStart)
                                    } else {
                                        onRangeSelected(range, events)
                                    }
                                },
                                onDayClick = { newDate, clickedDateEvent ->
                                    onDayClick(newDate, clickedDateEvent)
                                }
                            )
                        },
                        events = events,
                        calendarHeaderTextConfig = headerTextKonfig,
                        headerContent = headerContent,
                        calendarColor = calendarColors.color[currentMonthIndex],
                    )
                }
            }
        }
    )
}

/**
 * Displays the sticker header for the Calendar with the specified color and text size.
 *
 * @param color The color to be used for the sticker header.
 * @param textSize The text size to be used for the sticker header.
 */
@Composable
private fun CalendarStickerHeader(color: Color, textSize: TextUnit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            repeat(WeekDays.size) {
                Text(
                    modifier = Modifier
                        .weight(1F),
                    color = color,
                    fontSize = textSize,
                    text = WeekDays[it],
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Represents a collection of dates for each day in a Calendar month.
 *
 * @property dates The list of lists containing the dates for each day in a month. Each inner list represents a week
 * and contains nullable LocalDate values for each day of the week.
 */
@Immutable
internal data class CalendarDates(val dates: List<List<LocalDate?>>)

/**
 * Converts a list of lists containing nullable LocalDate values to a [CalendarDates] object.
 *
 * @receiver The source list of lists.
 * @return The converted [CalendarDates] object.
 */
internal fun List<List<LocalDate?>>.toCalendarDates() = CalendarDates(this)
