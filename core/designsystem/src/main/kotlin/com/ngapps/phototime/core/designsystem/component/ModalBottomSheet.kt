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

package com.ngapps.phototime.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/**
 * Photo time Modal Bottom Sheet. Wraps Material 3 [ModalBottomSheet].
 *
 * @param items A list of items to display in the bottom sheet. Each item is represented by a Triple
 * containing an [ImageVector], a [String] resource ID, and a lambda function to be executed when the
 * item is clicked.
 * @param onDismiss A lambda function to be executed when the bottom sheet is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PtModalBottomSheet(
    items: List<Triple<ImageVector, Int, () -> Unit>>,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.padding(horizontal = 12.dp),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(
            alpha = PtBottomSheetDefaults.BottomSheetBackgroundAlpha,
        ),
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            items.forEach {
                item {
                    PtBottomSheetListItem(
                        leadingIcon = it.first,
                        contentDescription = stringResource(id = it.second),
                        title = stringResource(id = it.second),
                        onClick = it.third,
                    )
                }
            }
            item {
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

@Composable
private fun PtBottomSheetListItem(
    leadingIcon: ImageVector,
    contentDescription: String,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

object PtBottomSheetDefaults {
    const val BottomSheetBackgroundAlpha = 0.5f
}