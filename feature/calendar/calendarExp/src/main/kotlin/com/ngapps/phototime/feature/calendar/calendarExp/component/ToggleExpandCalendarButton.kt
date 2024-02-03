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

package com.ngapps.phototime.feature.calendar.calendarExp.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ngapps.phototime.core.designsystem.component.PtIconToggleButton
import com.ngapps.phototime.core.designsystem.icon.PtIcons

@Composable
fun ToggleExpandCalendarButton(
    isExpanded: Boolean,
    expand: () -> Unit,
    collapse: () -> Unit,
    color: Color
) {
    PtIconToggleButton(
        checked = isExpanded,
        onCheckedChange = { isChecked -> if (isChecked) expand() else collapse() },
        icon = {
            if (isExpanded) {
                Icon(PtIcons.KeyboardArrowUp, "Collapse calendar", tint = color)
            } else {
                Icon(PtIcons.KeyboardArrowDown, "Expand calendar", tint = color)
            }
        },
    )
}