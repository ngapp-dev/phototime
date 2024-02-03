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

package com.ngapps.phototime.feature.tasks

import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import com.ngapps.phototime.core.ui.shoots.shootFeed
import com.ngapps.phototime.core.ui.tasks.taskFeed
import com.ngapps.phototime.feature.calendar.calendar.Calendar
import com.ngapps.phototime.feature.calendar.calendar.CalendarTasks
import com.ngapps.phototime.feature.calendar.calendar.CalendarUiState
import com.ngapps.phototime.feature.calendar.calendar.color.CalendarColor
import com.ngapps.phototime.feature.calendar.calendar.color.CalendarColors
import com.ngapps.phototime.feature.calendar.calendar.ui.component.day.CalendarDayConfig
import com.ngapps.phototime.feature.calendar.calendar.ui.component.header.CalendarTextConfig
import com.ngapps.phototime.feature.calendar.calendar.ui.expanded.DaySelectionMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.LocalDate

@Composable
internal fun CalendarRoute(
    onTaskClick: (String) -> Unit,
    onShootClick: (String) -> Unit,
    onEditShootActionClick: (String) -> Unit,
    onEditTaskActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val tasksUiState by viewModel.tasksUiState.collectAsStateWithLifecycle()
    val calendarUiState by viewModel.calendarUiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val deepLinkedUserTaskResource by viewModel.deepLinkedTaskResource.collectAsStateWithLifecycle()
    val selectedDay = viewModel.selectedDay.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is CalendarViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
            }
        }
    }

    CalendarScreen(
        isSyncing = isSyncing,
        selectedDay = selectedDay,
        calendarUiState = calendarUiState,
        tasksUiState = tasksUiState,
        deepLinkedUserTaskResource = deepLinkedUserTaskResource,
        onTaskDeepLinkOpened = viewModel::onTaskDeepLinkOpened,
        onTaskClick = onTaskClick,
        onShootClick = onShootClick,
        onEditShootActionClick = onEditShootActionClick,
        onEditTaskActionClick = onEditTaskActionClick,
        onDeleteShootActionClick = { viewModel.triggerAction(CalendarAction.DeleteShoot(it)) },
        onDeleteTaskActionClick = { viewModel.triggerAction(CalendarAction.DeleteTask(it)) },
        onTaskResourceCompleteChanged = viewModel::updateTaskResourceCompleted,
        onDayClick = viewModel::updateDaySelection,
        modifier = modifier,
    )
}

@Composable
internal fun CalendarScreen(
    isSyncing: Boolean,
    selectedDay: LocalDate,
    calendarUiState: CalendarUiState,
    tasksUiState: TasksUiState,
    deepLinkedUserTaskResource: TaskResource?,
    onTaskClick: (String) -> Unit,
    onShootClick: (String) -> Unit,
    onEditShootActionClick: (String) -> Unit,
    onEditTaskActionClick: (String) -> Unit,
    onDeleteShootActionClick: (String) -> Unit,
    onDeleteTaskActionClick: (String) -> Unit,
    onTaskDeepLinkOpened: (String) -> Unit,
    onTaskResourceCompleteChanged: (String, Boolean) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isTasksUiLoading = tasksUiState is TasksUiState.Loading
    val isCalendarUiLoading = calendarUiState is CalendarUiState.Loading

    // NOTE: This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isTasksUiLoading && !isCalendarUiLoading }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape) {
        Row {
            CalendarItem(
                calendarUiState = calendarUiState,
                onDayClick = onDayClick,
                modifier = modifier.weight(0.5f),
            )
            TasksItem(
                selectedDay = selectedDay,
                tasksUiState = tasksUiState,
                onTaskClick = onTaskClick,
                onShootClick = onShootClick,
                onEditActionClick = onEditTaskActionClick,
                onDeleteActionClick = onDeleteTaskActionClick,
                onTaskResourceCompleteChanged = onTaskResourceCompleteChanged,
                modifier = modifier.weight(0.5f),
            )
        }
    } else {
        Column {
            CalendarItem(
                calendarUiState = calendarUiState,
                onDayClick = onDayClick,
                modifier = modifier,
            )
            TasksItem(
                selectedDay = selectedDay,
                tasksUiState = tasksUiState,
                onTaskClick = onTaskClick,
                onShootClick = onShootClick,
                onEditActionClick = onEditTaskActionClick,
                onDeleteActionClick = onDeleteTaskActionClick,
                onTaskResourceCompleteChanged = onTaskResourceCompleteChanged,
                modifier = modifier,
            )
        }
    }
    AnimatedVisibility(
        visible = isSyncing || isTasksUiLoading || isCalendarUiLoading,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
        ) + fadeOut(),
    ) {
        val loadingContentDescription = stringResource(id = R.string.loading)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            PtOverlayLoadingWheel(
                modifier = Modifier.align(Alignment.Center),
                contentDesc = loadingContentDescription,
            )
        }
    }
    TrackScreenViewEvent(screenName = "Calendar")
    NotificationPermissionEffect()
//    DeepLinkEffect(
//        deepLinkedUserTaskResource,
//        onTaskDeepLinkOpened,
//    )
}

@Composable
fun CalendarItem(
    calendarUiState: CalendarUiState,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when (calendarUiState) {
            CalendarUiState.Loading -> Unit
            CalendarUiState.Error -> {}
            is CalendarUiState.Success -> {
                Calendar(
                    calendarTasks = CalendarTasks(
                        calendarTasks = calendarUiState.calendarTasks.filter { it.scheduledTime.start.isNotEmpty() },
                        calendarShoots = calendarUiState.calendarShoots,
                    ),
                    daySelectionMode = DaySelectionMode.Single,
                    calendarHeaderTextConfig = CalendarTextConfig(
                        calendarTextSize = MaterialTheme.typography.labelLarge.fontSize,
                        calendarTextColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    calendarColors = CalendarColors(
                        CalendarColor(
                            headerBackgroundColor = MaterialTheme.colorScheme.primary,
                            calendarBackgroundColor = MaterialTheme.colorScheme.surface,
                            headerIconColor = MaterialTheme.colorScheme.onPrimary,
                            calendarTextColor = MaterialTheme.colorScheme.onSurface,
                            dayBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.38f,
                            ),
                        ),
                    ),
                    calendarDayConfig = CalendarDayConfig(
                        size = 48.dp,
                        textSize = MaterialTheme.typography.bodyMedium.fontSize,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        currentDayTextColor = MaterialTheme.colorScheme.surface,
                        borderColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    onDayClick = onDayClick,
                )
            }
        }
    }
}

@Composable
fun TasksItem(
    selectedDay: LocalDate,
    tasksUiState: TasksUiState,
    onTaskClick: (String) -> Unit,
    onShootClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    onDeleteActionClick: (String) -> Unit,
    onTaskResourceCompleteChanged: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyGridState()
    TrackScrollJank(scrollableState = state, stateName = "tasks:screen")

    when (tasksUiState) {
        TasksUiState.Loading -> Unit
        TasksUiState.Error -> {
            Log.e("TasksUiState", "TasksUiState Result Error")
        }

        is TasksUiState.Success -> {
            Box(modifier = modifier) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    contentPadding = PaddingValues(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = modifier
                        .fillMaxSize()
                        .testTag("tasks:screen"),
                    state = state,
                ) {
                    item {
                        Text(
                            text = "${selectedDay.month}, ${selectedDay.dayOfMonth}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 6.dp),
                        )
                    }
                    shootFeed(
                        shoots = tasksUiState.shoots,
                        shootTasks = tasksUiState.shootTasks,
                        onTaskResourcesCompleteChanged = onTaskResourceCompleteChanged,
                        onTaskClick = onTaskClick,
                        onShootClick = onShootClick,
                    )
                    taskFeed(
                        tasks = tasksUiState.tasks,
                        onTaskClick = onTaskClick,
                        onEditActionClick = onEditActionClick,
                        onDeleteActionClick = onDeleteActionClick,
                    )
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            // NOTE: Add space for the content to clear the "offline" snackbar.
                            // TODO: Check that the Scaffold handles this correctly in SitApp
                            // NOTE: if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {
    // NOTE: Permission requests should only be made from an Activity Context, which is not present
    //  in previews
    if (LocalInspectionMode.current) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val notificationsPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

//@Composable
//private fun DeepLinkEffect(
//    taskResource: TaskResource?,
//    onDeepLinkOpened: (String) -> Unit,
//) {
//    LaunchedEffect(userTaskResource) {
//        if (userTaskResource == null) return@LaunchedEffect
//        if (!userTaskResource.isCompleted) onDeepLinkOpened(userTaskResource.id)
//    }
//}

private fun selectedDayTaskItemsSize(
    tasksUiState: TasksUiState,
): Int {
    val tasksSize = when (tasksUiState) {
        TasksUiState.Loading -> 1
        TasksUiState.Error -> 2
        is TasksUiState.Success -> tasksUiState.tasks.size
    }
    return tasksSize
}
