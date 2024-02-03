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

package com.ngapps.phototime.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.ngapps.phototime.R
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.feature.home.R as homeR
import com.ngapps.phototime.feature.tasks.R as tasksR
import com.ngapps.phototime.feature.locations.R as locationsR
import com.ngapps.phototime.feature.contacts.R as contactsR
import com.ngapps.phototime.feature.notes.R as notesR

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    LOCATIONS(
        selectedIcon = PtIcons.Location,
        unselectedIcon = PtIcons.Location,
        iconTextId = locationsR.string.locations,
        titleTextId = locationsR.string.locations,
    ),
    CONTACTS(
        selectedIcon = PtIcons.Contact,
        unselectedIcon = PtIcons.Contact,
        iconTextId = contactsR.string.contacts,
        titleTextId = contactsR.string.contacts,
    ),
    HOME(
        selectedIcon = PtIcons.Home,
        unselectedIcon = PtIcons.Home,
        iconTextId = homeR.string.home,
        titleTextId = R.string.app_name,
    ),
    CALENDAR(
        selectedIcon = PtIcons.Task,
        unselectedIcon = PtIcons.Task,
        iconTextId = tasksR.string.calendar,
        titleTextId = tasksR.string.calendar,
    ),
    NOTES(
        selectedIcon = PtIcons.Note,
        unselectedIcon = PtIcons.Note,
        iconTextId = notesR.string.notes,
        titleTextId = notesR.string.notes,
    )
}
