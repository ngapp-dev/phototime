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

package com.ngapps.phototime.feature.calendar.calendarEndless.ui.color

import androidx.compose.runtime.Immutable
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
private val backgroundColor = listOf(
    Color(0xffF7CFD3),
    Color(0xffEFBDCF),
    Color(0xffDBBFE4),
    Color(0xffCFC4E5),
    Color(0xffC6CAE6),
    Color(0xffC1DEF9),
    Color(0xffBDE3F9),
    Color(0xffBEE8F1),
    Color(0xffBBDEDB),
    Color(0xffCEE5CB),
    Color(0xffDEEBCB),
    Color(0xffF1F4C8),
)

@Stable
@SuppressWarnings("MagicNumber")
private val calendarBackgroundColor = listOf(
    Color.White,
    Color(0xFFFCEFFE),
    Color(0xFFFDF2FE),
    Color(0xFFFEF7FE),
    Color(0xFFF9FDFE),
    Color(0xFFF1FEFF),
    Color(0xFFEBFEFF),
    Color(0xFFE9FEFF),
    Color(0xFFEBFEFF),
    Color(0xFFFCFFFC),
    Color(0xFFFFFFFB),
    Color(0xFFFFFFF7),
)

@Stable
@SuppressWarnings("MagicNumber")
private val headerColors = listOf(
    Color(0xFFC39EA1),
    Color(0xFFBB8D9E),
    Color(0xFFAA8FB1),
    Color(0xFF9E94B4),
    Color(0xFF9599B4),
    Color(0xFF91ABC5),
    Color(0xFF8CB2C6),
    Color(0xFF8CB7BE),
    Color(0xFF8BACA9),
    Color(0xFF9DB39A),
    Color(0xFFADBA9A),
    Color(0xFFBEC196),
)

/**
 * A stable representation of a specific color scheme for Calendar.
 *
 * @property backgroundColor The background color.
 * @property dayBackgroundColor The color for day backgrounds.
 * @property headerTextColor The color for header text.
 */
@Immutable
data class CalendarColor(
    val backgroundColor: Color,
    val dayBackgroundColor: Color,
    val headerTextColor: Color,
) {
    companion object {

        internal fun previewDefault() = CalendarColor(
            calendarBackgroundColor.first(), backgroundColor.first(), headerColors.first()
        )
    }
}

private const val TOTAL_MONTH = 12

/**
 * A collection of predefined color schemes for Calendar.
 *
 * @property color A list of [CalendarColor] instances.
 */
@Immutable
data class CalendarColors(
    val color: List<CalendarColor> = emptyList()
) {
    companion object {
        /**
         * Returns the default set of colors.
         *
         * @return The default [CalendarColors] instance.
         */
        fun default(): CalendarColors {
            val colors = List(TOTAL_MONTH) { index ->
                CalendarColor(
                    calendarBackgroundColor[index],
                    backgroundColor[index],
                    headerColors[index]
                )
            }
            return CalendarColors(colors)
        }
    }
}
