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

package com.ngapps.phototime.core.converters

import kotlinx.datetime.toKotlinLocalDate
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

fun isoDateToLocalDate(scheduledTime: String): kotlinx.datetime.LocalDate {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val zonedDateTime = ZonedDateTime.parse(scheduledTime, formatter)
    return zonedDateTime.toLocalDate().toKotlinLocalDate()
}

fun isoDateToMillis(scheduledTime: String): Long {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val zonedDateTime = ZonedDateTime.parse(scheduledTime, formatter)
    return zonedDateTime.toInstant().toEpochMilli()
}

fun millisToIsoDate(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.systemDefault())
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    return zonedDateTime.format(formatter)
}

fun isoDateToTimePickerData(scheduledTime: String): Pair<Int, Int> {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.parse(scheduledTime, formatter).withZoneSameInstant(zoneId)
    val hour = zonedDateTime.hour
    val minute = zonedDateTime.minute
    return Pair(hour, minute)
}

fun updateIsoDateWithTime(isoDate: String, time: Calendar): String {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val originalDateTime = ZonedDateTime.parse(isoDate, formatter)
    val updatedDateTime = ZonedDateTime.of(
        originalDateTime.toLocalDate(),
        LocalTime.of(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)),
        ZoneId.systemDefault(),
    )
    return updatedDateTime.format(formatter)
}
