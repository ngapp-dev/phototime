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

package com.ngapps.phototime.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ngapps.phototime.feature.home.AddHomeRoute
import com.ngapps.phototime.feature.home.HomeRoute

private const val homeGraphRoutePattern = "home_graph"
const val homeRoute = "home_route"

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(homeGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.homeGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = homeGraphRoutePattern,
        startDestination = homeRoute,
        ) {
        composable(route = homeRoute) {
            HomeRoute()
        }
        nestedGraphs()
    }
}

const val addHomeRoute = "add_home_route"

fun NavController.navigateToAddHome(navOptions: NavOptions? = null) {
    this.navigate(addHomeRoute, navOptions)
}

fun NavGraphBuilder.addHomeScreen(
    onBackClick: () -> Unit,
) {
    composable(route = addHomeRoute) {
        AddHomeRoute(
            onBackClick = onBackClick,
        )
    }
}
