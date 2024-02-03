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

package com.ngapps.phototime.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.component.DynamicAsyncImage
import com.ngapps.phototime.core.designsystem.component.PtIconButton
import com.ngapps.phototime.core.designsystem.icon.PtIcons

@Composable
fun PtImagePicker(
    selectedImageUris: List<String>,
    modifier: Modifier = Modifier,
    onDetachImageClick: (Int) -> Unit
) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        selectedImageUris.forEachIndexed { index, uri ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(78.dp)
                    .clip(RoundedCornerShape(4.dp)),
            ) {
                DynamicAsyncImage(
                    imageUrl = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .padding(top = 4.dp, start = 4.dp)
                        .clip(RoundedCornerShape(4.dp)),
//                                        .clickable {
//                                            onAttachImageClick()
//                                        },
                )
                PtIconButton(
                    onClick = { onDetachImageClick(index) },
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape,
                        ),
                ) {
                    Icon(
                        painter = painterResource(id = PtIcons.Close),
                        contentDescription = stringResource(id = R.string.detach_image),
                    )
                }
            }
        }
    }
}
