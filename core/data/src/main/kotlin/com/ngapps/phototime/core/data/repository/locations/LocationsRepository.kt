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

package com.ngapps.phototime.core.data.repository.locations

import com.ngapps.phototime.core.data.Syncable
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.location.LocationResourceQuery
import com.ngapps.phototime.core.model.data.location.SearchAutocomplete
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulation class for query parameters for [LocationResource]
 */
data class LocationResourceEntityQuery(
    /**
     * Locations ids to filter for. Null means any location id will match.
     */
    val filterLocationIds: Set<String>? = null,
    /**
     * Locations category to filter for. Null means any location category will match.
     */
    val filterLocationCategories: Set<String>? = null,
)

interface LocationsRepository : Syncable {
    /**
     * Gets the available locations as a stream
     */
    fun getLocationResources(
        query: LocationResourceEntityQuery = LocationResourceEntityQuery(
            filterLocationIds = null,
            filterLocationCategories = null,
        ),
    ): Flow<List<LocationResource>>

    /**
     * Gets data for a specific location
     */
    fun getLocationResource(id: String): Flow<LocationResource>

    /**
     * Gets location resource unique categories
     */
    fun getLocationResourcesUniqueCategories(): Flow<List<String>>

    /**
     * Gets location resource search result for autocomplete
     */
    suspend fun getSearchAutocomplete(query: String): DataResult<List<SearchAutocomplete>>

    /**
     * Save location to the backend through the api
     */
    suspend fun getSaveLocation(location: LocationResourceQuery): DataResult<ResponseResource>

    /**
     * Delete location from the backend through the api
     */
    suspend fun getDeleteLocationResource(locationId: String): DataResult<ResponseResource>

    /**
     * Delete location from the local database
     */
    suspend fun getDeleteLocationEntity(locationId: String)
}
