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

package com.ngapps.phototime.feature.calendar.calendar.ui.component.button

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.ngapps.phototime.feature.calendar.calendar.color.CalendarColors

/**
 * An internal composable function that renders an icon button for the Calendar library.
 *
 * @param imageVector The vector icon to display.
 * @param modifier The modifier for the icon button.
 * @param contentDescription The content description for accessibility.
 * @param onClick The callback function to invoke when the button is clicked.
 */
@Composable
internal fun CalendarIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    calendarColors: CalendarColors,
    contentDescription: String? = null,
    onClick: () -> Unit = {}
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .wrapContentSize()
            .clip(CircleShape)
    ) {
        Icon(
            modifier = Modifier,
            tint = calendarColors.color.headerIconColor,
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}
