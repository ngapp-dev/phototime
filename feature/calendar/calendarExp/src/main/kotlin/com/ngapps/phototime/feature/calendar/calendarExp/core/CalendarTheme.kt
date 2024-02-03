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

package com.ngapps.phototime.feature.calendar.calendarExp.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape

data class CalendarTheme(
    val backgroundColor: Color,
    val headerBackgroundColor: Color,
    val dayBackgroundColor: Color,
    val selectedDayBackgroundColor: Color,
    val dayValueTextColor: Color,
    val selectedDayValueTextColor: Color,
    val headerTextColor: Color,
    val weekDaysTextColor: Color,
    val dayShape: Shape
)

val calendarDefaultTheme: CalendarTheme
    @Composable
    @ReadOnlyComposable
    get() = CalendarTheme(
        backgroundColor = Color.Transparent,
        headerBackgroundColor = Color.Transparent,
        dayBackgroundColor = Color.Transparent,
        selectedDayBackgroundColor = MaterialTheme.colorScheme.primary,
        dayValueTextColor = MaterialTheme.colorScheme.onBackground,
        selectedDayValueTextColor = MaterialTheme.colorScheme.onBackground,
        headerTextColor = MaterialTheme.colorScheme.onBackground,
        weekDaysTextColor = MaterialTheme.colorScheme.onBackground,
        dayShape = RectangleShape

    )
