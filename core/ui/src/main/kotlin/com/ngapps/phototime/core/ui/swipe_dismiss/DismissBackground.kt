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

package com.ngapps.phototime.core.ui.swipe_dismiss

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.icon.PtIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(
    dismissState: DismissState,
    modifier: Modifier = Modifier,
) {
    val color =
        when (dismissState.dismissDirection) {
            DismissDirection.StartToEnd -> MaterialTheme.colorScheme.tertiary
            DismissDirection.EndToStart -> MaterialTheme.colorScheme.error
            null -> Color.Transparent
        }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (dismissState.dismissDirection == DismissDirection.StartToEnd)
            Icon(
                imageVector = PtIcons.Edit1,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier,
            )
        Spacer(modifier = Modifier)
        if (dismissState.dismissDirection == DismissDirection.EndToStart)
            Icon(
                imageVector = PtIcons.Delete1,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier,
            )
    }
}