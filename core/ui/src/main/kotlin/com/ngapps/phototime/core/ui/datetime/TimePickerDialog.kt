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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.ngapps.phototime.core.converters.isoDateToTimePickerData
import com.ngapps.phototime.core.converters.updateIsoDateWithTime
import com.ngapps.phototime.core.designsystem.component.PtTextButton
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.ui.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitTimePickerDialog(
    showTimePicker: Boolean,
    scheduledDateTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (showTimePicker) {
        val (hour, minute) = isoDateToTimePickerData(scheduledDateTime)
        val state = rememberTimePickerState(initialHour = hour, initialMinute = minute)
        val showingPicker = remember { mutableStateOf(true) }
        val configuration = LocalConfiguration.current

        TimePickerDialog(
            title = if (showingPicker.value) {
                stringResource(R.string.select_time)
            } else {
                stringResource(R.string.enter_time)
            },
            onCancel = { onDismiss() },
            onConfirm = {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, state.hour)
                cal.set(Calendar.MINUTE, state.minute)
                cal.isLenient = false

                onConfirm(updateIsoDateWithTime(scheduledDateTime, cal))
                onDismiss()
            },
            toggle = {
                if (configuration.screenHeightDp > 400) {
                    // Make this take the entire viewport. This will guarantee that Screen readers
                    // focus the toggle first.
                    Box(
                        Modifier
                            .fillMaxSize()
                            .semantics {
                                isTraversalGroup = true
                            },
                    ) {
                        IconButton(
                            modifier = Modifier
                                // This is a workaround so that the Icon comes up first
                                // in the talkback traversal order. So that users of a11y
                                // services can use the text input. When talkback traversal
                                // order is customizable we can remove this.
                                .size(64.dp, 72.dp)
                                .align(Alignment.BottomStart)
                                .zIndex(5f),
                            onClick = { showingPicker.value = !showingPicker.value },
                        ) {
                            val icon = if (showingPicker.value) {
                                PtIcons.Keyboard
                            } else {
                                PtIcons.Schedule
                            }
                            Icon(
                                icon,
                                contentDescription = if (showingPicker.value) {
                                    stringResource(R.string.switch_to_text_input)
                                } else {
                                    stringResource(R.string.switch_to_touch_input)
                                },
                            )
                        }
                    }
                }
            },
        ) {
            if (showingPicker.value && configuration.screenHeightDp > 400) {
                TimePicker(state = state)
            } else {
                TimeInput(state = state)
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                ),
        ) {
            toggle()
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    PtTextButton(
                        onClick = onCancel,
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    PtTextButton(
                        onClick = onConfirm,
                    ) {
                        Text(
                            text = stringResource(id = R.string.confirm),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}