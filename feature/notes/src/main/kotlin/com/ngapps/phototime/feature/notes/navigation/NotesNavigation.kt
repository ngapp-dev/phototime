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

package com.ngapps.phototime.feature.notes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ngapps.phototime.feature.notes.EditNoteRoute
import com.ngapps.phototime.feature.notes.NotesRoute

private const val notesGraphRoutePattern = "notes_graph"
const val notesRoute = "notes_route"

fun NavController.navigateToNotesGraph(navOptions: NavOptions? = null) {
    this.navigate(notesGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.notesGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = notesGraphRoutePattern,
        startDestination = notesRoute,
    ) {
        composable(route = notesRoute) {
            NotesRoute()
        }
        nestedGraphs()
    }
}

const val addNoteRoute = "add_note_route"

fun NavController.navigateToAddNote(navOptions: NavOptions? = null) {
    this.navigate(addNoteRoute, navOptions)
}

fun NavGraphBuilder.addNoteScreen(
    onBackClick: () -> Unit,
) {
    composable(route = addNoteRoute) {
        EditNoteRoute(
            onBackClick = onBackClick,
        )
    }
}