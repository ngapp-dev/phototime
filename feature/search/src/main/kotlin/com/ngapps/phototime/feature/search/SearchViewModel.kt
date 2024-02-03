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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.analytics.AnalyticsEvent
import com.ngapps.phototime.core.domain.GetRecentSearchQueriesUseCase
import com.ngapps.phototime.core.domain.GetSearchContentsCountUseCase
import com.ngapps.phototime.core.domain.GetSearchContentsUseCase
import com.ngapps.phototime.core.analytics.AnalyticsHelper
import com.ngapps.phototime.core.analytics.AnalyticsEvent.Param
import com.ngapps.phototime.core.data.repository.RecentSearchRepository
import com.ngapps.phototime.core.result.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ngapps.phototime.core.result.Result

@HiltViewModel
class SearchViewModel @Inject constructor(
    getSearchContentsUseCase: GetSearchContentsUseCase,
    getSearchContentsCountUseCase: GetSearchContentsCountUseCase,
    recentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    private val recentSearchRepository: RecentSearchRepository,
    private val savedStateHandle: SavedStateHandle,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    val searchQuery = savedStateHandle.getStateFlow(SEARCH_QUERY, "")

    val searchResultUiState: StateFlow<SearchResultUiState> =
        getSearchContentsCountUseCase().flatMapLatest { totalCount ->
            if (totalCount < SEARCH_MIN_FTS_ENTITY_COUNT) {
                flowOf(SearchResultUiState.SearchNotReady)
            } else {
                searchQuery.flatMapLatest { query ->
                    if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                        flowOf(SearchResultUiState.EmptyQuery)
                    } else {
                        getSearchContentsUseCase(query).asResult().map {
                            when (it) {
                                is Result.Success -> {
                                    SearchResultUiState.Success(
                                        taskResources = it.data.taskResources,
                                        locationResources = it.data.locationResources,
                                        contactResources = it.data.contactResources,
                                        shootResources = it.data.shootResources,
                                    )
                                }

                                is Result.Loading -> {
                                    SearchResultUiState.Loading
                                }

                                is Result.Error -> {
                                    SearchResultUiState.LoadFailed
                                }
                            }
                        }
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResultUiState.Loading,
        )

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        recentSearchQueriesUseCase().map(RecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecentSearchQueriesUiState.Loading,
            )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    /**
     * Called when the search action is explicitly triggered by the user. For example, when the
     * search icon is tapped in the IME or when the enter key is pressed in the search text field.
     *
     * The search results are displayed on the fly as the user types, but to explicitly save the
     * search query in the search text field, defining this method.
     */
    fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            recentSearchRepository.insertOrReplaceRecentSearch(query)
        }
        analyticsHelper.logEvent(
            AnalyticsEvent(
                type = SEARCH_QUERY,
                extras = listOf(
                    Param(SEARCH_QUERY, query),
                ),
            ),
        )
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }
}

/** Minimum length where search query is considered as [SearchResultUiState.EmptyQuery] */
private const val SEARCH_QUERY_MIN_LENGTH = 2

/** Minimum number of the fts table's entity count where it's considered as search is not ready */
private const val SEARCH_MIN_FTS_ENTITY_COUNT = 1
private const val SEARCH_QUERY = "searchQuery"
