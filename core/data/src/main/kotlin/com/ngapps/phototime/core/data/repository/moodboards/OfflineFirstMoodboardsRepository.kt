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

package com.ngapps.phototime.core.data.repository.moodboards

import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.changeListSync
import com.ngapps.phototime.core.data.model.moodboard.asEntity
import com.ngapps.phototime.core.database.dao.moodboards.MoodboardResourceDao
import com.ngapps.phototime.core.database.model.moodboards.MoodboardResourceEntity
import com.ngapps.phototime.core.database.model.moodboards.asExternalModel
import com.ngapps.phototime.core.datastore.ChangeListVersions
import com.ngapps.phototime.core.datastore.PtPreferencesDataSource
import com.ngapps.phototime.core.model.data.moodboard.MoodboardResource
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.model.moodboard.NetworkMoodboardResource
import com.ngapps.phototime.core.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [MoodboardsRepository].
 * Reads are exclusively from local storage to support offline access.
 */
class OfflineFirstMoodboardsRepository @Inject constructor(
    private val ptPreferencesDataSource: PtPreferencesDataSource,
    private val moodboardResourceDao: MoodboardResourceDao,
    private val network: SyncPtNetworkDataSource,
    private val notifier: Notifier,
) : MoodboardsRepository {

    override fun getMoodboardResources(
        query: MoodboardResourceEntityQuery,
    ): Flow<List<MoodboardResource>> = moodboardResourceDao.getMoodboardResources(
        useFilterMoodboardIds = query.filterMoodboardIds != null,
        filterMoodboardIds = query.filterMoodboardIds ?: emptySet(),
    )
        .map { it.map(MoodboardResourceEntity::asExternalModel) }

    override fun getMoodboardResource(id: String): Flow<MoodboardResource> =
        moodboardResourceDao.getMoodboardResource(id).map { it.asExternalModel() }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::moodboardResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                network.getMoodboardResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(moodboardResourceVersion = latestVersion)
            },
            modelDeleter = moodboardResourceDao::deleteMoodboardResources,
            modelUpdater = { changedIds ->
                val userData = ptPreferencesDataSource.userData.first()

                // TODO: Make this more efficient, there is no need to retrieve populated
                //  moodboard resources when all that's needed are the ids
                val existingMoodboardResourceIdsThatHaveChanged =
                    moodboardResourceDao.getMoodboardResources(
                        useFilterMoodboardIds = true,
                        filterMoodboardIds = changedIds.toSet(),
                    )
                        .first()
                        .map { it.id }
                        .toSet()
                // No need to retrieve anything if notifications won't be sent

                if (isFirstSync) {
                    // When we first retrieve moodboards, mark everything viewed, so that we aren't
                    // overwhelmed with all historical moodboards.
//                    sitPreferencesDataSource.setMoodboardResourcesCompleted(changedIds, true)
                }

                // Obtain the moodboards resources which have changed from the network and upsert them locally
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkMoodboardResources = network.getMoodboardResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!
                    moodboardResourceDao.upsertMoodboardResources(
                        moodboardResourceEntities = networkMoodboardResources.map(
                            NetworkMoodboardResource::asEntity,
                        ),
                    )
                }

                val addedMoodboardResources = moodboardResourceDao.getMoodboardResources(
                    useFilterMoodboardIds = true,
                    filterMoodboardIds = changedIds.toSet() - existingMoodboardResourceIdsThatHaveChanged,
                )
                    .first()
                    .map(MoodboardResourceEntity::asExternalModel)

                if (addedMoodboardResources.isNotEmpty()) {
                    notifier.postMoodboardNotifications(
                        moodboardResources = addedMoodboardResources,
                    )
                }
            },
        )
    }

    companion object {
        // NOTE: Heuristic value to optimize for serialization and deserialization cost on client and server
        //  for each moodboard resource batch.
        private const val SYNC_BATCH_SIZE = 40
    }
}