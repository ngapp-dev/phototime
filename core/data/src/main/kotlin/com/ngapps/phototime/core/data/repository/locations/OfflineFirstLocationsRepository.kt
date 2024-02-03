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

import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.changeListSync
import com.ngapps.phototime.core.data.model.auth.asExternalModel
import com.ngapps.phototime.core.data.model.location.asEntity
import com.ngapps.phototime.core.data.model.location.asNetworkModel
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.database.dao.locations.LocationResourceDao
import com.ngapps.phototime.core.database.model.locations.LocationResourceEntity
import com.ngapps.phototime.core.database.model.locations.asExternalModel
import com.ngapps.phototime.core.datastore.ChangeListVersions
import com.ngapps.phototime.core.datastore.PtPreferencesDataSource
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.location.LocationResourceQuery
import com.ngapps.phototime.core.model.data.location.SearchAutocomplete
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.base.handleThrowable
import com.ngapps.phototime.core.network.model.location.NetworkLocationResource
import com.ngapps.phototime.core.network.model.location.asExternalModel
import com.ngapps.phototime.core.notifications.Notifier
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [LocationsRepository].
 * Reads are exclusively from local storage to support offline access.
 */
class OfflineFirstLocationsRepository @Inject constructor(
    private val ptPreferencesDataSource: PtPreferencesDataSource,
    private val locationResourceDao: LocationResourceDao,
    private val syncNetwork: SyncPtNetworkDataSource,
    private val uploadNetwork: UploadPtNetworkDataSource,
    private val notifier: Notifier,
) : LocationsRepository {

    override fun getLocationResources(
        query: LocationResourceEntityQuery,
    ): Flow<List<LocationResource>> = locationResourceDao.getLocationResources(
        useFilterLocationIds = query.filterLocationIds != null,
        filterLocationIds = query.filterLocationIds ?: emptySet(),
    )
        .map { it.map(LocationResourceEntity::asExternalModel) }

    override fun getLocationResource(id: String): Flow<LocationResource> =
        locationResourceDao.getLocationResource(id).map { it.asExternalModel() }

    override fun getLocationResourcesUniqueCategories(): Flow<List<String>> =
        locationResourceDao.getLocationResourcesUniqueCategories()

    override suspend fun getSearchAutocomplete(query: String): DataResult<List<SearchAutocomplete>> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    uploadNetwork.searchAutocomplete(query).map { it.asExternalModel() }
                DataResult.Success(response)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getSaveLocation(location: LocationResourceQuery): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    uploadNetwork.saveLocation(location.asNetworkModel()).asExternalModel()
                DataResult.Success(response)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteLocationResource(locationId: String): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val deleteResult = uploadNetwork.deleteLocation(locationId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteLocationEntity(locationId: String) {
        locationResourceDao.deleteLocationResources(listOf(locationId))
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::locationResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                syncNetwork.getLocationResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(locationResourceVersion = latestVersion)
            },
            modelDeleter = locationResourceDao::deleteLocationResources,
            modelUpdater = { changedIds ->
                val userData = ptPreferencesDataSource.userData.first()

                // TODO: Make this more efficient, there is no need to retrieve populated
                //  location resources when all that's needed are the ids
                val existingLocationResourceIdsThatHaveChanged =
                    locationResourceDao.getLocationResources(
                        useFilterLocationIds = true,
                        filterLocationIds = changedIds.toSet(),
                    )
                        .first()
                        .map { it.id }
                        .toSet()
                // No need to retrieve anything if notifications won't be sent

                if (isFirstSync) {
                    // When we first retrieve locations, mark everything viewed, so that we aren't
                    // overwhelmed with all historical locations.
//                    sitPreferencesDataSource.setLocationResourcesCompleted(changedIds, true)
                }

                // Obtain the locations resources which have changed from the network and upsert them locally
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkLocationResources =
                        syncNetwork.getLocationResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!
                    locationResourceDao.upsertLocationResources(
                        locationResourceEntities = networkLocationResources.map(
                            NetworkLocationResource::asEntity,
                        ),
                    )
                }

                val addedLocationResources = locationResourceDao.getLocationResources(
                    useFilterLocationIds = true,
                    filterLocationIds = changedIds.toSet() - existingLocationResourceIdsThatHaveChanged,
                )
                    .first()
                    .map(LocationResourceEntity::asExternalModel)

                if (addedLocationResources.isNotEmpty()) {
                    notifier.postLocationNotifications(
                        locationResources = addedLocationResources,
                    )
                }
            },
        )
    }

    companion object {
        private const val SYNC_BATCH_SIZE = 40
    }
}