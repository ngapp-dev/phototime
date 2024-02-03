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

package com.ngapps.phototime.feature.contacts

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.ngapps.phototime.core.designsystem.component.PtCreateCategoryTextButton
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.scrollbar.DraggableScrollbar
import com.ngapps.phototime.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.ngapps.phototime.core.designsystem.component.scrollbar.scrollbarState
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import com.ngapps.phototime.core.ui.contacts.ContactCategoryCard
import com.ngapps.phototime.core.ui.contacts.ContactResourceCard
import com.ngapps.phototime.core.ui.contacts.ContactResourcePreviewParameterProvider
import com.ngapps.phototime.core.ui.swipe_dismiss.PtSwipeToDismiss
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun ContactsRoute(
    onContactClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactsViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val contactsUiState by viewModel.contactsUiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is ContactsViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
            }
        }
    }

    ContactsScreen(
        isSyncing = isSyncing,
        contactsUiState = contactsUiState,
        onContactClick = onContactClick,
        onEditActionClick = onEditActionClick,
        onDeleteActionClick = { viewModel.triggerAction(ContactsAction.DeleteContact(it)) },
        modifier = modifier,
    )
}

@Composable
internal fun ContactsScreen(
    isSyncing: Boolean,
    contactsUiState: ContactsUiState,
    onContactClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    onDeleteActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUiLoading = contactsUiState is ContactsUiState.Loading
    var expandedState by rememberSaveable { mutableStateOf(listOf<String>()) }

    // NOTE: This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isUiLoading }

    val state = rememberLazyGridState()
    TrackScrollJank(scrollableState = state, stateName = "contact:feed")

    when (contactsUiState) {
        ContactsUiState.Loading -> Unit
        is ContactsUiState.Success -> {
            Box(modifier = modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("contacts:screen"),
                    state = state,
                ) {
                    contactsUiState.feed.forEach { (category, contacts) ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ContactCategoryCard(
                                contactResourceCategory = Pair(category, contacts),
                                onExpandClick = {
                                    if (expandedState.contains(category)) {
                                        expandedState -= category
                                    } else {
                                        expandedState += category
                                    }
                                },
                            )
                        }
                        items(contacts) { contact ->
                            if (expandedState.contains(category)) {
                                PtSwipeToDismiss(
                                    onEditActionClick = { onEditActionClick(contact.id) },
                                    onDeleteActionClick = { onDeleteActionClick(contact.id) },
                                    modifier = Modifier.padding(vertical = 6.dp),
                                ) {
                                    ContactResourceCard(
                                        contactResource = contact,
                                        onContactClick = onContactClick,
                                        modifier = Modifier.padding(vertical = 6.dp),
                                    )
                                }
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        PtCreateCategoryTextButton(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 4.dp),
                        )
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            // NOTE: Add space for the content to clear the "offline" snackbar.
                            // TODO: Check that the Scaffold handles this correctly in PtApp
                            // NOTE: if (isOffline) Spacer(modifier = Modifier.height(48.dp))
                            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                        }
                    }
                }
                AnimatedVisibility(
                    visible = isSyncing || isUiLoading,
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
                val itemsAvailable = feedItemsSize(contactsUiState)
                val scrollbarState = state.scrollbarState(
                    itemsAvailable = itemsAvailable,
                )
                state.DraggableScrollbar(
                    modifier = Modifier
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterEnd),
                    state = scrollbarState,
                    orientation = Orientation.Vertical,
                    onThumbMoved = state.rememberDraggableScroller(
                        itemsAvailable = itemsAvailable,
                    ),
                )
            }
        }
    }

    TrackScreenViewEvent(screenName = "Contacts")
}

private fun feedItemsSize(contactsUiState: ContactsUiState): Int {
    val feedSize = when (contactsUiState) {
        ContactsUiState.Loading -> 1
        is ContactsUiState.Success -> contactsUiState.feed.size
    }
    return feedSize
}

@DevicePreviews
@Composable
fun ContactsScreenPopulated(
    @PreviewParameter(ContactResourcePreviewParameterProvider::class)
    contactResources: List<ContactResource>,
) {
    PtTheme {
        ContactsScreen(
            isSyncing = false,
            contactsUiState = ContactsUiState.Success(
                feed = mapOf(
                    "Category 1" to contactResources,
                    "Category 2" to contactResources,
                    "Category 3" to contactResources,
                ),
            ),
            onContactClick = {},
            onEditActionClick = {},
            onDeleteActionClick = {},
        )
    }
}