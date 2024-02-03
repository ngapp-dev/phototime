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

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ngapps.phototime.core.data.util.getFileProviderUri
import com.ngapps.phototime.core.designsystem.component.PtButton
import com.ngapps.phototime.core.designsystem.component.PtCategorySelector
import com.ngapps.phototime.core.designsystem.component.PtLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtModalBottomSheet
import com.ngapps.phototime.core.designsystem.component.PtTextFieldWithErrorState
import com.ngapps.phototime.core.designsystem.component.PtTextFieldWithTrailingIcon
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.contact.ContactResourceQuery
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.PtImagePicker
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import com.ngapps.phototime.core.ui.TrackScrollJank
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun EditContactRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    viewModel: EditContactViewModel = hiltViewModel(),
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val context = LocalContext.current
    val contactUiState by viewModel.editContactUiState.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    val selectedImageUris by viewModel.selectedImageUris.collectAsStateWithLifecycle()
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.triggerAction(EditContactAction.SaveSelectedImageUris(uris.map { it.toString() }))
            showBottomSheet = false
        }
    }
    val fileProviderUri by rememberSaveable { mutableStateOf(context.getFileProviderUri()) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && !fileProviderUri.path.isNullOrEmpty()) {
                viewModel.triggerAction(
                    EditContactAction.SaveSelectedImageUris(listOf(fileProviderUri.toString())),
                )
            }
            showBottomSheet = false
        }

    TrackScreenViewEvent(screenName = "Edit contact: ${viewModel.contactId}")

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is EditContactViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
                EditContactViewEvent.NavigateBack -> onBackClick()
            }
        }
    }

    EditContactScreen(
        contactUiState = contactUiState,
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
        onDetachImageClick = { viewModel.triggerAction(EditContactAction.RemoveSelectedImageUri(it)) },
        onShowBottomSheetClick = { showBottomSheet = it },
        onShowAlertDialogClick = { showAlertDialog = it },
        onSaveButtonClick = { viewModel.triggerAction(EditContactAction.SaveContact(it)) },
        onUpdateContactCategories = {
            viewModel.triggerAction(EditContactAction.UpdateContactCategories(it))
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun EditContactScreen(
    contactUiState: EditContactUiState,
    showBottomSheet: Boolean,
    showAlertDialog: Boolean,
    selectedImageUris: List<String>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onCaptureImageClick: () -> Unit,
    onAttachImageClick: () -> Unit,
    onDetachImageClick: (Int) -> Unit,
    onShowBottomSheetClick: (Boolean) -> Unit,
    onShowAlertDialogClick: (Boolean) -> Unit,
    onSaveButtonClick: (ContactResourceQuery) -> Unit,
    onUpdateContactCategories: (List<String>) -> Unit,
) {
    val state = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    TrackScrollJank(scrollableState = state, stateName = "editContact:screen")
    TrackScreenViewEvent(screenName = "Edit contact")

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
    var contactCategory by rememberSaveable { mutableStateOf("") }
    if (showAlertDialog) {
        val configuration = LocalConfiguration.current
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
            onDismissRequest = { onShowAlertDialogClick(false) },
            title = {
                Text(
                    text = stringResource(R.string.contact_categories),
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                PtTextFieldWithErrorState(
                    enabled = true,
                    text = contactCategory,
                    label = "Category",
                    isError = false,
                    validate = { },
                    errorMessage = "",
                    textResult = { contactCategory = it },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Text(
                    text = stringResource(R.string.dismiss_dialog_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable {
                            onUpdateContactCategories(listOf(contactCategory))
                            onShowAlertDialogClick(false)
                        },
                )
            },
        )
    }

    Column(modifier = modifier) {
        EditContactToolbar(onBackClick = onBackClick)
        when (contactUiState) {
            EditContactUiState.Loading -> {
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

            EditContactUiState.Error -> {
                Log.e("EditContactUiState", "Error")
            }

            is EditContactUiState.Success -> {
                val categories by rememberSaveable { mutableStateOf(contactUiState.categories) }
                var selectedCategory by rememberSaveable { mutableStateOf(categories.firstOrNull()) }
                var name by rememberSaveable { mutableStateOf(contactUiState.contact?.name ?: "") }
                var description by rememberSaveable {
                    mutableStateOf(contactUiState.contact?.description ?: "")
                }
                var contact by rememberSaveable {
                    mutableStateOf(contactUiState.contact?.phone ?: "")
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    PtTextFieldWithTrailingIcon(
                        text = name,
                        label = stringResource(R.string.contact_name),
                        textResult = { name = it },
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PtTextFieldWithTrailingIcon(
                        text = description,
                        label = stringResource(R.string.contact_description),
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
                        categoriesTitleRes = stringResource(id = R.string.contact_category),
                        categories = categories,
                        onCategorySelected = { selectedCategory = it },
                        onEditCategories = { onShowAlertDialogClick(true) },
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PtTextFieldWithTrailingIcon(
                        text = contact,
                        label = stringResource(R.string.contact_communication),
                        textResult = { contact = it },
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        maxLines = Int.MAX_VALUE,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(40.dp))
                    PtButton(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        enabled = name.isNotBlank(),
                        onClick = {
                            onSaveButtonClick(
                                ContactResourceQuery(
                                    category = selectedCategory ?: "",
                                    firstName = name,
                                    lastName = name,
                                    description = description,
                                    photos = selectedImageUris,
                                    phone = contact,
                                    insta = contact,
                                ),
                            )
                        },
                    ) {
                        Text(text = stringResource(id = R.string.save_contact))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContactToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    PtTopAppBar(
        modifier = modifier,
        titleRes = R.string.edit_contact,
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
fun AddContactScreenPreview() {
    PtTheme {
        EditContactScreen(
            contactUiState = EditContactUiState.Loading,
            selectedImageUris = emptyList(),
            showBottomSheet = false,
            showAlertDialog = false,
            onBackClick = {},
            onCaptureImageClick = {},
            onAttachImageClick = {},
            onDetachImageClick = {},
            onShowBottomSheetClick = {},
            onShowAlertDialogClick = {},
            onUpdateContactCategories = {},
            onSaveButtonClick = {},
        )
    }
}

