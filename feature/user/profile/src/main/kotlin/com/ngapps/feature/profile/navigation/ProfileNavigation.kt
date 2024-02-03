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

package com.ngapps.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ngapps.feature.profile.ProfileRoute

private const val profileGraphRoutePattern = "profile_graph"
const val profileRoute = "profile_route"

fun NavController.navigateToProfileGraph(navOptions: NavOptions? = null) {
    this.navigate(profileGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.profileGraph(
    onBackClick: () -> Unit,
    onMoreActionClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = profileGraphRoutePattern,
        startDestination = profileRoute,
    ) {
        composable(route = profileRoute) {
            ProfileRoute(
                onBackClick = onBackClick,
                onMoreActionClick = onMoreActionClick,
            )
        }
        nestedGraphs()
    }
}
