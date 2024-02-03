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

package com.ngapps.phototime.feature.calendar.calendarExp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.feature.calendar.calendarExp.core.CalendarExpIntent
import com.ngapps.phototime.feature.calendar.calendarExp.core.Period
import com.ngapps.phototime.feature.calendar.calendarExp.utils.getNextDates
import com.ngapps.phototime.feature.calendar.calendarExp.utils.getRemainingDatesInMonth
import com.ngapps.phototime.feature.calendar.calendarExp.utils.getRemainingDatesInWeek
import com.ngapps.phototime.feature.calendar.calendarExp.utils.getWeekStartDate
import com.ngapps.phototime.feature.calendar.calendarExp.utils.yearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarExpViewModel : ViewModel() {
    private val _visibleDates =
        MutableStateFlow(
            calculateCollapsedCalendarDays(
                startDate = LocalDate.now().getWeekStartDate().minusWeeks(1)
            )
        )
    val visibleDates: StateFlow<Array<List<LocalDate>>> = _visibleDates

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    val currentMonth: StateFlow<YearMonth>
        get() = calendarExpanded.zip(visibleDates) { isExpanded, dates ->
            if (isExpanded) {
                dates[1][dates[1].size / 2].yearMonth()
            } else {
                if (dates[1].count { it.month == dates[1][0].month } > 3)
                    dates[1][0].yearMonth()
                else
                    dates[1][dates[1].size - 1].yearMonth()
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, LocalDate.now().yearMonth())


    private val _calendarExpanded = MutableStateFlow(false)
    val calendarExpanded: StateFlow<Boolean> = _calendarExpanded


    fun onIntent(intent: CalendarExpIntent) {
        when (intent) {
            CalendarExpIntent.ExpandCalendarExp -> {
                calculateCalendarDates(
                    startDate = currentMonth.value.minusMonths(1).atDay(1),
                    period = Period.MONTH
                )
                _calendarExpanded.value = true
            }

            CalendarExpIntent.CollapseCalendarExp -> {
                calculateCalendarDates(
                    startDate = calculateCollapsedCalendarVisibleStartDay()
                        .getWeekStartDate()
                        .minusWeeks(1),
                    period = Period.WEEK
                )
                _calendarExpanded.value = false
            }

            is CalendarExpIntent.LoadNextDates -> {
                calculateCalendarDates(intent.startDate, intent.period)
            }

            is CalendarExpIntent.SelectDate -> {
                viewModelScope.launch {
                    _selectedDate.emit(intent.date)
                }
            }
        }
    }

    private fun calculateCalendarDates(
        startDate: LocalDate,
        period: Period = Period.WEEK
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _visibleDates.emit(
                when (period) {
                    Period.WEEK -> calculateCollapsedCalendarDays(startDate)
                    Period.MONTH -> calculateExpandedCalendarDays(startDate)
                }
            )
        }
    }

    private fun calculateCollapsedCalendarVisibleStartDay(): LocalDate {
        val halfOfMonth = visibleDates.value[1][visibleDates.value[1].size / 2]
        val visibleMonth = YearMonth.of(halfOfMonth.year, halfOfMonth.month)
        return if (selectedDate.value.month == visibleMonth.month && selectedDate.value.year == visibleMonth.year)
            selectedDate.value
        else visibleMonth.atDay(1)
    }

    private fun calculateCollapsedCalendarDays(startDate: LocalDate): Array<List<LocalDate>> {
        val dates = startDate.getNextDates(21)
        return Array(3) {
            dates.slice(it * 7 until (it + 1) * 7)
        }
    }

    private fun calculateExpandedCalendarDays(startDate: LocalDate): Array<List<LocalDate>> {
        val array = Array(3) { monthIndex ->
            val monthFirstDate = startDate.plusMonths(monthIndex.toLong())
            val monthLastDate = monthFirstDate.plusMonths(1).minusDays(1)
            val weekBeginningDate = monthFirstDate.getWeekStartDate()
            if (weekBeginningDate != monthFirstDate) {
                weekBeginningDate.getRemainingDatesInMonth()
            } else {
                listOf()
            } +
                    monthFirstDate.getNextDates(monthFirstDate.month.length(monthFirstDate.isLeapYear)) +
                    monthLastDate.getRemainingDatesInWeek()
        }
        return array
    }
}
