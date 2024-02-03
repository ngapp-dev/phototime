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

package com.ngapps.phototime.core.ui.datetime

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ngapps.phototime.core.converters.isoDateToMillis
import com.ngapps.phototime.core.designsystem.component.PtTextButton
import com.ngapps.phototime.core.ui.R
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitDatePickerDialog(
    showDatePicker: Boolean,
    scheduledDateTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    if (showDatePicker) {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val zonedDateTime = ZonedDateTime.parse(scheduledDateTime, formatter)
        val timeOfDay = zonedDateTime.toLocalTime()


        val datePickerState =
            rememberDatePickerState(initialSelectedDateMillis = isoDateToMillis(scheduledDateTime))

        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            onDismissRequest = { onDismiss() },
            confirmButton = {
                PtTextButton(
                    onClick = {
                        onDismiss()
                        val selectedDateMillis = datePickerState.selectedDateMillis!!
                        val selectedDate = Instant.ofEpochMilli(selectedDateMillis)
                        val selectedZonedDateTime = ZonedDateTime.ofInstant(selectedDate, ZoneOffset.systemDefault())
                            .with(timeOfDay)
                        onConfirm(formatter.format(selectedZonedDateTime))
                    },
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                PtTextButton(
                    onClick = { onDismiss() },
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    headlineContentColor = MaterialTheme.colorScheme.onSurface,
                    todayContentColor = MaterialTheme.colorScheme.onSurface,
                    todayDateBorderColor = MaterialTheme.colorScheme.onSurface,
                    selectedDayContentColor = MaterialTheme.colorScheme.onSurface,
                    selectedDayContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f,
                    ),
                    currentYearContentColor = MaterialTheme.colorScheme.onSurface,
                    selectedYearContentColor = MaterialTheme.colorScheme.onSurface,
                    selectedYearContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f,
                    ),
                ),
            )
        }
    }
}