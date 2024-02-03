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

package com.ngapps.phototime.core.ui.tasks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.task.UserTaskResource
import com.ngapps.phototime.core.ui.R
import com.ngapps.phototime.core.ui.datetime.TimeZoneBroadcastReceiver
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * [UserTaskResource] card used on the following screens: For You, Saved
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTaskResourceCard(
    userTaskResource: UserTaskResource,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickActionLabel = stringResource(R.string.card_tap_action)
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                alpha = SitUserTaskResourceCardDefaults.UserTaskResourceCardBackgroundAlpha,
            ),
        ),
        // NOTE: Use custom label for accessibility services to communicate button's action to user.
        //  Pass null for action to only override the label and not the actual action.
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                onClick(label = clickActionLabel, action = null)
            },
    ) {
        UserTaskResourceBox(
            userTaskResource = userTaskResource,
            isCompleted = isCompleted,
            onToggleComplete = onToggleComplete,
        )
    }
}

@Composable
fun UserTaskResourceBox(
    userTaskResource: UserTaskResource,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .padding(end = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() },
            )
            UserTaskResourceTitle(
                userTaskResource.title,
                modifier = Modifier.weight(1f),
            )
            UserTaskResourceTime(
                userTaskResource.scheduledTime.start,
                modifier = Modifier,
            )
            UserTaskCompleteDot(
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(12.dp),
            )
        }
    }
}

@Composable
fun UserTaskResourceTitle(
    userTaskResourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = userTaskResourceTitle,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun UserTaskCompleteDot(
    color: Color,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.unread_resource_dot_content_description)
    Canvas(
        modifier = modifier
            .semantics { contentDescription = description },
        onDraw = {
            drawCircle(
                color,
                radius = size.minDimension / 2,
            )
        },
    )
}


@Composable
fun UserTaskResourceTime(
    userTaskResourceScheduledTime: String,
    modifier: Modifier = Modifier,
) {
    val formattedDate = userTaskDateFormatted(userTaskResourceScheduledTime)

    Text(
        formattedDate,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier,
    )
}


@Composable
fun userTaskDateFormatted(startScheduledTime: String): String {
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

    return DateTimeFormatter.ofPattern("HH:mm")
        .withZone(zoneId).format(startScheduledTime.toInstant().toJavaInstant())
}


@Preview("TaskResourceCardExpanded")
@Composable
private fun ExpandedUserTaskResourcePreview(
    @PreviewParameter(TaskResourcePreviewParameterProvider::class)
    userTaskResources: List<UserTaskResource>,
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        PtTheme {
            Surface {
                UserTaskResourceCard(
                    userTaskResource = userTaskResources[0],
                    isCompleted = false,
                    onToggleComplete = {},
                    onClick = {},
                )
            }
        }
    }
}

object SitUserTaskResourceCardDefaults {
    const val UserTaskResourceCardBackgroundAlpha = 0.04f
}
