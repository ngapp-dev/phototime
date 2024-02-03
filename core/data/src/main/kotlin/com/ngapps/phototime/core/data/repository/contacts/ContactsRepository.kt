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

import com.ngapps.phototime.core.data.Syncable
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.contact.ContactResourceQuery
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.flow.Flow

/**
 * Encapsulation class for query parameters for [ContactResource]
 */
data class ContactResourceEntityQuery(
    /**
     * Contacts ids to filter for. Null means any contact id will match.
     */
    val filterContactIds: Set<String>? = null,
    /**
     * Contacts category to filter for. Null means any contact category will match.
     */
    val filterContactCategories: Set<String>? = null,
)

interface ContactsRepository : Syncable {
    /**
     * Gets the available contacts as a stream
     */
    fun getContactResources(
        query: ContactResourceEntityQuery = ContactResourceEntityQuery(
            filterContactIds = null,
            filterContactCategories = null,
        ),
    ): Flow<List<ContactResource>>

    /**
     * Gets data for a specific contact
     */
    fun getContactResource(id: String): Flow<ContactResource>

    /**
     * Gets contact resource unique categories
     */
    fun getContactResourcesUniqueCategories(): Flow<List<String>>

    /**
     * Save contact to the backend through the api
     */
    suspend fun getSaveContact(contact: ContactResourceQuery): DataResult<ResponseResource>

    /**
     * Delete contact from the backend through the api
     */
    suspend fun getDeleteContactResource(contactId: String): DataResult<ResponseResource>

    /**
     * Delete contact from the local database
     */
    suspend fun getDeleteContactEntity(contactId: String)
}
