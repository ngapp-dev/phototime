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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.model.data.task.UserTaskResource
import com.ngapps.phototime.core.ui.R
import com.ngapps.phototime.core.ui.datetime.hourMinTimeFormatted
import com.ngapps.phototime.core.ui.swipe_dismiss.PtSwipeToDismiss

/**
 * [TaskResource] card used on the following screens: For You, Saved
 */
fun LazyGridScope.taskFeed(
    tasks: List<TaskResource>,
    onTaskClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    onDeleteActionClick: (String) -> Unit,
) {
    items(tasks) { task ->
        PtSwipeToDismiss(
            onEditActionClick = { onEditActionClick(task.id) },
            onDeleteActionClick = { onDeleteActionClick(task.id) },
            modifier = Modifier.padding(vertical = 6.dp),
        ) {
            TaskResourceCard(
                taskResource = task,
                isCompleted = false,
                onToggleComplete = {

                },
                onClick = {
                    onTaskClick(task.id)
                },
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskResourceCard(
    taskResource: TaskResource,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerShape: Shape = MaterialTheme.shapes.medium,
) {
    val clickActionLabel = stringResource(R.string.card_tap_action)

    Card(
        onClick = onClick,
        shape = cornerShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        // NOTE: Use custom label for accessibility services to communicate button's action to user.
        //  Pass null for action to only override the label and not the actual action.
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                onClick(label = clickActionLabel, action = null)
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() },
            )
            TaskResourceTitle(
                taskResource.title,
                modifier = Modifier.weight(1f),
            )
            TaskResourceTime(
                taskResource.scheduledTime.start,
                modifier = Modifier,
            )
            TaskCompleteDot(
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(12.dp),
            )
        }
    }
}

@Composable
fun TaskResourceTitle(
    taskResourceTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = taskResourceTitle,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun TaskCompleteDot(
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
fun TaskResourceTime(
    taskResourceScheduledTime: String,
    modifier: Modifier = Modifier,
) {
    val formattedTime = hourMinTimeFormatted(taskResourceScheduledTime)

    Text(
        formattedTime,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier,
    )
}

@Preview("TaskResourceCardExpanded")
@Composable
private fun ExpandedTaskResourcePreview(
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