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

package com.ngapps.phototime.feature.tasks

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.ngapps.phototime.core.rules.GrantPostNotificationsPermissionRule
import com.ngapps.phototime.feature.calendar.calendar.CalendarUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.junit.Rule
import org.junit.Test

class TasksScreenTest {

    @get:Rule(order = 0)
    val postNotificationsPermission = GrantPostNotificationsPermissionRule()

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun circularProgressIndicator_whenScreenIsLoading_exists() {
        composeTestRule.setContent {
            BoxWithConstraints {
                CalendarScreen(
                    isSyncing = false,
                    selectedDay = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    tasksUiState = TasksUiState.Loading,
                    calendarUiState = CalendarUiState.Loading,
                    deepLinkedUserTaskResource = null,
                    onTaskDeepLinkOpened = {},
                    onTaskClick = {},
                    onTaskResourceCompleteChanged = { _, _ -> },
                    onDayClick = {},
                    onShootClick = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.loading),
            )
            .assertExists()
    }

    @Test
    fun circularProgressIndicator_whenScreenIsSyncing_exists() {
        composeTestRule.setContent {
            BoxWithConstraints {
                CalendarScreen(
                    isSyncing = true,
                    selectedDay = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    tasksUiState = TasksUiState.Success(emptyList(), emptyList(), emptyList()),
                    calendarUiState = CalendarUiState.Success(emptyList(), emptyList()),
                    deepLinkedUserTaskResource = null,
                    onTaskDeepLinkOpened = {},
                    onTaskClick = {},
                    onTaskResourceCompleteChanged = { _, _ -> },
                    onDayClick = {},
                    onShootClick = {},
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.resources.getString(R.string.loading),
            )
            .assertExists()
    }
}