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

package com.ngapps.phototime.feature.calendar.calendar.color

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

/**
 * The `color` package provides color-related classes and utilities for the Calendar library.
 *
 * It includes predefined color schemes for backgrounds, day backgrounds, and header text colors.
 *
 * @see CalendarColor
 * @see CalendarColors
 */


@Stable
@SuppressWarnings("MagicNumber")
private val headerBackgroundColor = Color(0xFFFCEFFE)

@Stable
@SuppressWarnings("MagicNumber")
private val calendarBackgroundColor = Color(0xFFFCEFFE)

@Stable
@SuppressWarnings("MagicNumber")
private val headerTextColor = Color(0xFF000000)

@Stable
@SuppressWarnings("MagicNumber")
private val calendarTextColor = Color(0xFF000000)

@Stable
@SuppressWarnings("MagicNumber")
private val dayBackgroundColor = Color(0xffF1F4C8)


/**
 * A stable representation of a specific color scheme for Calendar.
 *
 * @property headerBackgroundColor The background color.
 * @property calendarBackgroundColor The background color.
 * @property headerIconColor The color for header text.
 * @property calendarTextColor The color for header text.
 * @property dayBackgroundColor The color for day backgrounds.
 */
data class CalendarColor(
    val headerBackgroundColor: Color,
    val calendarBackgroundColor: Color,
    val headerIconColor: Color,
    val calendarTextColor: Color,
    val dayBackgroundColor: Color,
) {
    companion object {

        internal fun previewDefault() = CalendarColor(
            headerBackgroundColor,
            calendarBackgroundColor,
            headerTextColor,
            calendarTextColor,
            dayBackgroundColor,
        )
    }
}

/**
 * A collection of predefined color schemes for Сalendar.
 *
 * @property color A list of [CalendarColor] instances.
 */
data class CalendarColors(
    val color: CalendarColor
) {
    companion object {
        /**
         * Returns the default set of colors.
         *
         * @return The default [CalendarColors] instance.
         */
        fun default(): CalendarColors {
            val colors =
                CalendarColor(
                    headerBackgroundColor,
                    calendarBackgroundColor,
                    headerTextColor,
                    calendarTextColor,
                    dayBackgroundColor,
                )

            return CalendarColors(colors)
        }
    }
}
