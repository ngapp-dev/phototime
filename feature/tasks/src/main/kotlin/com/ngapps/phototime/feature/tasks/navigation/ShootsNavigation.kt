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

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.feature.tasks.EditShootRoute
import com.ngapps.phototime.feature.tasks.SingleShootRoute

const val singleShootRoute = "single_shoot_route"
const val editShootRoute = "edit_shoot_route"

@VisibleForTesting
internal const val shootIdArg = "shootId"

internal class ShootArgs(val shootId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
            this(stringDecoder.decodeString(checkNotNull(savedStateHandle[shootIdArg])))
}

fun NavController.navigateToEditShoot(shootId: String) {
    val encodedId = Uri.encode(shootId)
    this.navigate("$editShootRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.editShootScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = "$editShootRoute/{$shootIdArg}",
        arguments = listOf(
            navArgument(shootIdArg) { type = NavType.StringType },
        ),
    ) {
        EditShootRoute(onBackClick = onBackClick)
    }
}

fun NavController.navigateToSingleShoot(shootId: String) {
    val encodedId = Uri.encode(shootId)
    this.navigate("$singleShootRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.singleShootScreen(
    onBackClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
) {
    composable(
        route = "$singleShootRoute/{$shootIdArg}",
        arguments = listOf(
            navArgument(shootIdArg) { type = NavType.StringType },
        ),
    ) {
        SingleShootRoute(
            onBackClick = onBackClick,
            onTaskClick = onTaskClick,
            onEditActionClick = onEditActionClick,
        )
    }
}
