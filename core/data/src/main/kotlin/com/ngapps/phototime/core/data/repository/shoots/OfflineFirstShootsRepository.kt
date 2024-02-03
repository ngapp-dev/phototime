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

import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.changeListSync
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.model.shoot.asEntity
import com.ngapps.phototime.core.database.dao.shoots.ShootResourceDao
import com.ngapps.phototime.core.database.model.shoots.ShootResourceEntity
import com.ngapps.phototime.core.database.model.shoots.asExternalModel
import com.ngapps.phototime.core.datastore.ChangeListVersions
import com.ngapps.phototime.core.datastore.PtPreferencesDataSource
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.shoot.ShootResourceQuery
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.model.shoot.NetworkShootResource
import com.ngapps.phototime.core.notifications.Notifier
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [ShootsRepository].
 * Reads are exclusively from local storage to support offline access.
 */
class OfflineFirstShootsRepository @Inject constructor(
    private val ptPreferencesDataSource: PtPreferencesDataSource,
    private val shootResourceDao: ShootResourceDao,
    private val syncNetwork: SyncPtNetworkDataSource,
    private val uploadNetwork: UploadPtNetworkDataSource,
    private val notifier: Notifier,
) : ShootsRepository {

    override fun getShootResources(
        query: ShootResourceEntityQuery,
    ): Flow<List<ShootResource>> = shootResourceDao.getShootResources(
        useFilterShootIds = query.filterShootIds != null,
        filterShootIds = query.filterShootIds ?: emptySet(),
        useFilterShootDate = query.filterShootDate != null,
        filterShootDate = query.filterShootDate ?: "",
    )
        .map { it.map(ShootResourceEntity::asExternalModel) }

    override fun getShootResource(id: String): Flow<ShootResource> =
        shootResourceDao.getShootResource(id).map { it.asExternalModel() }

    override suspend fun getSaveShoot(shoot: ShootResourceQuery): ResponseResource {
        return ResponseResource("", "", "", "")
    }

    override suspend fun getDeleteShootResource(shootId: String): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val deleteResult = uploadNetwork.deleteShoot(shootId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteShootEntity(shootId: String) {
        shootResourceDao.deleteShootResources(listOf(shootId))
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::shootResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                syncNetwork.getShootResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(shootResourceVersion = latestVersion)
            },
            modelDeleter = shootResourceDao::deleteShootResources,
            modelUpdater = { changedIds ->
                val userData = ptPreferencesDataSource.userData.first()

                // TODO: Make this more efficient, there is no need to retrieve populated
                //  shoot resources when all that's needed are the ids
                val existingShootResourceIdsThatHaveChanged =
                    shootResourceDao.getShootResources(
                        useFilterShootIds = true,
                        filterShootIds = changedIds.toSet(),
                    )
                        .first()
                        .map { it.id }
                        .toSet()
                // No need to retrieve anything if notifications won't be sent

                if (isFirstSync) {
                    // When we first retrieve tasks, mark everything viewed, so that we aren't
                    // overwhelmed with all historical tasks.
//                    sitPreferencesDataSource.setTaskResourcesCompleted(changedIds, true)
                }

                // Obtain the tasks resources which have changed from the network and upsert them locally
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkShootResources =
                        syncNetwork.getShootResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!
                    shootResourceDao.upsertShootResources(
                        shootResourceEntities = networkShootResources.map(
                            NetworkShootResource::asEntity,
                        ),
                    )
                }

                val addedShootResources = shootResourceDao.getShootResources(
                    useFilterShootIds = true,
                    filterShootIds = changedIds.toSet() - existingShootResourceIdsThatHaveChanged,
                )
                    .first()
                    .map(ShootResourceEntity::asExternalModel)

                if (addedShootResources.isNotEmpty()) {
                    notifier.postShootNotifications(
                        shootResources = addedShootResources,
                    )
                }
            },
        )
    }

    companion object {
        // NOTE: Heuristic value to optimize for serialization and deserialization cost on client and server
        //  for each task resource batch.
        private const val SYNC_BATCH_SIZE = 40
    }
}