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

package com.ngapps.phototime.feature.calendar.calendar

import androidx.lifecycle.ViewModel
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.feature.calendar.calendar.ui.collapsed.util.getNext7Dates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class CalendarViewModel : ViewModel() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    private val _calendarExpanded = MutableStateFlow(false)
    val calendarExpanded: StateFlow<Boolean> = _calendarExpanded

    private val _weekValue = MutableStateFlow(today.getNext7Dates())
    val weekValue: StateFlow<List<LocalDate>> = _weekValue

    private val _displayedMonth = MutableStateFlow(today.month)
    val displayedMonth: StateFlow<Month> = _displayedMonth

    private val _displayedYear = MutableStateFlow(today.year)
    val displayedYear: StateFlow<Int> = _displayedYear

    private val _selectedDate = MutableStateFlow(today)
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    fun onChangeWeekValue(list: List<LocalDate>) {
        _weekValue.value = list
        _displayedMonth.value = list.first().month
        _displayedYear.value = list.first().year
    }

    fun onChangeDisplayedMonth(month: Month) {
        _displayedMonth.value = month
    }

    fun onChangeDisplayedYear(year: Int) {
        _displayedYear.value = year
    }

    fun onChangeCalendarExpanded(isExpanded: Boolean) {
        _calendarExpanded.value = isExpanded
    }

    fun onChangeSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }
}


sealed interface CalendarUiState {
    data class Success(
        val calendarTasks: List<TaskResource>,
        val calendarShoots: List<ShootResource>
    ) : CalendarUiState

    data object Error : CalendarUiState
    data object Loading : CalendarUiState
}
