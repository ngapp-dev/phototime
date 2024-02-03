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

package com.ngapps.phototime.core.data.repository

import com.ngapps.phototime.core.database.dao.contacts.ContactResourceDao
import com.ngapps.phototime.core.database.dao.contacts.ContactResourceFtsDao
import com.ngapps.phototime.core.database.dao.locations.LocationResourceDao
import com.ngapps.phototime.core.database.dao.locations.LocationResourceFtsDao
import com.ngapps.phototime.core.database.dao.shoots.ShootResourceDao
import com.ngapps.phototime.core.database.dao.shoots.ShootResourceFtsDao
import com.ngapps.phototime.core.database.dao.tasks.TaskResourceDao
import com.ngapps.phototime.core.database.dao.tasks.TaskResourceFtsDao
import com.ngapps.phototime.core.database.model.contacts.ContactResourceEntity
import com.ngapps.phototime.core.database.model.contacts.asExternalModel
import com.ngapps.phototime.core.database.model.contacts.asFtsEntity
import com.ngapps.phototime.core.database.model.locations.LocationResourceEntity
import com.ngapps.phototime.core.database.model.locations.asExternalModel
import com.ngapps.phototime.core.database.model.locations.asFtsEntity
import com.ngapps.phototime.core.database.model.shoots.ShootResourceEntity
import com.ngapps.phototime.core.database.model.shoots.asExternalModel
import com.ngapps.phototime.core.database.model.shoots.asFtsEntity
import com.ngapps.phototime.core.database.model.tasks.TaskResourceEntity
import com.ngapps.phototime.core.database.model.tasks.asExternalModel
import com.ngapps.phototime.core.database.model.tasks.asFtsEntity
import com.ngapps.phototime.core.model.data.SearchResult
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultSearchContentsRepository @Inject constructor(
    private val locationResourceDao: LocationResourceDao,
    private val locationResourceFtsDao: LocationResourceFtsDao,
    private val contactResourceDao: ContactResourceDao,
    private val contactResourceFtsDao: ContactResourceFtsDao,
    private val taskResourceDao: TaskResourceDao,
    private val taskResourceFtsDao: TaskResourceFtsDao,
    private val shootResourceDao: ShootResourceDao,
    private val shootResourceFtsDao: ShootResourceFtsDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SearchContentsRepository {

    override suspend fun populateFtsData() {
        withContext(ioDispatcher) {
            locationResourceFtsDao.insertAll(
                locationResourceDao.getLocationResources(
                    useFilterLocationIds = false,
                )
                    .first()
                    .map(LocationResourceEntity::asFtsEntity),
            )

            contactResourceFtsDao.insertAll(
                contactResourceDao.getContactResources(
                    useFilterContactIds = false,
                )
                    .first()
                    .map(ContactResourceEntity::asFtsEntity),
            )

            taskResourceFtsDao.insertAll(
                taskResourceDao.getTaskResources(
                    useFilterTaskIds = false,
                )
                    .first()
                    .map(TaskResourceEntity::asFtsEntity),
            )

            shootResourceFtsDao.insertAll(
                shootResourceDao.getShootResources(
                    useFilterShootIds = false,
                )
                    .first()
                    .map(ShootResourceEntity::asFtsEntity),
            )

        }
    }

    override fun searchContents(searchQuery: String): Flow<SearchResult> {
        // NOTE: Surround the query by asterisks to match the query when it's in the middle of
        //  a word

        val locationResourceIds =
            locationResourceFtsDao.searchAllLocationResources("*$searchQuery*")
        val contactResourceIds = contactResourceFtsDao.searchAllContactResources("*$searchQuery*")
        val taskResourceIds = taskResourceFtsDao.searchAllTaskResources("*$searchQuery*")
        val shootResourceIds = shootResourceFtsDao.searchAllShootResources("*$searchQuery*")

        val locationResourceFlow = locationResourceIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                locationResourceDao.getLocationResources(
                    useFilterLocationIds = true,
                    filterLocationIds = it,
                )
            }

        val contactResourceFlow = contactResourceIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                contactResourceDao.getContactResources(
                    useFilterContactIds = true,
                    filterContactIds = it,
                )
            }

        val taskResourceFlow = taskResourceIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                taskResourceDao.getTaskResources(useFilterTaskIds = true, filterTaskIds = it)
            }

        val shootResourceFlow = shootResourceIds
            .mapLatest { it.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                shootResourceDao.getShootResources(useFilterShootIds = true, filterShootIds = it)
            }

        return combine(
            locationResourceFlow,
            contactResourceFlow,
            taskResourceFlow,
            shootResourceFlow,
        ) { locationResources, contactResources, taskResources, shootResources ->
            SearchResult(
                locationResources = locationResources.map { it.asExternalModel() },
                contactResources = contactResources.map { it.asExternalModel() },
                taskResources = taskResources.map { it.asExternalModel() },
                shootResources = shootResources.map { it.asExternalModel() },
            )
        }
    }

    override fun getSearchContentsCount(): Flow<Int> =
        combine(
            locationResourceFtsDao.getCount(),
            contactResourceFtsDao.getCount(),
            taskResourceFtsDao.getCount(),
            shootResourceFtsDao.getCount(),
        ) { locationResourceCount, contactResourceCount, taskResourceCount, shootResourceCount ->
            locationResourceCount + contactResourceCount + taskResourceCount + shootResourceCount
        }
}
