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

package com.ngapps.phototime.feature.calendar.calendar.ui.component.day.modifier

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.CalendarSelectedDayRange
import kotlinx.datetime.LocalDate

private const val FULL_ALPHA = 1f
private const val TOWNED_DOWN_ALPHA = 0.4F

/**
 * A modifier that applies the background color to a composable based on the day's selection state,
 * selected range, and specified color.
 *
 * @param currentDay Whether the day is currentDay.
 * @param color The color to be applied as the background.
 * @param date The date corresponding to the day.
 * @param selectedRange The selected date range.
 *
 * @return The modified modifier.
 */
fun Modifier.dayBackgroundColor(
    currentDay: Boolean,
    color: Color,
    date: LocalDate,
    selectedRange: CalendarSelectedDayRange?
): Modifier {
    val inRange = date == selectedRange?.start || date == selectedRange?.end

    val backgroundColor = when {
        currentDay -> color
        selectedRange != null && date in selectedRange.start..selectedRange.end -> {
            val alpha = if (inRange) FULL_ALPHA else TOWNED_DOWN_ALPHA
            color.copy(alpha = alpha)
        }
        else -> Color.Transparent
    }

    return this.then(
        background(backgroundColor)
    )
}
