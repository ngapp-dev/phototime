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

package com.ngapps.phototime.feature.calendar.calendar.ui.component.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.feature.calendar.calendar.util.MultiplePreviews

/**
 * Composable function that renders a CalendarIndicator.
 *
 * @param index The index of the indicator.
 * @param size The size of the indicator.
 * @param color The color of the indicator.
 * @param modifier The modifier for the indicator.
 */
@Composable
fun CalendarIndicator(
    index: Int,
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 1.dp)
            .clip(shape = CircleShape)
            // FIXME: Fix colors
            .background(color = Color.Red.copy(alpha = (index + 1) * 0.3f))
            .size(size = size.div(12))
    )
}

@Composable
@MultiplePreviews
private fun CalendarIndicatorPreview() {
    CalendarIndicator(
        index = 1,
        size = 40.dp,
        color = Color(0xFF000000)
    )
}
