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

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapps.phototime.core.designsystem.component.PtLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtModalBottomSheet
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.ui.ContactMessenger
import com.ngapps.phototime.core.ui.ContactPhone
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.Note
import com.ngapps.phototime.core.ui.ResourceCardNumber
import com.ngapps.phototime.core.ui.SingleItemCollapsedCard
import com.ngapps.phototime.core.ui.TitleCategoryDescriptionPhotosCard
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import com.ngapps.phototime.core.ui.datetime.ScheduledNotificationCard
import com.ngapps.phototime.core.ui.datetime.ScheduledTimeCard
import com.ngapps.phototime.core.ui.tasks.TaskResourcePreviewParameterProvider
import com.ngapps.phototime.core.ui.tasks.TaskTitleWithDescriptionCard
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SingleTaskRoute(
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SingleTaskViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val singleTaskUiState: SingleTaskUiState by viewModel.singleTaskUiState.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = "Task: ${viewModel.taskId}")

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is SingleTaskViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
                SingleTaskViewEvent.NavigateBack -> onBackClick()
            }
        }
    }

    SingleTaskScreen(
        singleTaskUiState = singleTaskUiState,
        modifier = modifier,
        onBackClick = onBackClick,
        onEditActionClick = onEditActionClick,
        onDeleteActionClick = { viewModel.triggerAction(SingleTaskAction.DeleteTask(it)) },
        onDownloadImageClick = { viewModel.triggerAction(SingleTaskAction.DownloadImage(it)) },
    )
}

@VisibleForTesting
@Composable
internal fun SingleTaskScreen(
    singleTaskUiState: SingleTaskUiState,
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    onDeleteActionClick: (String) -> Unit,
    onDownloadImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSingleTaskUiLoading = singleTaskUiState is SingleTaskUiState.Loading
    var expandedState by rememberSaveable { mutableStateOf(listOf<String>()) }

    val state = rememberLazyGridState()
    TrackScrollJank(scrollableState = state, stateName = "singleTask:screen")

    when (singleTaskUiState) {
        is SingleTaskUiState.Error -> Unit
        is SingleTaskUiState.Loading -> {
            Column {
                SingleTaskToolbar(
                    onBackClick = onBackClick,
                    onEditActionClick = {},
                    onDeleteActionClick = {},
                )
                PtLoadingWheel(
                    modifier = modifier,
                    contentDesc = stringResource(id = R.string.loading),
                )
            }
        }

        is SingleTaskUiState.Success -> {
            val dateTime = stringResource(id = R.string.date_time)
            val contacts = stringResource(id = R.string.contacts)
            val locations = stringResource(id = R.string.locations)
            Column {
                SingleTaskToolbar(
                    onBackClick = onBackClick,
                    onEditActionClick = {
                        onEditActionClick.invoke(singleTaskUiState.task?.id ?: "")
                    },
                    onDeleteActionClick = {
                        onDeleteActionClick.invoke(singleTaskUiState.task?.id ?: "")
                    },
                )
                Box(modifier = modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(300.dp),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = modifier
                            .fillMaxSize()
                            .testTag("singleTask:screen"),
                        state = state,
                    ) {
                        item {
                            TaskTitleWithDescriptionCard(
                                title = singleTaskUiState.task?.title ?: "",
                                category = singleTaskUiState.task?.category ?: "",
                                description = singleTaskUiState.task?.description ?: "",
                            )
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SingleItemCollapsedCard(
                                title = dateTime,
                                onExpandClick = {
                                    if (expandedState.contains(dateTime)) {
                                        expandedState -= dateTime
                                    } else {
                                        expandedState += dateTime
                                    }
                                },
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }
                        if (expandedState.contains(dateTime)) {
                            item {
                                ScheduledTimeCard(
                                    scheduledTime = singleTaskUiState.task?.scheduledTime?.start
                                        ?: "",
                                )
                            }
                            item {
                                ScheduledNotificationCard(
                                    scheduledTime = singleTaskUiState.task?.scheduledTime?.notification
                                        ?: "",
                                    annotation = stringResource(id = R.string.notification),
                                )
                            }
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SingleItemCollapsedCard(
                                title = contacts,
                                onExpandClick = {
                                    if (expandedState.contains(contacts)) {
                                        expandedState -= contacts
                                    } else {
                                        expandedState += contacts
                                    }
                                },
                            )
                        }
                        if (expandedState.contains(contacts)) {
                            singleTaskUiState.contacts.forEachIndexed { index, contact ->
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    ResourceCardNumber(index = index + 1)
                                }
                                item {
                                    TitleCategoryDescriptionPhotosCard(
                                        title = contact.name,
                                        category = contact.category,
                                        description = contact.description,
                                        photos = contact.photos,
                                        onDownloadClick = onDownloadImageClick,
                                    )
                                }
                                item {
                                    ContactPhone(
                                        contactIcon = PtIcons.Phone,
                                        phoneType = stringResource(id = R.string.phone),
                                        phone = contact.phone,
                                        modifier = Modifier.padding(top = 12.dp),
                                    )
                                }
                                item {
                                    ContactMessenger(
                                        messengerIcon = PtIcons.Instagram,
                                        messengerType = stringResource(id = R.string.instagram),
                                        messenger = contact.messenger,
                                        modifier = Modifier.padding(top = 12.dp),
                                    )
                                }
                            }
                        }
                        item {
                            Note(
                                noteIcon = PtIcons.Note,
                                note = singleTaskUiState.task?.note ?: "",
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }
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
                    this@Column.AnimatedVisibility(
                        visible = isSingleTaskUiLoading,
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleTaskToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditActionClick: () -> Unit = {},
    onDeleteActionClick: () -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        PtModalBottomSheet(
            items = listOf(
                Triple(PtIcons.Edit1, R.string.edit_task) {
                    onEditActionClick()
                    showBottomSheet = false
                },
                Triple(PtIcons.Delete1, R.string.delete_task) {
                    onDeleteActionClick()
                    showBottomSheet = false
                },
            ),
            onDismiss = { showBottomSheet = false },
        )
    }

    PtTopAppBar(
        modifier = modifier,
        titleRes = R.string.task,
        navigationIcon = PtIcons.ArrowBack,
        navigationIconContentDescription = stringResource(
            id = R.string.back,
        ),
        moreActionIcon = PtIcons.MoreVert,
        moreActionIconContentDescription = stringResource(
            id = R.string.more,
        ),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        onNavigationClick = { onBackClick.invoke() },
        onMoreActionClick = { showBottomSheet = true },
    )
}

@DevicePreviews
@Composable
fun SingleTaskScreenPopulated(
    @PreviewParameter(TaskResourcePreviewParameterProvider::class)
    taskResources: List<TaskResource>,
) {
    PtTheme {
        SingleTaskScreen(
            singleTaskUiState = SingleTaskUiState.Success(
                task = taskResources[0],
                contacts = emptyList(),
            ),
            onBackClick = {},
            onEditActionClick = {},
            onDeleteActionClick = {},
            onDownloadImageClick = {},
        )
    }
}

@DevicePreviews
@Composable
fun SingleTaskScreenLoading() {
    PtTheme {
        SingleTaskScreen(
            singleTaskUiState = SingleTaskUiState.Loading,
            onBackClick = {},
            onEditActionClick = {},
            onDeleteActionClick = {},
            onDownloadImageClick = {},
        )
    }
}
