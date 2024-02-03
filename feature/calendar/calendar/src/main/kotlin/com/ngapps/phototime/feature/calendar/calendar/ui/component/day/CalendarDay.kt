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

package com.ngapps.phototime.feature.calendar.calendar.ui.component.day

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.ngapps.phototime.core.converters.isoDateToLocalDate
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.ui.tasks.TaskResourcePreviewParameterProvider
import com.ngapps.phototime.feature.calendar.calendar.CalendarTasks
import com.ngapps.phototime.feature.calendar.calendar.R
import com.ngapps.phototime.feature.calendar.calendar.color.CalendarColor
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.modifier.circleLayout
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.modifier.dayBackgroundColor
import com.ngapps.phototime.feature.calendar.calendar.ui.component.indicator.CalendarIndicator
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.CalendarSelectedDayRange
import com.ngapps.phototime.feature.calendar.calendar.util.MultiplePreviews
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

/**
 * A composable representing a single day in the Calendar.
 *
 * @param date The date corresponding to the day.
 * @param calendarColors The colors used for styling the Calendar.
 * @param onDayClick The callback function invoked when the day is clicked.
 * @param selectedRange The selected date range in the Calendar.
 * @param modifier The modifier to be applied to the composable.
 * @param selectedDate The currently selected date.
 * @param calendarTasks The events associated with the Calendar.
 * @param calendarDayConfig The configuration for the Calendar day.
 */
@Composable
fun CalendarDay(
    date: LocalDate,
    calendarColors: CalendarColor,
    onDayClick: (LocalDate, List<TaskResource>) -> Unit,
    selectedRange: CalendarSelectedDayRange?,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate = date,
    calendarTasks: CalendarTasks = CalendarTasks(),
    calendarDayConfig: CalendarDayConfig = CalendarDayConfig.default(),
) {
    val selected = selectedDate == date
    val currentDay = Clock.System.todayIn(TimeZone.currentSystemDefault()) == date
    val hasShooting =
        calendarTasks.calendarShoots.any { isoDateToLocalDate(it.scheduledTime.start) == date }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
    ) {
        Column(
            modifier = modifier
                .defaultMinSize(
                    minWidth = calendarDayConfig.size,
                    minHeight = calendarDayConfig.size,
                )
                .fillMaxSize()
                .clip(shape = CircleShape)
                .clickable { onDayClick(date, calendarTasks.calendarTasks) },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .dayBackgroundColor(
                            currentDay,
                            calendarColors.dayBackgroundColor,
                            date,
                            selectedRange,
                        )
                        .border(
                            border = getBorder(calendarDayConfig.borderColor, selected),
                            shape = CircleShape,
                        )
                        .circleLayout()
                        .wrapContentSize(),
                ) {
                    if (hasShooting) {
                        Icon(
                            painter = painterResource(id = PtIcons.ShootNew),
                            contentDescription = stringResource(R.string.shooting),
                            tint = calendarColors.dayBackgroundColor,
                        )
                    } else {
                        Box(modifier = Modifier.size(28.dp))
                    }
                }
                Text(
                    text = date.dayOfMonth.toString(),
                    modifier = Modifier.wrapContentSize(),
                    textAlign = TextAlign.Center,
                    fontSize = calendarDayConfig.textSize,
                    color = if (currentDay) calendarDayConfig.currentDayTextColor else calendarDayConfig.textColor,
                    fontWeight = if (currentDay) FontWeight.Black else FontWeight.Normal,
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                calendarTasks.calendarTasks
                    .filter { isoDateToLocalDate(it.scheduledTime.start) == date }
                    .take(3)
                    .fastForEachIndexed { index, _ ->
                        Row {
                            CalendarIndicator(
                                modifier = Modifier,
                                index = index,
                                size = calendarDayConfig.size,
                                color = calendarColors.headerIconColor,
                            )
                        }
                    }
            }
        }
    }
}

/**
 * Returns the border stroke based on the current day, color, and selected state.
 *
 * @param color The color of the border.
 * @param selected Whether the day is selected.
 *
 * @return The border stroke to be applied.
 */
private fun getBorder(color: Color, selected: Boolean): BorderStroke {
    val emptyBorder = BorderStroke(0.dp, Color.Transparent)
    return if (selected) {
        BorderStroke(1.dp, color)
    } else {
        emptyBorder
    }
}

@MultiplePreviews
@Composable
private fun CalendarDayPreview(
    @PreviewParameter(TaskResourcePreviewParameterProvider::class)
    taskResources: List<TaskResource>,
) {
    val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val previous =
        Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(1, DateTimeUnit.DAY)

    Row {
        CalendarDay(
            date = date,
            calendarColors = CalendarColor.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            calendarTasks = CalendarTasks(taskResources),
            selectedRange = null,
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        CalendarDay(
            date = date.plus(1, DateTimeUnit.DAY),
            calendarColors = CalendarColor.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            calendarTasks = CalendarTasks(taskResources),
            selectedRange = null,
        )
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        CalendarDay(
            date = date,
            calendarColors = CalendarColor.previewDefault(),
            onDayClick = { _, _ -> },
            selectedDate = previous,
            calendarTasks = CalendarTasks(taskResources),
            selectedRange = null,
        )
    }
}
