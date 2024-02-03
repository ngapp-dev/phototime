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

package com.ngapps.phototime.core.data.repository.shoots

import com.ngapps.phototime.core.data.Syncable
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.shoot.ShootResourceQuery
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulation class for query parameters for [ShootResource]
 */
data class ShootResourceEntityQuery(
    /**
     * Shoots ids to filter for. Null means any shoot id will match.
     */
    val filterShootIds: Set<String>? = null,

    /**
     * Shoots dates to filter for. Null means any shoot date will match.
     */
    val filterShootDate: String? = null,
)

interface ShootsRepository : Syncable {
    /**
     * Gets the available shoots as a stream
     */
    fun getShootResources(
        query: ShootResourceEntityQuery = ShootResourceEntityQuery(
            filterShootIds = null,
            filterShootDate = null,
        ),
    ): Flow<List<ShootResource>>

    /**
     * Gets data for a specific shoot
     */
    fun getShootResource(id: String): Flow<ShootResource>

    /**
     * Save contact to the backend through the api
     */
    suspend fun getSaveShoot(shoot: ShootResourceQuery): ResponseResource

    /**
     * Delete shoot from the backend through the api
     */
    suspend fun getDeleteShootResource(shootId: String): DataResult<ResponseResource>

    /**
     * Delete shoot from the local database
     */
    suspend fun getDeleteShootEntity(shootId: String)
}
