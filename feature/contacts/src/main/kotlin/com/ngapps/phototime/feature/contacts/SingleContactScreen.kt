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

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapps.phototime.core.designsystem.component.PtModalBottomSheet
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.ui.ContactMessenger
import com.ngapps.phototime.core.ui.ContactPhone
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.TitleCategoryDescriptionPhotosCard
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import com.ngapps.phototime.core.ui.contacts.ContactResourcePreviewParameterProvider
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SingleContactRoute(
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SingleContactViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val contactUiState: ContactUiState by viewModel.contactUiState.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = "Contact: ${viewModel.contactId}")

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is SingleContactViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
                SingleContactViewEvent.NavigateBack -> onBackClick()
            }
        }
    }

    SingleContactScreen(
        contactUiState = contactUiState,
        onBackClick = onBackClick,
        onEditActionClick = onEditActionClick,
        onDeleteActionClick = { viewModel.triggerAction(SingleContactAction.DeleteContact(it)) },
        onDownloadImageClick = { viewModel.triggerAction(SingleContactAction.DownloadImage(it)) },
        modifier = modifier,
    )
}

@VisibleForTesting
@Composable
internal fun SingleContactScreen(
    contactUiState: ContactUiState,
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    onDeleteActionClick: (String) -> Unit,
    onDownloadImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "singleContact:screen")

    when (contactUiState) {
        ContactUiState.Loading ->
            Column(modifier = modifier) {
                SingleContactToolbar(
                    onBackClick = onBackClick,
                    onEditActionClick = {},
                    onDeleteActionClick = {},
                )
                this@Column.AnimatedVisibility(
                    visible = true,
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

        ContactUiState.Error -> TODO()
        is ContactUiState.Success -> {
            Column(modifier = modifier) {
                SingleContactToolbar(
                    onBackClick = onBackClick,
                    onEditActionClick = {
                        onEditActionClick.invoke(contactUiState.contact?.id ?: "")
                    },
                    onDeleteActionClick = {
                        onDeleteActionClick.invoke(contactUiState.contact?.id ?: "")
                    },
                )
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(
                    state = state,
                    modifier = modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    singleContactBody(
                        contact = contactUiState.contact,
                        userLocation = contactUiState.userLocation,
                        onDownloadImageClick = onDownloadImageClick,
                    )
                    item {
                        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                    }
                }
            }

        }

    }
}

private fun LazyListScope.singleContactBody(
    contact: ContactResource?,
    userLocation: Pair<String, String>,
    onDownloadImageClick: (String) -> Unit,
) {
    item {
        TitleCategoryDescriptionPhotosCard(
            title = contact?.name ?: "",
            category = contact?.category ?: "",
            description = contact?.description ?: "",
            photos = contact?.photos ?: emptyList(),
            onDownloadClick = onDownloadImageClick,
        )
    }
    item {
        ContactPhone(
            contactIcon = PtIcons.Phone,
            phoneType = stringResource(id = R.string.phone),
            phone = contact?.phone ?: "",
        )
    }
    item {
        ContactMessenger(
            messengerIcon = PtIcons.Instagram,
            messengerType = stringResource(id = R.string.instagram),
            messenger = contact?.messenger ?: "",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleContactToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditActionClick: () -> Unit = {},
    onDeleteActionClick: () -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        PtModalBottomSheet(
            items = listOf(
                Triple(PtIcons.Edit1, R.string.edit_contact) {
                    onEditActionClick()
                },
                Triple(PtIcons.Delete1, R.string.delete_contact) {
                    onDeleteActionClick()
                },
            ),
            onDismiss = { showBottomSheet = false },
        )
    }

    PtTopAppBar(
        modifier = modifier,
        titleRes = R.string.contact,
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
fun SingleContactScreenPopulated(
    @PreviewParameter(ContactResourcePreviewParameterProvider::class)
    contactResources: List<ContactResource>,
) {
    PtTheme {
        SingleContactScreen(
            contactUiState = ContactUiState.Success(
                userLocation = Pair("", ""),
                contact = contactResources[0],
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
fun SingleContactScreenLoading() {
    PtTheme {
        SingleContactScreen(
            contactUiState = ContactUiState.Loading,
            onBackClick = {},
            onEditActionClick = {},
            onDeleteActionClick = {},
            onDownloadImageClick = {},
        )
    }
}
