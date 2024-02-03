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

package com.ngapps.phototime.feature.calendar.calendar.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

/**
 * @return list of [@param count] next dates
 */
internal fun LocalDate.getNextDates(count: Int): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    repeat(count) { day ->
        dates.add(this.plusDays((day).toLong()))
    }
    return dates
}

/**
 * @return week start date - default monday
 */
internal fun LocalDate.getWeekStartDate(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
    var date = this
    while (date.dayOfWeek != weekStartDay) {
        date = date.minusDays(1)
    }
    return date
}

/**
 * @return first date of the month
 */
internal fun LocalDate.getMonthStartDate(): LocalDate {
    return LocalDate.of(this.year, this.month, 1)
}

/**
 * @return list of dates remaining in the week
 */
internal fun LocalDate.getRemainingDatesInWeek(weekStartDay: DayOfWeek = DayOfWeek.MONDAY): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var date = this.plusDays(1)
    while (date.dayOfWeek != weekStartDay) {
        dates.add(date)
        date = date.plusDays(1)
    }
    return dates
}

/**
 * @return list of dates remaining in the month
 */
internal fun LocalDate.getRemainingDatesInMonth(): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    repeat(this.month.length(this.isLeapYear) - this.dayOfMonth + 1) {
        dates.add(this.plusDays(it.toLong()))
    }
    return dates
}

/**
 * @return YearMonth object of given date
 */
internal fun LocalDate.yearMonth(): YearMonth = YearMonth.of(this.year, this.month)



