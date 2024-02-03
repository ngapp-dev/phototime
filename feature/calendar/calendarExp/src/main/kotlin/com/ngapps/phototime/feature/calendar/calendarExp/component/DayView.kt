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

package com.ngapps.phototime.feature.calendar.calendarExp.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngapps.phototime.feature.calendar.calendarExp.core.CalendarTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle

/**
 * View that represent one day in the calendar
 * @param date date that view should represent
 * @param weekDayLabel flag that indicates if name of week day should be visible above day value
 * @param modifier view modifier
 */
@Composable
fun DayView(
    date: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    theme: CalendarTheme,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    weekDayLabel: Boolean = true
) {
    val context = LocalContext.current
    val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        context.resources.configuration.locale
    }

    val isCurrentDay = date == LocalDate.now()
    val dayValueModifier =
        if (isCurrentDay) modifier.background(
            theme.selectedDayBackgroundColor.copy(alpha = 0.5f),
            shape = theme.dayShape
        )
        else if (isSelected) modifier.background(
            theme.selectedDayBackgroundColor,
            shape = theme.dayShape
        )
        else modifier.background(theme.dayBackgroundColor, shape = theme.dayShape)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .heightIn(max = if (weekDayLabel) 50.dp + 20.dp else 50.dp)
            .widthIn(max = 50.dp)
            .testTag("day_view_column")
    ) {
        if (weekDayLabel) {
            Text(
                DayOfWeek.values()[date.dayOfWeek.value - 1].getDisplayName(
                    TextStyle.SHORT,
                    currentLocale
                ),
                fontSize = 10.sp,
                color = theme.weekDaysTextColor
            )
        }
        Box(
            dayValueModifier
                .padding(5.dp)
                .aspectRatio(1f)
                .clickable { onDayClick(date) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                date.dayOfMonth.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = if (isSelected || isCurrentDay) theme.selectedDayValueTextColor else theme.dayValueTextColor
            )
        }
    }
}