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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom Photo time Drag Handle. Wraps Material 3 [Box].
 *
 * @param modifier Modifier to be applied to the drag handle.
 * @param width Drag handle width.
 * @param height Drag handle height.
 * @param shape Drag handle corner shape.
 * @param color Drag handle container color.
 */
@Composable
fun SitDragHandle(
    modifier: Modifier = Modifier,
    width: Dp = PtDragHandleDefaults.DragHandleWidth,
    height: Dp = PtDragHandleDefaults.DragHandleHeight,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
        Box(
            modifier
                .background(color)
                .clip(shape)
                .size(
                    width = width,
                    height = height,
                ),
        )
}

/**
 * Photo time drag handle default values.
 */
object PtDragHandleDefaults {
    val DragHandleWidth = 32.0.dp
    val DragHandleHeight = 4.0.dp
    const val DragHandleOpacity = 0.38f
}