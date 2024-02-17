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

import android.content.Context
import android.util.Log
import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.model.contact.asEntity
import com.ngapps.phototime.core.data.model.contact.asNetworkModel
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.repository.contacts.ContactResourceEntityQuery
import com.ngapps.phototime.core.data.repository.contacts.ContactsRepository
import com.ngapps.phototime.core.database.model.contacts.ContactResourceEntity
import com.ngapps.phototime.core.database.model.contacts.asExternalModel
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.contact.ContactResourceQuery
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.base.handleThrowable
import com.ngapps.phototime.core.network.fake.FakeSyncRetrofitPtNetwork
import com.ngapps.phototime.core.network.model.contact.NetworkContactResource
import com.ngapps.phototime.core.result.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fake implementation of the [ContactsRepository] that retrieves the contact resources from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeContactsRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: FakeSyncRetrofitPtNetwork,
    private val uploadNetwork: UploadPtNetworkDataSource,
    @ApplicationContext private val context: Context,
) : ContactsRepository {

    override fun getContactResources(
        query: ContactResourceEntityQuery,
    ): Flow<List<ContactResource>> =
        flow {
            emit(
                datasource
                    .getContactResources()
                    .filter { networkContactResource ->
                        /**
                         * Filter out any contact resources which don't match the current query.
                         * If no query parameter filterContactsIds is specified
                         * then the contact resource is returned.
                         * */
                        listOfNotNull(
                            true,
                            query.filterContactIds?.contains(networkContactResource.id),
                        )
                            .all(true::equals)
                    }
                    .map(NetworkContactResource::asEntity)
                    .map(ContactResourceEntity::asExternalModel),

            )
            Log.e("asd", "asd")
        }.flowOn(ioDispatcher)

    override fun getContactResource(id: String): Flow<ContactResource> =
        getContactResources().map { it.first { contactResource -> contactResource.id == id } }


    override fun getContactResourcesUniqueCategories(): Flow<List<String>> =
        getContactResourcesUniqueCategories()

    override suspend fun getSaveContact(contact: ContactResourceQuery): DataResult<ResponseResource> =
        withContext(ioDispatcher) {
            try {
                val response =
                    uploadNetwork.saveContact(contact.asNetworkModel()).asExternalModel()
                DataResult.Success(response)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteContactResource(contactId: String): DataResult<ResponseResource> =
        withContext(ioDispatcher) {
            try {
                val deleteResult = uploadNetwork.deleteContact(contactId).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }

    override suspend fun getDeleteContactEntity(contactId: String) {

    }


    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
