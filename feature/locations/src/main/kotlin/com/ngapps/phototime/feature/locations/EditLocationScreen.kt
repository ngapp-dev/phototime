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
import android.util.Log
import androidx.activity.compose.ReportDrawnWhen
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.ngapps.phototime.core.data.util.getFileProviderUri
import com.ngapps.phototime.core.designsystem.component.PtButton
import com.ngapps.phototime.core.designsystem.component.PtCategorySelector
import com.ngapps.phototime.core.designsystem.component.PtLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtModalBottomSheet
import com.ngapps.phototime.core.designsystem.component.PtTextFieldWithTrailingIcon
import com.ngapps.phototime.core.designsystem.component.PtTextSearchBar
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.location.LocationResourceQuery
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.PtImagePicker
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import com.ngapps.phototime.core.ui.alert_dialog.EditCategoriesAlertDialog
import com.ngapps.phototime.core.ui.autocomplete.AutoCompleteBox
import com.ngapps.phototime.core.ui.autocomplete.AutoCompleteSearchBarTag
import com.ngapps.phototime.core.ui.autocomplete.asAutoCompleteEntities
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
internal fun EditLocationRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: EditLocationViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val context = LocalContext.current
    val locationUiState by viewModel.editLocationUiState.collectAsStateWithLifecycle()
    val categoriesUiState by viewModel.categoriesUiState.collectAsStateWithLifecycle()
    val searchAutocompleteState by viewModel.autocompleteSearch.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    val selectedImageUris by viewModel.selectedImageUris.collectAsStateWithLifecycle()
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.triggerAction(EditLocationAction.SaveSelectedImageUris(uris.map { it.toString() }))
            showBottomSheet = false
        }
    }
    val fileProviderUri by rememberSaveable { mutableStateOf(context.getFileProviderUri()) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && !fileProviderUri.path.isNullOrEmpty()) {
                viewModel.triggerAction(
                    EditLocationAction.SaveSelectedImageUris(listOf(fileProviderUri.toString())),
                )
            }
            showBottomSheet = false
        }

    TrackScreenViewEvent(screenName = "Edit location: ${viewModel.locationId}")

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is EditLocationViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
                EditLocationViewEvent.NavigateBack -> onBackClick()
            }
        }
    }

    EditLocationScreen(
        locationUiState = locationUiState,
        categoriesUiState = categoriesUiState,
        showBottomSheet = showBottomSheet,
        showAlertDialog = showAlertDialog,
        selectedImageUris = selectedImageUris,
        onBackClick = onBackClick,
        onCaptureImageClick = { cameraLauncher.launch(fileProviderUri) },
        onAttachImageClick = {
            multiplePhotoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onDetachImageClick = { viewModel.triggerAction(EditLocationAction.RemoveSelectedImageUri(it)) },
        onShowBottomSheetClick = { showBottomSheet = it },
        onShowAlertDialog = { showAlertDialog = it },
        onSaveButtonClick = { viewModel.triggerAction(EditLocationAction.SaveLocation(it)) },
        onUpdateLocationCategories = {
            viewModel.triggerAction(EditLocationAction.UpdateLocationCategories(it))
        },
        searchAutocompleteState = searchAutocompleteState,
        searchAutocompleteResult = { EditLocationAction.SearchAutocomplete(it) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
internal fun EditLocationScreen(
    locationUiState: EditLocationUiState,
    categoriesUiState: EditCategoriesUiState,
    showBottomSheet: Boolean,
    showAlertDialog: Boolean,
    selectedImageUris: List<String>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCaptureImageClick: () -> Unit,
    onAttachImageClick: () -> Unit,
    onDetachImageClick: (Int) -> Unit,
    onShowBottomSheetClick: (Boolean) -> Unit,
    onShowAlertDialog: (Boolean) -> Unit,
    onSaveButtonClick: (LocationResourceQuery) -> Unit,
    onUpdateLocationCategories: (List<String>) -> Unit,
    searchAutocompleteState: List<String>,
    searchAutocompleteResult: (String) -> Unit,
) {
    val isLocationLoading = locationUiState is EditLocationUiState.Loading
    val isCategoriesLoading = categoriesUiState is EditCategoriesUiState.Loading

    // This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isLocationLoading && !isCategoriesLoading }

    val state = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    TrackScrollJank(scrollableState = state, stateName = "editLocation:screen")
    TrackScreenViewEvent(screenName = "Edit location")

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

    Column(modifier = modifier) {
        EditLocationToolbar(onBackClick = onBackClick)
        when (locationUiState) {
            EditLocationUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PtLoadingWheel(
                        modifier = modifier,
                        contentDesc = stringResource(id = R.string.loading),
                    )
                }
            }

            EditLocationUiState.Error -> {
                Log.e("EditLocationUiState", "Error")
            }

            is EditLocationUiState.Success -> {
                when (categoriesUiState) {
                    is EditCategoriesUiState.Success -> {
                        var categories by rememberSaveable { mutableStateOf(categoriesUiState.categories) }

                        if (showAlertDialog) {
                            EditCategoriesAlertDialog(
                                title = stringResource(R.string.edit_location_categories),
                                categories = categoriesUiState.categories,
                                onUpdateCategories = {
                                    onUpdateLocationCategories(it)
                                    categories = it
                                },
                                onShowAlertDialog = onShowAlertDialog,
                            )
                        }


                        var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
                        var title by remember {
                            mutableStateOf(
                                locationUiState.location?.title ?: "",
                            )
                        }
                        var description by remember {
                            mutableStateOf(locationUiState.location?.description ?: "")
                        }
                        var address by rememberSaveable {
                            mutableStateOf(locationUiState.location?.address ?: "")
                        }
                        val autoCompleteEntities = searchAutocompleteState.asAutoCompleteEntities(
                            filter = { item, query ->
                                item.lowercase(Locale.getDefault())
                                    .startsWith(query.lowercase(Locale.getDefault()))
                            },
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            Spacer(modifier = Modifier.height(32.dp))
                            PtTextFieldWithTrailingIcon(
                                text = title,
                                label = stringResource(R.string.location_title),
                                textResult = { title = it },
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PtTextFieldWithTrailingIcon(
                                text = description,
                                label = stringResource(R.string.location_description),
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
                            Spacer(modifier = Modifier.height(20.dp))
                            PtCategorySelector(
                                categoriesTitleRes = stringResource(id = R.string.location_category),
                                categories = categories,
                                onCategorySelected = { selectedCategory = it },
                                onEditCategories = { onShowAlertDialog(true) },
                            )
                            Spacer(modifier = Modifier.height(20.dp))
//                    PtTextFieldWithTrailingIcon(
//                        text = address,
//                        label = stringResource(R.string.location_address),
//                        textResult = { address = it },
//                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
//                        maxLines = Int.MAX_VALUE,
//                        trailingIconV = PtIcons.Location,
//                        onIconClick = {},
//                        modifier = Modifier.fillMaxWidth(),
//                    )
                            AutoCompleteBox(
                                items = autoCompleteEntities,
                                itemContent = { item ->
                                    ValueAutoCompleteItem(item.value)
                                },
                            ) {
                                var value by remember { mutableStateOf("") }
                                val view = LocalView.current

                                onItemSelected { item ->
                                    value = item.value
                                    filter(value)
                                    view.clearFocus()
                                }

                                PtTextSearchBar(
                                    modifier = Modifier.testTag(AutoCompleteSearchBarTag),
                                    value = value,
                                    label = "Search by value",
                                    onDoneActionClick = {
                                        view.clearFocus()
                                    },
                                    onClearClick = {
                                        value = ""
                                        filter(value)
                                        view.clearFocus()
                                    },
                                    onFocusChanged = { focusState ->
                                        isSearching = focusState.isFocused
                                    },
                                    onValueChanged = { query ->
                                        searchAutocompleteResult(query)
                                        value = query
                                        filter(value)
                                        searchAutocompleteResult(query)
                                    },
                                )
                            }
                            Spacer(Modifier.height(40.dp))
                            PtButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                enabled = title.isNotBlank(),
                                onClick = {
                                    onSaveButtonClick(
                                        LocationResourceQuery(
                                            category = selectedCategory ?: "",
                                            title = title,
                                            description = description,
                                            photos = "123",
                                            address = address,
                                            lat = address,
                                            lng = address,
                                        ),
                                    )
                                },
                            ) {
                                Text(text = stringResource(id = R.string.save_location))
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    EditCategoriesUiState.Loading -> {

                    }

                    EditCategoriesUiState.Error -> {

                    }
                }
            }

        }

    }
    LocationPermissionEffect()
}

@Composable
fun ValueAutoCompleteItem(item: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = item, style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditLocationToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    PtTopAppBar(
        modifier = modifier,
        titleRes = R.string.edit_location,
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
fun EditLocationScreenPreview() {
    PtTheme {
        EditLocationScreen(
            locationUiState = EditLocationUiState.Loading,
            categoriesUiState = EditCategoriesUiState.Loading,
            selectedImageUris = emptyList(),
            showBottomSheet = false,
            showAlertDialog = false,
            onBackClick = {},
            onCaptureImageClick = {},
            onAttachImageClick = {},
            onDetachImageClick = {},
            onShowBottomSheetClick = {},
            onShowAlertDialog = {},
            onUpdateLocationCategories = {},
            onSaveButtonClick = {},
            searchAutocompleteState = emptyList(),
            searchAutocompleteResult = {},
        )
    }
}
