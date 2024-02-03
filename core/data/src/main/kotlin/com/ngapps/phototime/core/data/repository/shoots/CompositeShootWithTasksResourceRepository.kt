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

import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.data.repository.tasks.TaskResourceEntityQuery
import com.ngapps.phototime.core.data.repository.tasks.TasksRepository
import com.ngapps.phototime.core.model.data.shoot.ShootWithTasksResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements a [CompositeShootWithTasksResourceRepository] by combining a [TasksRepository] with a
 * [UserDataRepository].
 */
class CompositeShootWithTasksResourceRepository @Inject constructor(
    private val shootsRepository: ShootsRepository,
    private val tasksRepository: TasksRepository
) : ShootWithTasksResourceRepository {

    /**
     * Returns available shoot with tasks resources (joined with user data) matching the given query.
     */
    override fun observeShootResourcesWithTasks(
        query: ShootResourceEntityQuery,
    ): Flow<List<ShootWithTasksResource>> {
        val shootResourcesFlow = shootsRepository.getShootResources(query)

        return shootResourcesFlow.flatMapLatest { shootResources ->
            val taskIds = shootResources.flatMap { it.tasks }.toSet()
            tasksRepository.getTaskResources(
                query = TaskResourceEntityQuery(
                    filterTaskIds = taskIds,
                    filterTaskDate = null,
                )
            ).map { taskResources ->
                val shootsWithTasks = mutableListOf<ShootWithTasksResource>()

                for (shootResource in shootResources) {
                    val tasksForResource = taskResources.filter { it.id in shootResource.tasks }
                    val shootWithTasks = ShootWithTasksResource(shootResource, tasksForResource)
                    shootsWithTasks.add(shootWithTasks)
                }

                shootsWithTasks
            }
        }
    }
}
