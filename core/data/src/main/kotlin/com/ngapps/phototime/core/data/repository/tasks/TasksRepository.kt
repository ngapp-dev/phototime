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

import com.ngapps.phototime.core.data.Syncable
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulation class for query parameters for [TaskResource]
 */
data class TaskResourceEntityQuery(
    /**
     * Tasks ids to filter for. Null means any task id will match.
     */
    val filterTaskIds: Set<String>? = null,

    /**
     * Tasks dates to filter for. Null means any task date will match.
     */
    val filterTaskDate: String? = null,
)

interface TasksRepository : Syncable {
    /**
     * Gets the available tasks as a stream
     */
    fun getTaskResources(
        query: TaskResourceEntityQuery = TaskResourceEntityQuery(
            filterTaskIds = null,
            filterTaskDate = null
        ),
    ): Flow<List<TaskResource>>

    /**
     * Gets data for a specific task
     */
    fun getTaskResource(id: String): Flow<TaskResource>

    /**
     * Delete task from the backend through the api
     */
    suspend fun getDeleteTaskResource(taskId: String): DataResult<ResponseResource>

    /**
     * Delete task from the local database
     */
    suspend fun getDeleteTaskEntity(taskId: String)
}
