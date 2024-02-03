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

package com.ngapps.phototime.feature.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ngapps.phototime.feature.tasks.CalendarRoute

private const val calendarGraphRoutePattern = "calendar_graph"
const val calendarRoute = "calendar_route"


fun NavController.navigateToCalendarGraph(navOptions: NavOptions? = null) {
    this.navigate(calendarGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.calendarGraph(
    onTaskClick: (String) -> Unit,
    onShootClick: (String) -> Unit,
    onEditShootActionClick: (String) -> Unit,
    onEditTaskActionClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = calendarGraphRoutePattern,
        startDestination = calendarRoute,
    ) {
        composable(route = calendarRoute) {
            CalendarRoute(
                onTaskClick = onTaskClick,
                onShootClick = onShootClick,
                onEditShootActionClick = onEditShootActionClick,
                onEditTaskActionClick = onEditTaskActionClick,
                onShowSnackbar = onShowSnackbar,
            )
        }
        nestedGraphs()
    }
}
