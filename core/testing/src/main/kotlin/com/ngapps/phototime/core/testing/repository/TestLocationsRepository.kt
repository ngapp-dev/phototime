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

package com.ngapps.phototime.core.testing.repository

import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.repository.locations.LocationResourceEntityQuery
import com.ngapps.phototime.core.data.repository.locations.LocationsRepository
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.location.LocationResourceQuery
import com.ngapps.phototime.core.model.data.location.SearchAutocomplete
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestLocationsRepository : LocationsRepository {

    /**
     * The backing hot flow for the list of topics ids for testing.
     */
    private val locationResourcesFlow: MutableSharedFlow<List<LocationResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    /**
     * A test-only API to allow controlling the list of location resources from tests.
     */
    fun sendLocationResources(locationResources: List<LocationResource>) {
        locationResourcesFlow.tryEmit(locationResources)
    }

    override fun getLocationResources(query: LocationResourceEntityQuery): Flow<List<LocationResource>> =
        locationResourcesFlow.map { locationResources ->
            var result = locationResources
            query.filterLocationIds?.let { filterLocationIds ->
                result = locationResources.filter {
                    filterLocationIds.contains(it.id)
                }
            }
            result
        }

    override fun getLocationResource(id: String): Flow<LocationResource> {
        return locationResourcesFlow.map { locations -> locations.find { it.id == id }!! }
    }

    override fun getLocationResourcesUniqueCategories(): Flow<List<String>> =
        locationResourcesFlow.map { locationResources ->
            locationResources
                .distinctBy { it.category }
                .map { it.category }
        }

    override suspend fun getSearchAutocomplete(query: String): DataResult<List<SearchAutocomplete>> {
        TODO("Not yet implemented")
    }


    override suspend fun getSaveLocation(location: LocationResourceQuery): DataResult<ResponseResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getDeleteLocationResource(locationId: String): DataResult<ResponseResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getDeleteLocationEntity(locationId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
