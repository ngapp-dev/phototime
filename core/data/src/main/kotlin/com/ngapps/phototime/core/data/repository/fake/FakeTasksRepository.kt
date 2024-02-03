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
import com.ngapps.phototime.core.data.model.task.asEntity
import com.ngapps.phototime.core.data.repository.tasks.TaskResourceEntityQuery
import com.ngapps.phototime.core.data.repository.tasks.TasksRepository
import com.ngapps.phototime.core.database.model.tasks.TaskResourceEntity
import com.ngapps.phototime.core.database.model.tasks.asExternalModel
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.fake.FakeSyncPtNetworkDataSource
import com.ngapps.phototime.core.network.model.task.NetworkTaskResource
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fake implementation of the [TasksRepository] that retrieves the task resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeTasksRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: FakeSyncPtNetworkDataSource,
    private val uploadNetwork: UploadPtNetworkDataSource,
) : TasksRepository {

    override fun getTaskResources(
        query: TaskResourceEntityQuery,
    ): Flow<List<TaskResource>> =
        flow {
            emit(
                datasource
                    .getTaskResources()
                    .filter { networkTaskResource ->
                        /**
                         * Filter out any task resources which don't match the current query.
                         * If no query parameters (filterTaskIds or filterTaskDate) are specified
                         * then the task resource is returned.
                         * */
                        listOfNotNull(
                            true,
                            query.filterTaskIds?.contains(networkTaskResource.id),
                            query.filterTaskDate?.contains(networkTaskResource.scheduledTime.start),
                        )
                            .all(true::equals)
                    }
                    .map(NetworkTaskResource::asEntity)
                    .map(TaskResourceEntity::asExternalModel),
            )
        }.flowOn(ioDispatcher)

    override fun getTaskResource(id: String): Flow<TaskResource> {
        return getTaskResources().map { it.first { taskResource -> taskResource.id == id } }
    }

    override suspend fun getDeleteTaskResource(taskId: String): DataResult<ResponseResource> =
        withContext(ioDispatcher) {
            try {
                val deleteResult = uploadNetwork.deleteTask(taskId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteTaskEntity(taskId: String) {

    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
