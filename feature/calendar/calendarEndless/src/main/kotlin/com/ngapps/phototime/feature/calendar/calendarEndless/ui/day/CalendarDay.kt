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

package com.ngapps.phototime.feature.calendar.calendarEndless.ui.day

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.ngapps.phototime.feature.calendar.calendarEndless.daterange.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvent
import com.ngapps.phototime.feature.calendar.calendarEndless.model.CalendarEvents
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.CalendarIndicator
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.color.CalendarColor
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.modifier.circleLayout
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.modifier.dayBackgroundColor
import com.ngapps.phototime.feature.calendar.calendarEndless.util.MultiplePreviews
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * Composable representing a single day in the Calendar.
 *
 * @param date The date of the day.
 * @param calendarColor The color configuration for the Calendar.
 * @param selectedRange The selected date range in the Calendar.
 * @param onDayClick The callback triggered when the day is clicked.
 * @param modifier The modifier for styling theCalendarDay composable.
 * @param selectedDate The currently selected date.
 * @param events The list of Calendar events.
 * @param calendarDayConfig The configuration for the Calendar day.
 */
@Composable
fun CalendarDay(
    date: LocalDate,
    calendarColor: CalendarColor,
    selectedRange: CalendarSelectedDayRange?,
    onDayClick: (LocalDate, List<CalendarEvent>) -> Unit,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate = date,
    events: CalendarEvents = CalendarEvents(),
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
) {
    val selected = selectedDate == date
    val currentDay = Clock.System.todayIn(TimeZone.currentSystemDefault()) == date
    Column(
        modifier = modifier
            .border(
                border = getBorder(currentDay, calendarDayConfig.borderColor, selected),
                shape = CircleShape
            )
            .clip(shape = CircleShape)
            .clickable { onDayClick(date, events.events) }
            .dayBackgroundColor(
                selected,
                calendarColor.dayBackgroundColor,
                date,
                selectedRange
            )
            .circleLayout()
            .wrapContentSize()
            .defaultMinSize(calendarDayConfig.size),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier.wrapContentSize(),
            textAlign = TextAlign.Center,
            fontSize = calendarDayConfig.textSize,
            color = if (selected) calendarDayConfig.selectedTextColor else calendarDayConfig.textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
        )
        Row {
            events.events
                .filter { it.date == date }
                .take(3)
                .fastForEachIndexed { index, _ ->
                    CalendarIndicator(
                        modifier = Modifier,
                        index = index,
                        size = calendarDayConfig.size,
                        color = calendarColor.headerTextColor
                    )
                }
        }
    }
}

/**
 * Returns the BorderStroke based on the current day, color, and selected state.
 *
 * @param currentDay Whether the day is the current day.
 * @param color The color of the border.
 * @param selected Whether the day is selected.
 * @return The BorderStroke for the day.
 */
private fun getBorder(currentDay: Boolean, color: Color, selected: Boolean): BorderStroke {
    val emptyBorder = BorderStroke(0.dp, Color.Transparent)
    return if (currentDay && selected.not()) {
        BorderStroke(1.dp, color)
    } else {
        emptyBorder
    }
}

@Composable
@MultiplePreviews
private fun CalendarDayPreview() {
    val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val previous = Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(1, DateTimeUnit.DAY)
    val events = (0..5).map {
        CalendarEvent(
            date = date,
            eventName = it.toString(),
        )
    }
    Row {
        CalendarDay(
            date = date,
            calendarColor = CalendarColor.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            events = CalendarEvents(events),
            selectedRange = null
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        CalendarDay(
            date = date.plus(1, DateTimeUnit.DAY),
            calendarColor = CalendarColor.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            events = CalendarEvents(events),
            selectedRange = null
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        CalendarDay(
            date = date,
            calendarColor = CalendarColor.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            events = CalendarEvents(events),
            selectedRange = null
        )
    }
}
