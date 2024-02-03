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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarDuration.Long
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ngapps.phototime.R
import com.ngapps.phototime.core.data.util.NetworkMonitor
import com.ngapps.phototime.core.designsystem.component.PtBackground
import com.ngapps.phototime.core.designsystem.component.PtModalBottomSheet
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.feature.settings.SettingsDialog
import com.ngapps.phototime.navigation.PtNavHost
import com.ngapps.phototime.navigation.TopLevelDestination
import com.ngapps.phototime.core.designsystem.R as designR
import com.ngapps.phototime.feature.user.settings.R as settingsR

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveNavigationSuiteApi::class,
)
@Composable
fun PtApp(
    windowSize: DpSize,
    networkMonitor: NetworkMonitor,
    appState: SitAppState = rememberPtAppState(
        networkMonitor = networkMonitor,
        windowSize = windowSize,
    ),
) {
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }
    var onShowBottomSheetClick by remember { mutableStateOf(false) }

    PtBackground {

        val snackbarHostState = remember { SnackbarHostState() }

        val isOffline by appState.isOffline.collectAsStateWithLifecycle()
        // NOTE: If user is not connected to the internet show a snack bar to inform them.
        val notConnectedMessage = stringResource(R.string.not_connected)
        var currentMessage by remember { mutableStateOf(notConnectedMessage) }
        LaunchedEffect(key1 = isOffline, key2 = currentMessage) {
            if (isOffline) {
                snackbarHostState.showSnackbar(
                    message = notConnectedMessage,
                    duration = Indefinite,
                )
            }
        }

        if (showSettingsDialog) {
            SettingsDialog(
                onDismiss = { showSettingsDialog = false },
                onSignOutClick = {
                    showSettingsDialog = false
                    appState.navigateToSignIn()
                },
            )
        }
        if (onShowBottomSheetClick) {
            PtModalBottomSheet(
                items = listOf(
                    Triple(
                        PtIcons.Search,
                        com.ngapps.phototime.feature.tasks.R.string.add_shoot,
                    ) {
                        onShowBottomSheetClick = false
                        appState.navigateToAddShoot()
                    },
                    Triple(PtIcons.Task, com.ngapps.phototime.feature.tasks.R.string.add_task) {
                        onShowBottomSheetClick = false
                        appState.navigateToAddTask()
                    },
                ),
                onDismiss = { onShowBottomSheetClick = false },
            )
        }

        val currentDestination = appState.currentDestination
        val topLevelDestination = appState.currentTopLevelDestination
        NavigationSuiteScaffold(
            layoutType = appState.navigationSuiteType,
            containerColor = Color.Transparent,
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationRailContainerColor = Color.Transparent,
                navigationDrawerContainerColor = Color.Transparent,
                navigationBarContainerColor = Color.Transparent,
            ),
            navigationSuiteItems = {
                if (topLevelDestination != null) {
                    appState.topLevelDestinations.forEach { destination ->
                        val isSelected =
                            currentDestination.isTopLevelDestinationInHierarchy(destination)
                        item(
                            selected = isSelected,
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) {
                                        destination.selectedIcon
                                    } else {
                                        destination.unselectedIcon
                                    },
                                    contentDescription = null,
                                )
                            },
                            onClick = { appState.navigateToTopLevelDestination(destination) },
//                        colors = NavigationSuiteScaffoldDefaults.(
//                            selectedIconColor = SitNavigationDefaults.navigationSelectedItemColor(),
//                            unselectedIconColor = SitNavigationDefaults.navigationContentColor(),
//                            selectedTextColor = SitNavigationDefaults.navigationIndicatorColor(),
//                            unselectedTextColor = SitNavigationDefaults.navigationContentColor(),
//                            indicatorColor = SitNavigationDefaults.navigationIndicatorColor(),
//                        ),
                        )
                    }
                }
            },
        ) {
            Scaffold(
                topBar = {
                    val destination = appState.currentTopLevelDestination
                    if (destination != null) {
                        PtTopAppBar(
                            titleRes = destination.titleTextId,
                            navigationIcon = PtIcons.Search,
                            navigationIconContentDescription = stringResource(
                                id = settingsR.string.top_app_bar_navigation_icon_description,
                            ),
                            actionIcon = PtIcons.Add,
                            actionIconContentDescription = stringResource(
                                id = settingsR.string.top_app_bar_action_icon_description,
                            ),
                            profileImage = designR.drawable.profile,
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            onNavigationClick = { appState.navigateToSearch() },
                            onActionClick = {
                                appState.navigateToAdd(
                                    destination = destination,
                                    onShowBottomSheetClick = { onShowBottomSheetClick = it },
                                )
                            },
                            onProfileClick = { appState.navigateToProfile() },
                        )
                    }
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    SnackbarHost(snackbarHostState) {
                        Snackbar(snackbarData = it, shape = MaterialTheme.shapes.small)
                    }
                },
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) { padding ->
                PtNavHost(
                    appState = appState,
                    onShowSnackbar = { message, action ->
                        currentMessage = message
                        snackbarHostState.showSnackbar(
                            message = currentMessage,
                            actionLabel = action,
                            duration = Long,
                        ) == ActionPerformed
                    },
                    onSettingsClick = { showSettingsDialog = true },
                    modifier = Modifier.padding(padding),
                )
            }

            // TODO: We may want to add padding or spacer when the snackbar is shown so that
            //  content doesn't display behind it.
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false
