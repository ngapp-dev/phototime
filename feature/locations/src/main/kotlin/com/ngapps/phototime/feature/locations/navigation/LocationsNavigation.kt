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

package com.ngapps.phototime.feature.locations.navigation

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.feature.locations.EditLocationRoute
import com.ngapps.phototime.feature.locations.LocationsRoute
import com.ngapps.phototime.feature.locations.SingleLocationRoute

private const val locationsGraphRoutePattern = "locations_graph"
const val locationsRoute = "single_location_route"
const val editLocationRoute = "edit_location_route"

@VisibleForTesting
internal const val locationIdArg = "locationId"

internal class LocationArg(val locationId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
            this(stringDecoder.decodeString(checkNotNull(savedStateHandle[locationIdArg])))
}

fun NavController.navigateToLocationsGraph(navOptions: NavOptions? = null) {
    this.navigate(locationsGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.locationsGraph(
    onLocationClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = locationsGraphRoutePattern,
        startDestination = locationsRoute,
    ) {
        composable(route = locationsRoute) {
            LocationsRoute(
                onLocationClick = onLocationClick,
                onEditActionClick = onEditActionClick,
                onShowSnackbar = onShowSnackbar,
            )
        }
        nestedGraphs()
    }
}

fun NavController.navigateToEditLocation(locationId: String) {
    val encodedId = Uri.encode(locationId)
    this.navigate("$editLocationRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.editLocationScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = "$editLocationRoute/{$locationIdArg}",
        arguments = listOf(
            navArgument(locationIdArg) { type = NavType.StringType },
        ),
    ) {
        EditLocationRoute(
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}

fun NavController.navigateToSingleLocation(locationId: String) {
    val encodedId = Uri.encode(locationId)
    this.navigate("$locationsRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.singleLocationScreen(
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = "$locationsRoute/{$locationIdArg}",
        arguments = listOf(
            navArgument(locationIdArg) { type = NavType.StringType },
        ),
    ) {
        SingleLocationRoute(
            onBackClick = onBackClick,
            onEditActionClick = onEditActionClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}

