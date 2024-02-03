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

package com.ngapps.phototime.core.data.repository.contacts

import android.content.Context
import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.changeListSync
import com.ngapps.phototime.core.data.model.contact.asEntity
import com.ngapps.phototime.core.data.model.contact.asNetworkModel
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.database.dao.contacts.ContactResourceDao
import com.ngapps.phototime.core.database.model.contacts.ContactResourceEntity
import com.ngapps.phototime.core.database.model.contacts.asExternalModel
import com.ngapps.phototime.core.datastore.ChangeListVersions
import com.ngapps.phototime.core.datastore.PtPreferencesDataSource
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.contact.ContactResourceQuery
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.base.handleThrowable
import com.ngapps.phototime.core.network.model.contact.NetworkContactResource
import com.ngapps.phototime.core.notifications.Notifier
import com.ngapps.phototime.core.result.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [ContactsRepository].
 * Reads are exclusively from local storage to support offline access.
 */

class OfflineFirstContactsRepository @Inject constructor(
    private val ptPreferencesDataSource: PtPreferencesDataSource,
    private val contactResourceDao: ContactResourceDao,
    private val syncNetwork: SyncPtNetworkDataSource,
    private val uploadNetwork: UploadPtNetworkDataSource,
    private val notifier: Notifier,
    @ApplicationContext private val context: Context,
) : ContactsRepository {

    override fun getContactResources(
        query: ContactResourceEntityQuery,
    ): Flow<List<ContactResource>> = contactResourceDao.getContactResources(
        useFilterContactIds = query.filterContactIds != null,
        filterContactIds = query.filterContactIds ?: emptySet(),
    )
        .map { it.map(ContactResourceEntity::asExternalModel) }

    override fun getContactResource(id: String): Flow<ContactResource> =
        contactResourceDao.getContactResource(id).map { it.asExternalModel() }

    override fun getContactResourcesUniqueCategories(): Flow<List<String>> =
        contactResourceDao.getContactResourcesUniqueCategories()

    override suspend fun getSaveContact(contact: ContactResourceQuery): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    uploadNetwork.saveContact(contact.asNetworkModel()).asExternalModel()
                DataResult.Success(response)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteContactResource(contactId: String): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val deleteResult = uploadNetwork.deleteContact(contactId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }


    override suspend fun getDeleteContactEntity(contactId: String) {
        contactResourceDao.deleteContactResources(listOf(contactId))
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::contactResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                syncNetwork.getContactResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(contactResourceVersion = latestVersion)
            },
            modelDeleter = contactResourceDao::deleteContactResources,
            modelUpdater = { changedIds ->
                // TODO: Make this more efficient, there is no need to retrieve populated
                //  contact resources when all that's needed are the ids
                val existingContactResourceIdsThatHaveChanged =
                    contactResourceDao.getContactResources(
                        useFilterContactIds = true,
                        filterContactIds = changedIds.toSet(),
                    )
                        .first()
                        .map { it.id }
                        .toSet()
                // No need to retrieve anything if notifications won't be sent

                if (isFirstSync) {
                    // When we first retrieve contacts, mark everything viewed, so that we aren't
                    // overwhelmed with all historical contacts.
//                    sitPreferencesDataSource.setContactResourcesCompleted(changedIds, true)
                }

                // Obtain the contacts resources which have changed from the network and upsert them locally
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkContactResources = syncNetwork.getContactResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!
                    contactResourceDao.upsertContactResources(
                        contactResourceEntities = networkContactResources.map(
                            NetworkContactResource::asEntity,
                        ),
                    )
                }

                val addedContactResources = contactResourceDao.getContactResources(
                    useFilterContactIds = true,
                    filterContactIds = changedIds.toSet() - existingContactResourceIdsThatHaveChanged,
                )
                    .first()
                    .map(ContactResourceEntity::asExternalModel)

                if (addedContactResources.isNotEmpty()) {
                    notifier.postContactNotifications(
                        contactResources = addedContactResources,
                    )
                }
            },
        )
    }

    companion object {
        private const val SYNC_BATCH_SIZE = 40
    }
}