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

package com.ngapps.phototime.core.domain

import com.ngapps.phototime.core.data.repository.tasks.TasksRepository
import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.model.data.task.TaskResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * A use case which obtains a list of topics with their followed state.
 */
class GetTaskResourcesUseCase @Inject constructor(
    private val taskRepository: TasksRepository,
    private val userDataRepository: UserDataRepository,
) {
    /**
     * Returns a list of topics with their associated followed state.
     *
     * @param sortBy - the field used to sort the topics. Default NONE = no sorting.
     */
    operator fun invoke(sortBy: TaskSortField = TaskSortField.NONE): Flow<List<TaskResource>> {
        return taskRepository.getTaskResources()
    }
}

enum class TaskSortField {
    NONE,
    NAME,
}

