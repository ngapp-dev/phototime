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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.feature.calendar.calendarEndless.daterange.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvent
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvents
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.color.CalendarColors
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.header.CalendarTextConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import java.time.format.TextStyle
import java.util.Locale

private const val YEAR_LENGTH = 12
private const val MIN_MONTH = 12
const val WEEK_LENGTH = 7
const val MIN_DAY = 2

/**
 * Composable function that displays a week view calendar using Jetpack Compose.
 *
 * @param modifier The modifier for the root layout of the calendar.
 * @param daySelectionMode The mode for selecting days in the calendar.
 * @param currentDay The current day to be displayed. If null, the system's current date will be used.
 * @param showLabel Whether to show labels for each day of the week.
 * @param CalendarHeaderTextConfig The configuration for the header text in the calendar.
 * @param calendarColors The colors used in the calendar.
 * @param events The events to be displayed in the calendar.
 * @param labelFormat The format function for the labels of the days of the week.
 * @param calendarDayConfig The configuration for each day in the calendar.
 * @param dayContent The content to be displayed for each day in the calendar.
 * @param headerContent The content to be displayed in the header of the calendar.
 * @param onDayClick The callback function when a day is clicked in the calendar.
 * @param onRangeSelected The callback function when a range of days is selected in the calendar.
 * @param onErrorRangeSelected The callback function when an error occurs during range selection.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
@ExperimentalFoundationApi
fun CalendarSmall(
    modifier: Modifier = Modifier,
    daySelectionMode: DaySelectionMode = DaySelectionMode.Range,
    currentDay: LocalDate? = null,
    showLabel: Boolean = true,
    CalendarHeaderTextConfig: CalendarTextConfig? = null,
    calendarColors: CalendarColors = CalendarColors.default(),
    events: CalendarEvents = CalendarEvents(),
    labelFormat: (Month) -> String = {
        it.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault(),
        )
    },
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
    dayContent: @Composable ((LocalDate) -> Unit)? = null,
    headerContent: @Composable ((Month, Int) -> Unit)? = null,
    onDayClick: (LocalDate, List<CalendarEvent>) -> Unit = { _, _ -> },
    onRangeSelected: (CalendarSelectedDayRange, List<CalendarEvent>) -> Unit = { _, _ -> },
    onErrorRangeSelected: (RangeSelectionError) -> Unit = {}
) {

//    val today = currentDay ?: Clock.System.todayIn(TimeZone.currentSystemDefault())
//    val currentMonthIndex = today.month.value.minus(1)
//    val currentYear = today.year
//    val initialPage = remember { mutableStateOf(Int.MAX_VALUE / 2) }
//    val pagerState = rememberPagerState(
//        initialPage = initialPage.value,
//    ) {
//        Int.MAX_VALUE
//    }
//    val months = Month.values().toList()
//    HorizontalPager(
//        state = pagerState,
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(vertical = 16.dp),
//    ) { pageIndex ->
//        val monthIndex = pageIndex.minus(initialPage.value)
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Text(
//                text = months[pageIndex].name,
//                modifier = Modifier.padding(16.dp),
//            )
//            Text(
//                text = currentYear.toString(),
//            )
//        }

    val today = currentDay ?: Clock.System.todayIn(TimeZone.currentSystemDefault())
    val initialPage = remember { mutableStateOf(Int.MAX_VALUE / 2) }
    val yearMonths = remember { mutableStateOf(generateMonths(0)) }
    val currentMonthIndex = today.month.value.minus(1)
    val pagerState = rememberPagerState(initialPage = initialPage.value) { Int.MAX_VALUE }

    Column(
        modifier = modifier
            .background(color = calendarColors.color[currentMonthIndex].backgroundColor)
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(all = 8.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) { pageIndex ->
            val monthIndex = pageIndex.minus(initialPage.value)
            val months = generateMonths(monthIndex)
            yearMonths.value = months

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (dayContent != null) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            modifier = Modifier,
                            color = calendarDayConfig.textColor,
                            fontSize = calendarDayConfig.textSize,
                            text = months.toString(),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            modifier = Modifier,
                            color = calendarDayConfig.textColor,
                            fontSize = calendarDayConfig.textSize,
                            text = monthIndex.toString(),
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
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
