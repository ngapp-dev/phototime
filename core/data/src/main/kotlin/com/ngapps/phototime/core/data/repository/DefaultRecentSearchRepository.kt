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

package com.ngapps.phototime.core.data.repository

import com.ngapps.phototime.core.data.model.RecentSearchQuery
import com.ngapps.phototime.core.data.model.asExternalModel
import com.ngapps.phototime.core.data.model.auth.asExternalModel
import com.ngapps.phototime.core.database.dao.RecentSearchQueryDao
import com.ngapps.phototime.core.database.model.RecentSearchQueryEntity
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject

class DefaultRecentSearchRepository @Inject constructor(
    private val recentSearchQueryDao: RecentSearchQueryDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : RecentSearchRepository {
    override suspend fun insertOrReplaceRecentSearch(searchQuery: String) {
        withContext(ioDispatcher) {
            recentSearchQueryDao.insertOrReplaceRecentSearchQuery(
                RecentSearchQueryEntity(
                    query = searchQuery,
                    queriedDate = Clock.System.now(),
                ),
            )
        }
    }

    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        recentSearchQueryDao.getRecentSearchQueryEntities(limit).map { searchQueries ->
            searchQueries.map {
                it.asExternalModel()
            }
        }

    override suspend fun clearRecentSearches() = recentSearchQueryDao.clearRecentSearchQueries()
}
