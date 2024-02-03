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
import com.ngapps.phototime.core.data.repository.shoots.ShootResourceEntityQuery
import com.ngapps.phototime.core.data.repository.shoots.ShootsRepository
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.shoot.ShootResourceQuery
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestShootsRepository : ShootsRepository {

    /**
     * The backing hot flow for the list of shoots ids for testing.
     */
    private val shootResourcesFlow: MutableSharedFlow<List<ShootResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getShootResources(query: ShootResourceEntityQuery): Flow<List<ShootResource>> =
        shootResourcesFlow.map { shootResources ->
            var result = shootResources
            query.filterShootIds?.let { filterShootIds ->
                result = shootResources.filter {
                    filterShootIds.contains(it.id)
                }
            }
            result
        }

    /**
     * A test-only API to allow controlling the list of news resources from tests.
     */
    override fun getShootResource(id: String): Flow<ShootResource> =
        shootResourcesFlow.map { shoots -> shoots.find { it.id == id }!! }

    override suspend fun getSaveShoot(shoot: ShootResourceQuery): ResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun getDeleteShootResource(shootId: String): DataResult<ResponseResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getDeleteShootEntity(shootId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
