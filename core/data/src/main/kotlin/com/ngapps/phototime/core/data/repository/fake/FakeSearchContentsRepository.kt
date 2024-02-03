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

package com.ngapps.phototime.core.data.repository.fake

import com.ngapps.phototime.core.data.repository.SearchContentsRepository
import com.ngapps.phototime.core.model.data.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Fake implementation of the [SearchContentsRepository]
 */
class FakeSearchContentsRepository @Inject constructor() : SearchContentsRepository {

    override suspend fun populateFtsData() { /* no-op */ }
    override fun searchContents(searchQuery: String): Flow<SearchResult> = flowOf()
    override fun getSearchContentsCount(): Flow<Int> = flowOf(1)
}
