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

package com.ngapps.phototime.core.data.repository.tasks

import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.changeListSync
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.model.task.asEntity
import com.ngapps.phototime.core.database.dao.tasks.TaskResourceDao
import com.ngapps.phototime.core.database.model.tasks.TaskResourceEntity
import com.ngapps.phototime.core.database.model.tasks.asExternalModel
import com.ngapps.phototime.core.datastore.ChangeListVersions
import com.ngapps.phototime.core.datastore.PtPreferencesDataSource
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.model.task.NetworkTaskResource
import com.ngapps.phototime.core.notifications.Notifier
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [TasksRepository].
 * Reads are exclusively from local storage to support offline access.
 */
class OfflineFirstTasksRepository @Inject constructor(
    private val ptPreferencesDataSource: PtPreferencesDataSource,
    private val taskResourceDao: TaskResourceDao,
    private val syncNetwork: SyncPtNetworkDataSource,
    private val uploadNetwork: UploadPtNetworkDataSource,
    private val notifier: Notifier,
) : TasksRepository {

    override fun getTaskResources(
        query: TaskResourceEntityQuery,
    ): Flow<List<TaskResource>> = taskResourceDao.getTaskResources(
        useFilterTaskIds = query.filterTaskIds != null,
        filterTaskIds = query.filterTaskIds ?: emptySet(),
        useFilterTaskDate = query.filterTaskDate != null,
        filterTaskDate = query.filterTaskDate ?: "",
    )
        .map { it.map(TaskResourceEntity::asExternalModel) }

    override fun getTaskResource(id: String): Flow<TaskResource> =
        taskResourceDao.getTaskResource(id).map { it.asExternalModel() }

    override suspend fun getDeleteTaskResource(taskId: String): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val deleteResult = uploadNetwork.deleteTask(taskId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }


    override suspend fun getDeleteTaskEntity(taskId: String) {
        taskResourceDao.deleteTaskResources(listOf(taskId))
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::taskResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                syncNetwork.getTaskResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(taskResourceVersion = latestVersion)
            },
            modelDeleter = taskResourceDao::deleteTaskResources,
            modelUpdater = { changedIds ->
                val userData = ptPreferencesDataSource.userData.first()

                // TODO: Make this more efficient, there is no need to retrieve populated
                //  task resources when all that's needed are the ids
                val existingTaskResourceIdsThatHaveChanged = taskResourceDao.getTaskResources(
                    useFilterTaskIds = true,
                    filterTaskIds = changedIds.toSet(),
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
                    val networkTaskResources = syncNetwork.getTaskResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!
                    taskResourceDao.upsertTaskResources(
                        taskResourceEntities = networkTaskResources.map(
                            NetworkTaskResource::asEntity,
                        ),
                    )
                }

                val addedTaskResources = taskResourceDao.getTaskResources(
                    useFilterTaskIds = true,
                    filterTaskIds = changedIds.toSet() - existingTaskResourceIdsThatHaveChanged,
                )
                    .first()
                    .map(TaskResourceEntity::asExternalModel)

                if (addedTaskResources.isNotEmpty()) {
                    notifier.postTaskNotifications(
                        taskResources = addedTaskResources,
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