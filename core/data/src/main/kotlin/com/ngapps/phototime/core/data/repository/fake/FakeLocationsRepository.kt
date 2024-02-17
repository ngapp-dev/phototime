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
import com.ngapps.phototime.core.data.model.location.asEntity
import com.ngapps.phototime.core.data.model.location.asNetworkModel
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.repository.locations.LocationResourceEntityQuery
import com.ngapps.phototime.core.data.repository.locations.LocationsRepository
import com.ngapps.phototime.core.database.model.locations.LocationResourceEntity
import com.ngapps.phototime.core.database.model.locations.asExternalModel
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.location.LocationResourceQuery
import com.ngapps.phototime.core.model.data.location.SearchAutocomplete
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.base.handleThrowable
import com.ngapps.phototime.core.network.fake.FakeSyncRetrofitPtNetwork
import com.ngapps.phototime.core.network.model.location.NetworkLocationResource
import com.ngapps.phototime.core.network.model.location.asExternalModel
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fake implementation of the [LocationsRepository] that retrieves the location resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeLocationsRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: FakeSyncRetrofitPtNetwork,
    private val uploadNetwork: UploadPtNetworkDataSource,
) : LocationsRepository {

    override fun getLocationResources(
        query: LocationResourceEntityQuery,
    ): Flow<List<LocationResource>> =
        flow {
            emit(
                datasource
                    .getLocationResources()
                    .filter { networkLocationResource ->
                        /**
                         * Filter out any location resources which don't match the current query.
                         * If no query parameter filterLocationIds is specified
                         * then the location resource is returned.
                         * */
                        listOfNotNull(
                            true,
                            query.filterLocationIds?.contains(networkLocationResource.id),
                        )
                            .all(true::equals)
                    }
                    .map(NetworkLocationResource::asEntity)
                    .map(LocationResourceEntity::asExternalModel),
            )
        }.flowOn(ioDispatcher)

    override fun getLocationResource(id: String): Flow<LocationResource> =
        getLocationResources().map { it.first { locationResource -> locationResource.id == id } }


    override fun getLocationResourcesUniqueCategories(): Flow<List<String>> =
        getLocationResourcesUniqueCategories()

    override suspend fun getSearchAutocomplete(query: String): DataResult<List<SearchAutocomplete>> =
        withContext(ioDispatcher) {
            try {
                val response =
                    uploadNetwork.searchAutocomplete(query).map { it.asExternalModel() }
                DataResult.Success(response)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getSaveLocation(location: LocationResourceQuery): DataResult<ResponseResource> =
        withContext(ioDispatcher) {
            try {
                val response =
                    uploadNetwork.saveLocation(location.asNetworkModel()).asExternalModel()
                DataResult.Success(response)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteLocationResource(locationId: String): DataResult<ResponseResource> =
        withContext(ioDispatcher) {
            try {
                val deleteResult = uploadNetwork.deleteLocation(locationId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }


    override suspend fun getDeleteLocationEntity(locationId: String) {

    }


    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
