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
import com.ngapps.phototime.core.data.repository.tasks.TaskResourceEntityQuery
import com.ngapps.phototime.core.data.repository.tasks.TasksRepository
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestTasksRepository : TasksRepository {

    /**
     * The backing hot flow for the list of topics ids for testing.
     */
    private val taskResourcesFlow: MutableSharedFlow<List<TaskResource>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getTaskResources(query: TaskResourceEntityQuery): Flow<List<TaskResource>> =
        taskResourcesFlow.map { taskResources ->
            var result = taskResources
            query.filterTaskIds?.let { filterNewsIds ->
                result = taskResources.filter {
                    filterNewsIds.contains(it.id)
                }
            }
            result
        }

    override fun getTaskResource(id: String): Flow<TaskResource> {
        return taskResourcesFlow.map { tasks -> tasks.find { it.id == id }!! }
    }

    override suspend fun getDeleteTaskResource(taskId: String): DataResult<ResponseResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getDeleteTaskEntity(taskId: String) {
        TODO("Not yet implemented")
    }

    /**
     * A test-only API to allow controlling the list of news resources from tests.
     */
    fun sendTaskResources(taskResources: List<TaskResource>) {
        taskResourcesFlow.tryEmit(taskResources)
    }

    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
