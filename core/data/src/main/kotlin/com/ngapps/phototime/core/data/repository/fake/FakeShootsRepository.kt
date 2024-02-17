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

import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.model.shoot.asEntity
import com.ngapps.phototime.core.data.repository.shoots.ShootResourceEntityQuery
import com.ngapps.phototime.core.data.repository.shoots.ShootsRepository
import com.ngapps.phototime.core.database.model.shoots.ShootResourceEntity
import com.ngapps.phototime.core.database.model.shoots.asExternalModel
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.shoot.ShootResourceQuery
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.fake.FakeSyncRetrofitPtNetwork
import com.ngapps.phototime.core.network.model.shoot.NetworkShootResource
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fake implementation of the [ShootsRepository] that retrieves the shoot resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeShootsRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: FakeSyncRetrofitPtNetwork,
    private val uploadNetwork: UploadPtNetworkDataSource,
) : ShootsRepository {

    override fun getShootResources(
        query: ShootResourceEntityQuery,
    ): Flow<List<ShootResource>> =
        flow {
            emit(
                datasource
                    .getShootResources()
                    .filter { networkShootResource ->
                        /**
                         * Filter out any shoot resources which don't match the current query.
                         * If no query parameters (filterShootIds or filterShootDate) are specified
                         * then the shoot resource is returned.
                         * */
                        listOfNotNull(
                            true,
                            query.filterShootIds?.contains(networkShootResource.id),
                            query.filterShootDate?.contains(networkShootResource.scheduledTime.start),
                        )
                            .all(true::equals)
                    }
                    .map(NetworkShootResource::asEntity)
                    .map(ShootResourceEntity::asExternalModel),
            )
        }.flowOn(ioDispatcher)

    override fun getShootResource(id: String): Flow<ShootResource> =
        getShootResources().map { it.first { shootResource -> shootResource.id == id } }

    override suspend fun getSaveShoot(shoot: ShootResourceQuery): ResponseResource {
        return ResponseResource("", "", "", "")
    }

    override suspend fun getDeleteShootResource(shootId: String): DataResult<ResponseResource> =
        withContext(ioDispatcher) {
            try {
                val deleteResult = uploadNetwork.deleteShoot(shootId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteShootEntity(shootId: String) {

    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
