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

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ngapps.phototime.core.data.util.getFileProviderUri
import com.ngapps.phototime.core.designsystem.component.PtButton
import com.ngapps.phototime.core.designsystem.component.PtLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtModalBottomSheet
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtTextFieldWithTrailingIcon
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.shoot.ShootResourceQuery
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.PtImagePicker
import com.ngapps.phototime.core.ui.SingleItemCollapsedCard
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank

@Composable
internal fun EditShootRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: EditShootViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val shootUiState by viewModel.editShootUiState.collectAsStateWithLifecycle()

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val selectedImageUris by viewModel.selectedImageUris.collectAsStateWithLifecycle()
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.triggerAction(EditShootAction.SaveSelectedImageUris(uris.map { it.toString() }))
            showBottomSheet = false
        }
    }
    val fileProviderUri by rememberSaveable { mutableStateOf(context.getFileProviderUri()) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && !fileProviderUri.path.isNullOrEmpty()) {
                viewModel.triggerAction(
                    EditShootAction.SaveSelectedImageUris(listOf(fileProviderUri.toString())),
                )
            }
            showBottomSheet = false
        }

    TrackScreenViewEvent(screenName = "Edit shoot: ${viewModel.shootId}")
    EditShootScreen(
        shootUiState = shootUiState,
        showBottomSheet = showBottomSheet,
        selectedImageUris = selectedImageUris,
        onBackClick = onBackClick,
        onCaptureImageClick = { cameraLauncher.launch(fileProviderUri) },
        onAttachImageClick = {
            multiplePhotoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onDetachImageClick = { viewModel.triggerAction(EditShootAction.RemoveSelectedImageUri(it)) },
        onShowBottomSheetClick = { showBottomSheet = it },
        onSaveButtonClick = { viewModel.triggerAction(EditShootAction.SaveShoot(it)) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun EditShootScreen(
    shootUiState: EditShootUiState,
    showBottomSheet: Boolean,
    selectedImageUris: List<String>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onCaptureImageClick: () -> Unit,
    onAttachImageClick: () -> Unit,
    onDetachImageClick: (Int) -> Unit,
    onShowBottomSheetClick: (Boolean) -> Unit,
    onSaveButtonClick: (ShootResourceQuery) -> Unit,
) {
    val isUiLoading = shootUiState is EditShootUiState.Loading
    var expandedState by rememberSaveable { mutableStateOf(listOf<String>()) }
    val state = rememberLazyGridState()
    val focusManager = LocalFocusManager.current

    TrackScrollJank(scrollableState = state, stateName = "editShoot:screen")
    TrackScreenViewEvent(screenName = "Edit shoot")

    val cameraPermissionState = cameraPermissionEffect()

    if (showBottomSheet) {
        PtModalBottomSheet(
            items = listOf(
                Triple(PtIcons.Camera, R.string.camera) {
                    if (cameraPermissionState != null && cameraPermissionState.status == PermissionStatus.Granted) {
                        onCaptureImageClick()
                    }
                },
                Triple(PtIcons.Gallery, R.string.gallery) {
                    onAttachImageClick()
                },
            ),
            onDismiss = { onShowBottomSheetClick(false) },
        )
    }
    Column {
        EditShootToolbar(onBackClick = onBackClick)
        when (shootUiState) {
            EditShootUiState.Loading -> {
                PtLoadingWheel(
                    modifier = modifier,
                    contentDesc = stringResource(id = R.string.loading),
                )
            }

            EditShootUiState.Error -> {
                Log.e("EditShootUiState", "Error")
            }

            is EditShootUiState.Success -> {
                var title by rememberSaveable { mutableStateOf(shootUiState.shoot?.title ?: "") }
                var description by rememberSaveable {
                    mutableStateOf(shootUiState.shoot?.description ?: "")
                }
                val tasks = stringResource(id = R.string.tasks)
                val dateTime = stringResource(id = R.string.date_time)
                val moodboards = stringResource(id = R.string.moodboards)
                val contacts = stringResource(id = R.string.contacts)
                val locations = stringResource(id = R.string.locations)

                Box(modifier = modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(300.dp),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = modifier
                            .fillMaxSize()
                            .testTag("singleShoot:feed"),
                        state = state,
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column {
                                Spacer(modifier = Modifier.height(32.dp))
                                PtTextFieldWithTrailingIcon(
                                    text = title,
                                    label = stringResource(R.string.title),
                                    textResult = { title = it },
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                PtTextFieldWithTrailingIcon(
                                    text = description,
                                    label = stringResource(R.string.description),
                                    textResult = { description = it },
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                    maxLines = Int.MAX_VALUE,
                                    trailingIcon = PtIcons.Pin,
                                    onIconClick = { onShowBottomSheetClick(true) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 80.dp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                PtImagePicker(
                                    selectedImageUris = selectedImageUris,
                                    onDetachImageClick = onDetachImageClick,
                                )
                            }
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                SingleItemCollapsedCard(
                                    title = tasks,
                                    onExpandClick = {
                                        if (expandedState.contains(tasks)) {
                                            expandedState -= tasks
                                        } else {
                                            expandedState += tasks
                                        }
                                    },
                                )
                            }
                        }
                        if (expandedState.contains(tasks)) {

                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column {
                                SingleItemCollapsedCard(
                                    title = dateTime,
                                    onExpandClick = {
                                        if (expandedState.contains(dateTime)) {
                                            expandedState -= dateTime
                                        } else {
                                            expandedState += dateTime
                                        }
                                    },
                                )
                            }
                        }
                        if (expandedState.contains(dateTime)) {

                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SingleItemCollapsedCard(
                                title = moodboards,
                                onExpandClick = {
                                    if (expandedState.contains(moodboards)) {
                                        expandedState -= moodboards
                                    } else {
                                        expandedState += moodboards
                                    }
                                },
                            )
                        }
                        if (expandedState.contains(moodboards)) {

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

                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            SingleItemCollapsedCard(
                                title = locations,
                                onExpandClick = {
                                    if (expandedState.contains(locations)) {
                                        expandedState -= locations
                                    } else {
                                        expandedState += locations
                                    }
                                },
                            )
                        }
                        if (expandedState.contains(locations)) {

                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .padding(top = 40.dp)
                                    .fillMaxWidth(),
                            ) {
                                PtButton(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    enabled = title.isNotBlank(),
                                    onClick = {
                                    },
                                ) {
                                    Text(text = stringResource(id = R.string.save_shoot))
                                }
                            }
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
                    this@Column.AnimatedVisibility(
                        visible = isUiLoading,
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
private fun EditShootToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    PtTopAppBar(
        modifier = modifier,
        titleRes = R.string.edit_shoot,
        navigationIcon = PtIcons.ArrowBack,
        navigationIconContentDescription = stringResource(
            id = R.string.back,
        ),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        onNavigationClick = onBackClick,
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun cameraPermissionEffect(): PermissionState? {
    // NOTE: Permission requests should only be made from an Activity Context, which is not present
    //  in previews

    // FIXME: Make permission requests on capture button click
    if (LocalInspectionMode.current) {
        return null
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return null
    }
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA,
    )
    LaunchedEffect(cameraPermissionState) {
        val status = cameraPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    return cameraPermissionState
}

@DevicePreviews
@Composable
fun EditShootScreenPreview() {
    PtTheme {
        EditShootScreen(
            shootUiState = EditShootUiState.Loading,
            selectedImageUris = emptyList(),
            showBottomSheet = false,
            onBackClick = {},
            onCaptureImageClick = {},
            onAttachImageClick = {},
            onDetachImageClick = {},
            onShowBottomSheetClick = {},
            onSaveButtonClick = {},
        )
    }
}