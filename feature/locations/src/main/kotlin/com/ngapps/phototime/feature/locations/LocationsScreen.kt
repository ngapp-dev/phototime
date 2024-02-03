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

package com.ngapps.phototime.feature.locations

import android.Manifest
import android.os.Build
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ngapps.phototime.core.designsystem.component.PtCreateCategoryTextButton
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.scrollbar.DraggableScrollbar
import com.ngapps.phototime.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.ngapps.phototime.core.designsystem.component.scrollbar.scrollbarState
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import com.ngapps.phototime.core.ui.locations.LocationCategoryCard
import com.ngapps.phototime.core.ui.locations.LocationResourceCard
import com.ngapps.phototime.core.ui.locations.LocationResourcePreviewParameterProvider
import com.ngapps.phototime.core.ui.swipe_dismiss.PtSwipeToDismiss
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun LocationsRoute(
    onLocationClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LocationsViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val locationsUiState by viewModel.locationsFeedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is LocationsViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
            }
        }
    }

    LocationsScreen(
        isSyncing = isSyncing,
        locationsUiState = locationsUiState,
        onLocationClick = onLocationClick,
        onEditActionClick = onEditActionClick,
        onDeleteActionClick = { viewModel.triggerAction(LocationsAction.DeleteLocation(it)) },
        modifier = modifier,
    )
}

@Composable
internal fun LocationsScreen(
    isSyncing: Boolean,
    locationsUiState: LocationsUiState,
    onLocationClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    onDeleteActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUiLoading = locationsUiState is LocationsUiState.Loading
    var expandedState by rememberSaveable { mutableStateOf(listOf<String>()) }

    // NOTE: This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isUiLoading }

    val state = rememberLazyGridState()
    TrackScrollJank(scrollableState = state, stateName = "location:feed")

    when (locationsUiState) {
        LocationsUiState.Loading -> Unit
        is LocationsUiState.Success -> {
            Box(modifier = modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("locationCategory:feed"),
                    state = state,
                ) {
                    locationsUiState.feed.forEach { (category, locations) ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            LocationCategoryCard(
                                locationResourceCategory = Pair(category, locations),
                                onExpandClick = {
                                    if (expandedState.contains(category)) {
                                        expandedState -= category
                                    } else {
                                        expandedState += category
                                    }
                                },
                            )
                        }
                        items(locations) { location ->
                            if (expandedState.contains(category)) {
                                PtSwipeToDismiss(
                                    onEditActionClick = { onEditActionClick(location.id) },
                                    onDeleteActionClick = { onDeleteActionClick(location.id) },
                                    modifier = Modifier.padding(vertical = 6.dp),
                                ) {
                                    LocationResourceCard(
                                        locationResource = location,
                                        onLocationClick = onLocationClick,
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
                val itemsAvailable = feedItemsSize(locationsUiState)
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

    TrackScreenViewEvent(screenName = "Locations")
    LocationPermissionEffect()
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun LocationPermissionEffect() {
    // NOTE: Permission requests should only be made from an Activity Context, which is not present7
    //  in previews
    if (LocalInspectionMode.current) return
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val locationsPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
    )
    LaunchedEffect(locationsPermissionState) {
        val status = locationsPermissionState.allPermissionsGranted
        if (!status) {
            locationsPermissionState.launchMultiplePermissionRequest()
        }
    }
}

private fun feedItemsSize(feedState: LocationsUiState): Int {
    val feedSize = when (feedState) {
        LocationsUiState.Loading -> 1
        is LocationsUiState.Success -> feedState.feed.size
    }
    return feedSize
}

@DevicePreviews
@Composable
fun LocationsScreenPopulated(
    @PreviewParameter(LocationResourcePreviewParameterProvider::class)
    locationResources: List<LocationResource>,
) {
    PtTheme {
        LocationsScreen(
            isSyncing = false,
            locationsUiState = LocationsUiState.Success(
                feed = mapOf(
                    "Category 1" to locationResources,
                    "Category 2" to locationResources,
                    "Category 3" to locationResources,
                ),
            ),
            onLocationClick = {},
            onEditActionClick = {},
            onDeleteActionClick = {},
        )
    }
}
