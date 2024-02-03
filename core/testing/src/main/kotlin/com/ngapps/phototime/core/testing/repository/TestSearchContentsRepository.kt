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

import com.ngapps.phototime.core.data.repository.SearchContentsRepository
import com.ngapps.phototime.core.model.data.SearchResult
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class TestSearchContentsRepository : SearchContentsRepository {

    private val cachedLocationResources: MutableList<LocationResource> = mutableListOf()
    private val cachedContactResources: MutableList<ContactResource> = mutableListOf()
    private val cachedTaskResources: MutableList<TaskResource> = mutableListOf()
    private val cachedShootResources: MutableList<ShootResource> = mutableListOf()

    override suspend fun populateFtsData() { /* no-op */
    }

    override fun searchContents(searchQuery: String): Flow<SearchResult> = flowOf(
        SearchResult(
            locationResources = cachedLocationResources.filter {
                it.category.contains(searchQuery) ||
                        it.title.contains(searchQuery) ||
                        it.description.contains(searchQuery) ||
                        it.address.contains(searchQuery) ||
                        it.lat.contains(searchQuery) ||
                        it.lng.contains(searchQuery)
            },
            contactResources = cachedContactResources.filter {
                it.category.contains(searchQuery) ||
                        it.name.contains(searchQuery) ||
                        it.description.contains(searchQuery) ||
                        it.phone.contains(searchQuery) ||
                        it.messenger.contains(searchQuery)
            },
            taskResources = cachedTaskResources.filter {
                it.category.contains(searchQuery) ||
                        it.title.contains(searchQuery) ||
                        it.description.contains(searchQuery) ||
                        it.note.contains(searchQuery)
            },
            shootResources = cachedShootResources.filter {
                it.title.contains(searchQuery) ||
                        it.description.contains(searchQuery)
            },
        ),
    )

    override fun getSearchContentsCount(): Flow<Int> = flow {
        emit(cachedLocationResources.size + cachedLocationResources.size + cachedTaskResources.size + cachedShootResources.size)
    }

    /**
     * Test only method to add the location resources to the stored list in memory
     */
    fun addLocationResources(locationResources: List<LocationResource>) {
        cachedLocationResources.addAll(locationResources)
    }

    /**
     * Test only method to add the contact resources to the stored list in memory
     */
    fun addContactResources(contactResources: List<ContactResource>) {
        cachedContactResources.addAll(contactResources)
    }

    /**
     * Test only method to add the task resources to the stored list in memory
     */
    fun addTaskResources(taskResources: List<TaskResource>) {
        cachedTaskResources.addAll(taskResources)
    }

    /**
     * Test only method to add the shoot resources to the stored list in memory
     */
    fun addShootResources(shootResources: List<ShootResource>) {
        cachedShootResources.addAll(shootResources)
    }
}
