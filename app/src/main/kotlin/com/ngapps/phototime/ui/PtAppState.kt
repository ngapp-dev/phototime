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

package com.ngapps.phototime.ui

import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.ngapps.feature.profile.navigation.navigateToProfileGraph
import com.ngapps.phototime.core.data.util.NetworkMonitor
import com.ngapps.phototime.core.ui.TrackDisposableJank
import com.ngapps.phototime.feature.auth.navigation.navigateToSignIn
import com.ngapps.phototime.feature.contacts.navigation.navigateToContactsGraph
import com.ngapps.phototime.feature.contacts.navigation.navigateToEditContact
import com.ngapps.phototime.feature.contacts.navigation.contactsRoute
import com.ngapps.phototime.feature.home.navigation.homeRoute
import com.ngapps.phototime.feature.home.navigation.navigateToAddHome
import com.ngapps.phototime.feature.home.navigation.navigateToHomeGraph
import com.ngapps.phototime.feature.locations.navigation.navigateToEditLocation
import com.ngapps.phototime.feature.locations.navigation.navigateToLocationsGraph
import com.ngapps.phototime.feature.locations.navigation.locationsRoute
import com.ngapps.phototime.feature.notes.navigation.navigateToAddNote
import com.ngapps.phototime.feature.notes.navigation.navigateToNotesGraph
import com.ngapps.phototime.feature.notes.navigation.notesRoute
import com.ngapps.phototime.feature.search.navigation.navigateToSearch
import com.ngapps.phototime.feature.tasks.navigation.calendarRoute
import com.ngapps.phototime.feature.tasks.navigation.navigateToCalendarGraph
import com.ngapps.phototime.feature.tasks.navigation.navigateToEditShoot
import com.ngapps.phototime.feature.tasks.navigation.navigateToEditTask
import com.ngapps.phototime.navigation.TopLevelDestination
import com.ngapps.phototime.navigation.TopLevelDestination.CALENDAR
import com.ngapps.phototime.navigation.TopLevelDestination.CONTACTS
import com.ngapps.phototime.navigation.TopLevelDestination.HOME
import com.ngapps.phototime.navigation.TopLevelDestination.LOCATIONS
import com.ngapps.phototime.navigation.TopLevelDestination.NOTES
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberPtAppState(
    windowSize: DpSize,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): SitAppState {
    NavigationTrackingSideEffect(navController)

    return remember(
        navController,
        coroutineScope,
        windowSize,
        networkMonitor,
    ) {
        SitAppState(
            navController,
            coroutineScope,
            windowSize,
            networkMonitor,
        )
    }
}

@Stable
class SitAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    private val windowSize: DpSize,
    networkMonitor: NetworkMonitor,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            locationsRoute -> LOCATIONS
            contactsRoute -> CONTACTS
            homeRoute -> HOME
            calendarRoute -> CALENDAR
            notesRoute -> NOTES
            else -> null
        }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    /**
     * Per <a href="https://m3.material.io/components/navigation-drawer/guidelines">Material Design 3 guidelines</a>,
     * the selection of the appropriate navigation component should be contingent on the available
     * window size:
     * - Bottom Bar for compact window sizes (below 600dp)
     * - Navigation Rail for medium and expanded window sizes up to 1240dp (between 600dp and 1240dp)
     * - Navigation Drawer to window size above 1240dp
     */
    @OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    val navigationSuiteType: NavigationSuiteType
        @Composable get() {
            return if (windowSize.width > 1240.dp) {
                NavigationSuiteType.NavigationDrawer
            } else if (windowSize.width >= 600.dp) {
                NavigationSuiteType.NavigationRail
            } else {
                NavigationSuiteType.NavigationBar
            }
        }

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param destination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        trace("Navigation: ${destination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                    inclusive = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
            when (destination) {
                LOCATIONS -> navController.navigateToLocationsGraph(topLevelNavOptions)
                CONTACTS -> navController.navigateToContactsGraph(topLevelNavOptions)
                HOME -> navController.navigateToHomeGraph(topLevelNavOptions)
                CALENDAR -> navController.navigateToCalendarGraph(topLevelNavOptions)
                NOTES -> navController.navigateToNotesGraph(topLevelNavOptions)
            }
        }
    }

    fun navigateToSignIn() {
        navController.popBackStack()
        navController.navigateToSignIn()
    }

    fun navigateToSearch() {
        navController.navigateToSearch()
    }

    fun navigateToProfile() {
        navController.navigateToProfileGraph()
    }

    fun navigateToAddShoot() {
        navController.navigateToEditShoot(shootId = "0")
    }

    fun navigateToAddTask() {
        navController.navigateToEditTask(taskId = "0")
    }

    fun navigateToAdd(
        destination: TopLevelDestination,
        onShowBottomSheetClick: (Boolean) -> Unit,
    ) {
        when (destination) {
            LOCATIONS -> navController.navigateToEditLocation(locationId = "0")
            CONTACTS -> navController.navigateToEditContact(contactId = "0")
            HOME -> navController.navigateToAddHome()
            CALENDAR -> onShowBottomSheetClick(true)
            NOTES -> navController.navigateToAddNote()
        }
    }
}


/**
 * Stores information about navigation events to be used with JankStats
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    TrackDisposableJank(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
