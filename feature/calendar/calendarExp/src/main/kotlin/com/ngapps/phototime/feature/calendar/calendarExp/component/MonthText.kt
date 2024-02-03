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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ngapps.phototime.feature.calendar.calendarExp.core.CalendarTheme
import java.text.DateFormatSymbols
import java.time.Month
import java.time.YearMonth
import java.util.Locale

@Composable
fun MonthText(selectedMonth: YearMonth, theme: CalendarTheme, modifier: Modifier = Modifier) {
    val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Locale.getDefault(Locale.Category.FORMAT)
    } else {
        Locale.getDefault()
    }
    val displayName = getMonthName(selectedMonth.month, currentLocale) + " " + selectedMonth.year

    Text(
        displayName,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = theme.headerTextColor,
        modifier = modifier,
    )
}

private fun getMonthName(month: Month, locale: Locale): String {
    val dateFormatSymbols = DateFormatSymbols.getInstance(locale)
    val months = dateFormatSymbols.months
    return months[month.value - 1]
}