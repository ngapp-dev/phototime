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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.ngapps.feature.profile.navigation.profileGraph
import com.ngapps.phototime.feature.auth.navigation.navigateToSignIn
import com.ngapps.phototime.feature.auth.navigation.navigateToSignUp
import com.ngapps.phototime.feature.auth.navigation.signInRoute
import com.ngapps.phototime.feature.auth.navigation.signInScreen
import com.ngapps.phototime.feature.auth.navigation.signUpScreen
import com.ngapps.phototime.feature.contacts.navigation.contactsGraph
import com.ngapps.phototime.feature.contacts.navigation.editContactScreen
import com.ngapps.phototime.feature.contacts.navigation.navigateToEditContact
import com.ngapps.phototime.feature.contacts.navigation.navigateToSingleContact
import com.ngapps.phototime.feature.contacts.navigation.singleContactScreen
import com.ngapps.phototime.feature.home.navigation.addHomeScreen
import com.ngapps.phototime.feature.home.navigation.homeGraph
import com.ngapps.phototime.feature.home.navigation.navigateToHomeGraph
import com.ngapps.phototime.feature.locations.navigation.editLocationScreen
import com.ngapps.phototime.feature.locations.navigation.locationsGraph
import com.ngapps.phototime.feature.locations.navigation.navigateToEditLocation
import com.ngapps.phototime.feature.locations.navigation.navigateToSingleLocation
import com.ngapps.phototime.feature.locations.navigation.singleLocationScreen
import com.ngapps.phototime.feature.notes.navigation.addNoteScreen
import com.ngapps.phototime.feature.notes.navigation.notesGraph
import com.ngapps.phototime.feature.search.navigation.searchScreen
import com.ngapps.phototime.feature.tasks.navigation.calendarGraph
import com.ngapps.phototime.feature.tasks.navigation.editShootScreen
import com.ngapps.phototime.feature.tasks.navigation.editTaskScreen
import com.ngapps.phototime.feature.tasks.navigation.navigateToEditShoot
import com.ngapps.phototime.feature.tasks.navigation.navigateToEditTask
import com.ngapps.phototime.feature.tasks.navigation.navigateToSingleShoot
import com.ngapps.phototime.feature.tasks.navigation.navigateToSingleTask
import com.ngapps.phototime.feature.tasks.navigation.singleShootScreen
import com.ngapps.phototime.feature.tasks.navigation.singleTaskScreen
import com.ngapps.phototime.ui.SitAppState

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun PtNavHost(
    appState: SitAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = signInRoute,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        signInScreen(
            onSignUpClick = { navController.navigateToSignUp() },
            onShowSnackbar = onShowSnackbar,
            onSignInSuccess = {
                navController.popBackStack(startDestination, true)
                navController.navigateToHomeGraph()
            },
        )

        signUpScreen(
            onSignInClick = { navController.navigateToSignIn() },
            onSignUpSuccess = { navController.navigateToSignIn() },
            onShowSnackbar = onShowSnackbar,
        )

        profileGraph(
            onBackClick = navController::popBackStack,
            onMoreActionClick = onSettingsClick,
            nestedGraphs = {},
        )

        locationsGraph(
            onLocationClick = { locationId ->
                navController.navigateToSingleLocation(locationId)
            },
            onEditActionClick = { locationId ->
                navController.navigateToEditLocation(locationId)
            },
            onShowSnackbar = onShowSnackbar,
            nestedGraphs = {
                singleLocationScreen(
                    onBackClick = navController::popBackStack,
                    onEditActionClick = { locationId ->
                        navController.navigateToEditLocation(locationId)
                    },
                    onShowSnackbar = onShowSnackbar,
                )
            },
        )
        contactsGraph(
            onContactClick = { contactId ->
                navController.navigateToSingleContact(contactId)
            },
            onEditActionClick = { contactId ->
                navController.navigateToEditContact(contactId)
            },
            onShowSnackbar = onShowSnackbar,
            nestedGraphs = {
                singleContactScreen(
                    onBackClick = navController::popBackStack,
                    onEditActionClick = { contactId ->
                        navController.navigateToEditContact(contactId)
                    },
                    onShowSnackbar = onShowSnackbar,
                )
            },
        )
        homeGraph(nestedGraphs = {})
        calendarGraph(
            onTaskClick = { taskId ->
                navController.navigateToSingleTask(taskId)
            },
            onShootClick = { shootId ->
                navController.navigateToSingleShoot(shootId)
            },
            onEditShootActionClick = { shootId ->
                navController.navigateToEditShoot(shootId)
            },
            onEditTaskActionClick = { taskId ->
                navController.navigateToEditTask(taskId)
            },
            onShowSnackbar = onShowSnackbar,
            nestedGraphs = {
                singleTaskScreen(
                    onBackClick = navController::popBackStack,
                    onEditActionClick = { taskId ->
                        navController.navigateToEditTask(taskId)
                    },
                    onShowSnackbar = onShowSnackbar,
                )
                singleShootScreen(
                    onBackClick = navController::popBackStack,
                    onTaskClick = { taskId ->
                        navController.navigateToSingleTask(taskId)
                    },
                    onEditActionClick = { shootId ->
                        navController.navigateToEditShoot(shootId)
                    },
                )
            },
        )
        notesGraph(nestedGraphs = {})

        searchScreen(
            onBackClick = navController::popBackStack,
            onInterestsClick = {},
            onTopicClick = {},
        )

        editLocationScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        editContactScreen(
            onBackClick = navController::popBackStack,
            onShowSnackbar = onShowSnackbar,
        )
        addHomeScreen(
            onBackClick = navController::popBackStack,
        )
        editShootScreen(
            onBackClick = navController::popBackStack,
        )
        editTaskScreen(
            onBackClick = navController::popBackStack,
        )
        addNoteScreen(
            onBackClick = navController::popBackStack,
        )
    }
}