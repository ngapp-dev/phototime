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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.ngapps.phototime.core.testing.util.TestNetworkMonitor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests [SitAppState].
 *
 * Note: This could become an unit test if Robolectric is added to the project and the Context
 *  is faked.
 */
class PtAppStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Create the test dependencies.
    private val networkMonitor = TestNetworkMonitor()

    // Subject under test.
    private lateinit var state: SitAppState

    @Test
    fun sitAppState_currentDestination() = runTest {
        var currentDestination: String? = null

        composeTestRule.setContent {
            val navController = rememberTestNavController()
            state = remember(navController) {
                SitAppState(
                    navController = navController,
                    coroutineScope = backgroundScope,
                    windowSize = getCompactWindowSize(),
                    networkMonitor = networkMonitor,
                )
            }

            // Update currentDestination whenever it changes
            currentDestination = state.currentDestination?.route

            // Navigate to destination b once
            LaunchedEffect(Unit) {
                navController.setCurrentDestination("b")
            }
        }

        assertEquals("b", currentDestination)
    }

    @Test
    fun sitAppState_destinations() = runTest {
        composeTestRule.setContent {
            state = rememberPtAppState(
                windowSize = getCompactWindowSize(),
                networkMonitor = networkMonitor,
            )
        }

        assertEquals(3, state.topLevelDestinations.size)
        assertTrue(state.topLevelDestinations[0].name.contains("locations", true))
        assertTrue(state.topLevelDestinations[1].name.contains("contacts", true))
        assertTrue(state.topLevelDestinations[2].name.contains("home", true))
        assertTrue(state.topLevelDestinations[3].name.contains("tasks", true))
        assertTrue(state.topLevelDestinations[4].name.contains("notes", true))
    }

    @Test
    fun stateIsOfflineWhenNetworkMonitorIsOffline() = runTest(UnconfinedTestDispatcher()) {
        composeTestRule.setContent {
            state = SitAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSize = DpSize(900.dp, 1200.dp),
                networkMonitor = networkMonitor,
            )
        }

        backgroundScope.launch { state.isOffline.collect() }
        networkMonitor.setConnected(false)
        assertEquals(
            true,
            state.isOffline.value,
        )
    }

    private fun getCompactWindowSize() = DpSize(500.dp, 300.dp)
}

@Composable
private fun rememberTestNavController(): TestNavHostController {
    val context = LocalContext.current
    return remember<TestNavHostController> {
        TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = "a") {
                composable("a") { }
                composable("b") { }
                composable("c") { }
                composable("d") { }
                composable("e") { }
            }
        }
    }
}