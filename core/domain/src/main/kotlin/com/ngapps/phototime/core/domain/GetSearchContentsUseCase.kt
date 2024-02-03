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

package com.ngapps.phototime.core.domain

import com.ngapps.phototime.core.data.repository.SearchContentsRepository
import com.ngapps.phototime.core.model.data.SearchResult
import com.ngapps.phototime.core.model.data.UserData
import com.ngapps.phototime.core.model.data.UserSearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which returns the searched contents matched with the search query.
 */
class GetSearchContentsUseCase @Inject constructor(
    private val searchContentsRepository: SearchContentsRepository,
) {

    operator fun invoke(searchQuery: String): Flow<SearchResult> =
        searchContentsRepository.searchContents(searchQuery)
}

private fun Flow<SearchResult>.mapToUserSearchResult(userDataStream: Flow<UserData>): Flow<UserSearchResult> =
    combine(userDataStream) { searchResult, userData ->
        UserSearchResult(
            locationResources = searchResult.locationResources,
            contactResources = searchResult.contactResources,
            taskResources = searchResult.taskResources,
            shootResources = searchResult.shootResources,
        )
    }
