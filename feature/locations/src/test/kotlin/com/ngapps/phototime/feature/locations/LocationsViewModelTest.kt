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

package com.ngapps.phototime.feature.locations

import com.ngapps.phototime.core.data.repository.locations.LocationResourceEntityQuery
import com.ngapps.phototime.core.domain.locations.GetDeleteLocationUseCase
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.testing.repository.TestLocationsRepository
import com.ngapps.phototime.core.testing.repository.TestUserDataRepository
import com.ngapps.phototime.core.testing.util.MainDispatcherRule
import com.ngapps.phototime.core.testing.util.TestSyncManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class ForYouViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()
    private val userDataRepository = TestUserDataRepository()
    private val locationsRepository = TestLocationsRepository()
    private val getDeleteLocation = GetDeleteLocationUseCase(
        locationsRepository = locationsRepository,
    )
    private lateinit var viewModel: LocationsViewModel

    @Before
    fun setup() {
        viewModel = LocationsViewModel(
            syncManager = syncManager,
            userRepository = userDataRepository,
            locationsRepository = locationsRepository,
            getDeleteLocation = getDeleteLocation,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(LocationsUiState.Loading, viewModel.locationsFeedState.value)
    }

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoLocations() = runTest {
        syncManager.setSyncing(true)

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            true,
            viewModel.isSyncing.value,
        )

        collectJob.cancel()
    }

    @Test
    fun locationResourceUpdatesAfterLoading() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.locationsFeedState.collect() }

        val locationCategories = locationsRepository.getLocationResourcesUniqueCategories()
            .combine(userDataRepository.userData) { locationCategories, userCategories ->
                (locationCategories + userCategories.locationCategories).toSet()
            }

        val categoriesWithLocations = locationCategories.flatMapLatest { categories ->
            locationsRepository.getLocationResources(
                query = LocationResourceEntityQuery(
                    filterLocationCategories = categories,
                ),
            ).map { locationResources ->
                val categoriesWithLocations = mutableMapOf<String, List<LocationResource>>()

                categories.forEach { category ->
                    categoriesWithLocations[category] = emptyList()
                }

                locationResources.forEach { locationResource ->
                    val category = locationResource.category
                    val locations =
                        categoriesWithLocations.getOrDefault(
                            category,
                            emptyList(),
                        ) + locationResource
                    categoriesWithLocations[category] = locations
                }
            }
        }

        locationsRepository.sendLocationResources(sampleLocationResources)

        assertEquals(
            LocationsUiState.Success(
                feed = mapOf<String, List<LocationResource>>(

                ),
            ),
            viewModel.locationsFeedState.value,
        )

        collectJob1.cancel()
    }
}

private val sampleLocationResources = listOf(
    LocationResource(
        id = "1",
        category = "Nature",
        title = "Sunny Meadows",
        description = "Explore the beauty of sunny meadows with breathtaking views.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        address = "Brest, Vokzalnaya",
        lat = "53.9045",
        lng = "27.5615",
    ),
    LocationResource(
        id = "2",
        category = "Urban",
        title = "City Lights",
        description = "Experience the vibrant city lights and urban atmosphere in Minsk.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        address = "Minsk, Nezavisimosti",
        lat = "53.8506",
        lng = "27.4577",
    ),
    LocationResource(
        id = "3",
        category = "Historical",
        title = "Ancient Castle",
        description = "Step into history with a visit to the ancient castle in Gomel.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        address = "Gomel, Centralnaya",
        lat = "53.8560",
        lng = "27.6628",
    ),
    LocationResource(
        id = "4",
        category = "Modern",
        title = "Futuristic Hub",
        description = "Discover the future in the modern hub located in Minsk.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        address = "Minsk, Vostochnaya",
        lat = "53.9041",
        lng = "27.5615",
    ),
)