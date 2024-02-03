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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun ScheduledTimeCard(
    scheduledTime: String,
    modifier: Modifier = Modifier,
    annotation: String? = "",
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
) {

    val formattedTime = hourMinTimeFormatted(scheduledTime)
    val formattedDate = dateFormatted(scheduledTime)

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Text(
                text = annotation + formattedDate,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
                    .clickable {
                        onDateClick()
                    },
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        onTimeClick()
                    },
            ) {
                Text(
                    formattedTime,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
fun ScheduledNotificationCard(
    scheduledTime: String,
    modifier: Modifier = Modifier,
    annotation: String? = "",
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {},
) {


    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Text(
                text = annotation + scheduledTime,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .weight(1f)
                    .clickable {
                        onDateClick()
                    },
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        onTimeClick()
                    },
            ) {

            }
        }
    }
}

@Composable
fun hourMinTimeFormatted(startScheduledTime: String): String {
    var zoneId by remember { mutableStateOf(ZoneId.systemDefault()) }

    val context = LocalContext.current

    DisposableEffect(context) {
        val receiver = TimeZoneBroadcastReceiver(
            onTimeZoneChanged = { zoneId = ZoneId.systemDefault() },
        )
        receiver.register(context)
        onDispose {
            receiver.unregister(context)
        }
    }

    return if (startScheduledTime.isNotEmpty()) {
        DateTimeFormatter.ofPattern("HH:mm").withZone(zoneId)
            .format(startScheduledTime.toInstant().toJavaInstant())
    } else {
        "2022-03-20T16:58:00.288Z"
    }

}

@Composable
fun dateFormatted(dateString: String): String {
    return if (dateString.isNotEmpty()) {
        val zonedDateTime = ZonedDateTime.parse(dateString)

        val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM", Locale.getDefault())
        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())

        "${dateFormatter.format(zonedDateTime.toLocalDate())}, ${
            dayOfWeekFormatter.format(
                zonedDateTime.toLocalDate().dayOfWeek,
            )
        }"
    } else {
        ""
    }
}