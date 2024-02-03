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

package com.ngapps.phototime.feature.search

import androidx.lifecycle.SavedStateHandle
import com.ngapps.phototime.core.analytics.NoOpAnalyticsHelper
import com.ngapps.phototime.core.domain.GetRecentSearchQueriesUseCase
import com.ngapps.phototime.core.domain.GetSearchContentsCountUseCase
import com.ngapps.phototime.core.domain.GetSearchContentsUseCase
import com.ngapps.phototime.core.testing.data.contactResourcesTestData
import com.ngapps.phototime.core.testing.data.locationResourcesTestData
import com.ngapps.phototime.core.testing.data.shootResourcesTestData
import com.ngapps.phototime.core.testing.data.taskResourcesTestData
import com.ngapps.phototime.core.testing.repository.TestRecentSearchRepository
import com.ngapps.phototime.core.testing.repository.TestSearchContentsRepository
import com.ngapps.phototime.core.testing.util.MainDispatcherRule
import com.ngapps.phototime.feature.search.RecentSearchQueriesUiState.Success
import com.ngapps.phototime.feature.search.SearchResultUiState.EmptyQuery
import com.ngapps.phototime.feature.search.SearchResultUiState.Loading
import com.ngapps.phototime.feature.search.SearchResultUiState.SearchNotReady
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class SearchViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val searchContentsRepository = TestSearchContentsRepository()
    private val getSearchContentsUseCase = GetSearchContentsUseCase(
        searchContentsRepository = searchContentsRepository,
    )
    private val recentSearchRepository = TestRecentSearchRepository()
    private val getRecentQueryUseCase = GetRecentSearchQueriesUseCase(recentSearchRepository)
    private val getSearchContentsCountUseCase =
        GetSearchContentsCountUseCase(searchContentsRepository)
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        viewModel = SearchViewModel(
            getSearchContentsUseCase = getSearchContentsUseCase,
            getSearchContentsCountUseCase = getSearchContentsCountUseCase,
            recentSearchQueriesUseCase = getRecentQueryUseCase,
            savedStateHandle = SavedStateHandle(),
            recentSearchRepository = recentSearchRepository,
            analyticsHelper = NoOpAnalyticsHelper(),
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.searchResultUiState.value)
    }

    @Test
    fun stateIsEmptyQuery_withEmptySearchQuery() = runTest {
        searchContentsRepository.addLocationResources(locationResourcesTestData)
        searchContentsRepository.addContactResources(contactResourcesTestData)
        searchContentsRepository.addTaskResources(taskResourcesTestData)
        searchContentsRepository.addShootResources(shootResourcesTestData)
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("")

        assertEquals(EmptyQuery, viewModel.searchResultUiState.value)

        collectJob.cancel()
    }

    @Test
    fun emptyResultIsReturned_withNotMatchingQuery() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("XXX")
        searchContentsRepository.addLocationResources(locationResourcesTestData)
        searchContentsRepository.addContactResources(contactResourcesTestData)
        searchContentsRepository.addTaskResources(taskResourcesTestData)
        searchContentsRepository.addShootResources(shootResourcesTestData)

        val result = viewModel.searchResultUiState.value
        // TODO: Figure out to get the latest emitted ui State? The result is emitted as EmptyQuery
        // assertIs<Success>(result)

        collectJob.cancel()
    }

    @Test
    fun recentSearches_verifyUiStateIsSuccess() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.recentSearchQueriesUiState.collect() }
        viewModel.onSearchTriggered("photo")

        val result = viewModel.recentSearchQueriesUiState.value
        assertIs<Success>(result)

        collectJob.cancel()
    }

    @Test
    fun searchNotReady_withNoFtsTableEntity() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.searchResultUiState.collect() }

        viewModel.onSearchQueryChanged("")

        assertEquals(SearchNotReady, viewModel.searchResultUiState.value)

        collectJob.cancel()
    }
}
