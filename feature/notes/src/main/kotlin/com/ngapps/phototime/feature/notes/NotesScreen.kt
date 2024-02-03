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

package com.ngapps.phototime.feature.notes

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.ngapps.phototime.core.designsystem.component.PtBackground
import com.ngapps.phototime.core.designsystem.component.PtLoadingWheel
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.core.ui.TrackScreenViewEvent

@Composable
internal fun NotesRoute(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val uiState = NotesUiState.Empty

    NotesScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
internal fun NotesScreen(
    uiState: NotesUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            NotesUiState.Loading ->
                PtLoadingWheel(
                    modifier = modifier,
                    contentDesc = stringResource(id = R.string.loading),
                )

            is NotesUiState.Success -> NotesEmptyScreen() // TODO: Change to valuable data

            is NotesUiState.Empty -> NotesEmptyScreen()
        }
    }
    TrackScreenViewEvent(screenName = "Notes")
    NotificationPermissionEffect()
}

@Composable
private fun NotesEmptyScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.empty_header),
            textAlign = TextAlign.Center,
        )
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
        Manifest.permission.POST_NOTIFICATIONS,
    )
    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}

@DevicePreviews
@Composable
fun NotesScreenLoading() {
    PtTheme {
        PtBackground {
            NotesScreen(
                uiState = NotesUiState.Loading,
            )
        }
    }
}

@DevicePreviews
@Composable
fun NotesScreenEmpty() {
    PtTheme {
        PtBackground {
            NotesScreen(
                uiState = NotesUiState.Empty,
            )
        }
    }
}
