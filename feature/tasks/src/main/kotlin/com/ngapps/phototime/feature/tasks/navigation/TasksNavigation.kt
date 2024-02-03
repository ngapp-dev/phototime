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
import com.ngapps.phototime.feature.tasks.EditTaskRoute
import com.ngapps.phototime.feature.tasks.SingleTaskRoute

const val LINKED_TASK_RESOURCE_ID = "linkedTaskResourceId"
const val singleTaskRoute = "single_task_route"
const val editTaskRoute = "edit_task_route"

@VisibleForTesting
internal const val taskIdArg = "taskId"

internal class TaskArgs(val taskId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
            this(stringDecoder.decodeString(checkNotNull(savedStateHandle[taskIdArg])))
}

fun NavController.navigateToEditTask(taskId: String) {
    val encodedId = Uri.encode(taskId)
    this.navigate("$editTaskRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.editTaskScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = "$editTaskRoute/{$taskIdArg}",
        arguments = listOf(
            navArgument(taskIdArg) { type = NavType.StringType },
        ),
    ) {
        EditTaskRoute(onBackClick = onBackClick)
    }
}

fun NavController.navigateToSingleTask(taskId: String) {
    val encodedId = Uri.encode(taskId)
    this.navigate("$singleTaskRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.singleTaskScreen(
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = "$singleTaskRoute/{$taskIdArg}",
        arguments = listOf(
            navArgument(taskIdArg) { type = NavType.StringType },
        ),
    ) {
        SingleTaskRoute(
            onBackClick = onBackClick,
            onEditActionClick = onEditActionClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
