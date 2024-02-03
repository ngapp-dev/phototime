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

import android.graphics.drawable.Drawable
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapps.phototime.core.designsystem.component.PtLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtModalBottomSheet
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.LocationAddress
import com.ngapps.phototime.core.ui.TitleCategoryDescriptionPhotosCard
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import com.ngapps.phototime.core.ui.locations.LocationResourcePreviewParameterProvider
import com.ngapps.phototime.feature.osm.DefaultMapProperties
import com.ngapps.phototime.feature.osm.Marker
import com.ngapps.phototime.feature.osm.OpenStreetMap
import com.ngapps.phototime.feature.osm.ZoomButtonVisibility
import com.ngapps.phototime.feature.osm.rememberCameraState
import com.ngapps.phototime.feature.osm.rememberMarkerState
import com.ngapps.phototime.feature.osm.rememberOverlayManagerState
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.TilesOverlay

@Composable
internal fun SingleLocationRoute(
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SingleLocationViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val locationUiState: LocationUiState by viewModel.locationUiState.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = "Location: ${viewModel.locationId}")

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is SingleLocationViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
                SingleLocationViewEvent.NavigateBack -> onBackClick()
            }
        }
    }

    SingleLocationScreen(
        locationUiState = locationUiState,
        onBackClick = onBackClick,
        onEditActionClick = onEditActionClick,
        onDeleteActionClick = { viewModel.triggerAction(SingleLocationAction.DeleteLocation(it)) },
        onDownloadImageClick = { viewModel.triggerAction(SingleLocationAction.DownloadImage(it)) },
        modifier = modifier,
    )
}

@VisibleForTesting
@Composable
internal fun SingleLocationScreen(
    locationUiState: LocationUiState,
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    onDeleteActionClick: (String) -> Unit,
    onDownloadImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()
    TrackScrollJank(scrollableState = state, stateName = "singleLocation:screen")

    when (locationUiState) {
        LocationUiState.Loading ->
            Column(modifier = modifier) {
                SingleLocationToolbar(
                    onBackClick = onBackClick,
                )
                PtLoadingWheel(
                    modifier = modifier,
                    contentDesc = stringResource(id = R.string.loading),
                )
            }

        LocationUiState.Error -> TODO()
        is LocationUiState.Success -> {
            Column(modifier = modifier) {
                SingleLocationToolbar(
                    onBackClick = onBackClick,
                    onEditActionClick = { onEditActionClick.invoke(locationUiState.location?.id ?: "") },
                    onDeleteActionClick = { onDeleteActionClick.invoke(locationUiState.location?.id ?: "") },
                )
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(
                    state = state,
                    modifier = modifier.padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    singleLocationBody(
                        location = locationUiState.location,
                        userLocation = locationUiState.userLocation,
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

private fun LazyListScope.singleLocationBody(
    location: LocationResource?,
    userLocation: Pair<String, String>,
    onDownloadImageClick: (String) -> Unit,
) {
    item {
        TitleCategoryDescriptionPhotosCard(
            title = location?.title ?: "",
            category = location?.category ?: "",
            description = location?.description ?: "",
            photos = location?.photos ?: emptyList(),
            onDownloadClick = onDownloadImageClick,
        )
    }
    item {
        LocationAddress(
            trailingIcon = PtIcons.Location,
            text = location?.address ?: "",
        )
    }
    item {
        Box(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .size(232.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            MarkerPage(
                title = location?.title ?: "",
                address = location?.address ?: "",
                lat = location?.lat ?: "",
                lng = location?.lng ?: "",
            )
        }
    }
}

@Composable
fun MarkerPage(
    title: String,
    address: String,
    lat: String,
    lng: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val markerCoordinates = if (lat.toDoubleOrNull() != null && lng.toDoubleOrNull() != null) {
        GeoPoint(lat.toDouble(), lng.toDouble())
    } else {
        GeoPoint(53.89940556727764, 27.55284152779563)
    }

    val cameraState = rememberCameraState {
        geoPoint = markerCoordinates
        zoom = 12.0
    }

    val markerState = rememberMarkerState(
        geoPoint = markerCoordinates,
        rotation = 90f,
    )

    val markerIcon: Drawable? by remember {
        mutableStateOf(context.getDrawable(PtIcons.Marker))
    }

    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    val overlayManagerState = rememberOverlayManagerState()

    SideEffect {
        mapProperties = mapProperties
            .copy(isTilesScaledToDpi = true)
            .copy(tileSources = TileSourceFactory.MAPNIK)
            .copy(isEnableRotationGesture = false)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
            .copy(isDarkMode = TilesOverlay.INVERT_COLORS)
    }
    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        properties = mapProperties,
        overlayManagerState = overlayManagerState,
        onFirstLoadListener = {
            val copyright = CopyrightOverlay(context)
            overlayManagerState.overlayManager.add(copyright)
        },
    ) {
        Marker(
            state = markerState,
            icon = markerIcon,
            title = title,
            snippet = address,
        ) {
            Box(
                modifier = modifier
                    .background(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(all = 12.dp),
            ) {
                Column(
                    modifier = modifier.wrapContentSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = it.title)
                    Text(text = it.snippet, fontSize = 12.sp)
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleLocationToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditActionClick: () -> Unit = {},
    onDeleteActionClick: () -> Unit = {},
) {

    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        PtModalBottomSheet(
            items = listOf(
                Triple(PtIcons.Edit1, R.string.edit_location) {
                    onEditActionClick()
                },
                Triple(PtIcons.Delete1, R.string.delete_location) {
                    onDeleteActionClick()
                },
            ),
            onDismiss = { showBottomSheet = false },
        )
    }

    PtTopAppBar(
        modifier = modifier,
        titleRes = R.string.location,
        navigationIcon = PtIcons.ArrowBack,
        navigationIconContentDescription = stringResource(R.string.back),
        moreActionIcon = PtIcons.MoreVert,
        moreActionIconContentDescription = stringResource(R.string.more),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        onNavigationClick = { onBackClick() },
        onMoreActionClick = { showBottomSheet = true },
    )
}

@DevicePreviews
@Composable
fun SingleLocationScreenPopulated(
    @PreviewParameter(LocationResourcePreviewParameterProvider::class)
    locationResources: List<LocationResource>,
) {
    PtTheme {
        SingleLocationScreen(
            locationUiState = LocationUiState.Success(
                userLocation = Pair("", ""),
                location = locationResources[0],
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
fun SingleLocationScreenLoading() {
    PtTheme {
        SingleLocationScreen(
            locationUiState = LocationUiState.Loading,
            onBackClick = {},
            onEditActionClick = {},
            onDeleteActionClick = {},
            onDownloadImageClick = {},
        )
    }
}
