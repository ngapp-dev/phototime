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

package com.ngapps.phototime.core.ui.shoots

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.ui.R
import com.ngapps.phototime.core.ui.datetime.TimeZoneBroadcastReceiver
import com.ngapps.phototime.core.ui.tasks.TaskResourceCard
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * [ShootResource] card used on the following screens: Tasks/Calendar
 */

fun LazyGridScope.shootFeed(
    shoots: List<ShootResource>,
    shootTasks: List<TaskResource>,
    onTaskResourcesCompleteChanged: (String, Boolean) -> Unit,
    onTaskClick: (String) -> Unit,
    onShootClick: (String) -> Unit,
) {
    shoots.forEach { shoot ->
        item {
            val cornerShape = if (shootTasks.isNotEmpty()) {
                RoundedCornerShape(10.dp, 10.dp, 0.dp, 0.dp)
            } else {
                MaterialTheme.shapes.medium
            }
            ShootResourceCard(
                shootResource = shoot,
                onClick = { onShootClick(shoot.id) },
                cornerShape = cornerShape,
                modifier = Modifier,
            )
        }
        itemsIndexed(shootTasks) { index, task ->
            val cornerShape = if (index == shootTasks.size - 1) {
                RoundedCornerShape(0.dp, 0.dp, 10.dp, 10.dp)
            } else {
                MaterialTheme.shapes.extraSmall
            }

            TaskResourceCard(
                taskResource = task,
                isCompleted = false,
                onToggleComplete = { },
                onClick = { onTaskClick(task.id) },
                cornerShape = cornerShape,
                modifier = Modifier,
            )
        }
        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShootResourceCard(
    shootResource: ShootResource,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isTimeShown: Boolean = true,
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
            .semantics { onClick(label = clickActionLabel, action = null) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = PtIcons.Shoot),
                contentDescription = "Shoot",
                modifier = Modifier.size(24.dp),
            )
            ShootResourceTitleNew(
                shootResourceTitle = shootResource.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
            )
            if (isTimeShown) {
                ShootResourceTimeNew(
                    shootResourceScheduledTime = shootResource.scheduledTime.start,
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
fun ShootResourceTitleNew(
    shootResourceTitle: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = shootResourceTitle,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun ShootCompleteDotNew(
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
fun ShootResourceTimeNew(
    shootResourceScheduledTime: String,
    modifier: Modifier = Modifier,
) {
    val formattedDate = shootDateFormatted(shootResourceScheduledTime)

    Text(
        formattedDate,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier,
    )
}


@Composable
fun shootDateFormatted(startScheduledTime: String): String {
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


@Preview("ShootResourceCardExpanded")
@Composable
private fun ExpandedShootResourcePreview(
    @PreviewParameter(ShootResourcePreviewParameterProvider::class)
    shootResources: List<ShootResource>,
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        PtTheme {
            Surface {
                ShootResourceCard(
                    shootResource = shootResources[0],
                    onClick = {},
                )
            }
        }
    }
}
