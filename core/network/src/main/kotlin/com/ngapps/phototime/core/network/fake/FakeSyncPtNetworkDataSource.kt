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

@file:OptIn(ExperimentalSerializationApi::class)

package com.ngapps.phototime.core.network.fake

import JvmUnitTestFakeAssetManager
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.model.NetworkChangeList
import com.ngapps.phototime.core.network.model.contact.NetworkContactResource
import com.ngapps.phototime.core.network.model.location.NetworkLocationResource
import com.ngapps.phototime.core.network.model.moodboard.NetworkMoodboardResource
import com.ngapps.phototime.core.network.model.shoot.NetworkShootResource
import com.ngapps.phototime.core.network.model.task.NetworkTaskResource
import com.ngapps.phototime.core.network.model.user.NetworkUserResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

/**
 * [SyncPtNetworkDataSource] implementation that provides static news resources to aid development
 */
class FakeSyncPtNetworkDataSource @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: FakeAssetManager = JvmUnitTestFakeAssetManager,
) : SyncPtNetworkDataSource {

    companion object {
        private const val MOODBOARDS_ASSET = "moodboards.json"
        private const val SHOOTS_ASSET = "shoots.json"
        private const val TASKS_ASSET = "tasks.json"
        private const val LOCATIONS_ASSET = "locations.json"
        private const val CONTACTS_ASSET = "contacts.json"
        private const val USER_ASSET = "user.json"
    }

    override suspend fun getUserResource(id: String?): NetworkUserResource =
        withContext(ioDispatcher) {
            assets.open(USER_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getUserResourceChangeList(after: Int?): List<NetworkChangeList> =
        listOf(getUserResource()).mapToChangeList(NetworkUserResource::id)

    override suspend fun getMoodboardResources(ids: List<String>?): List<NetworkMoodboardResource> =
        withContext(ioDispatcher) {
            assets.open(MOODBOARDS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getMoodboardResourceChangeList(after: Int?): List<NetworkChangeList> =
        getMoodboardResources().mapToChangeList(NetworkMoodboardResource::id)

    override suspend fun getShootResources(ids: List<String>?): List<NetworkShootResource> =
        withContext(ioDispatcher) {
            assets.open(SHOOTS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getShootResourceChangeList(after: Int?): List<NetworkChangeList> =
        getShootResources().mapToChangeList(NetworkShootResource::id)

    override suspend fun getContactResources(ids: List<String>?): List<NetworkContactResource> =
        withContext(ioDispatcher) {
            assets.open(CONTACTS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getContactResourceChangeList(after: Int?): List<NetworkChangeList> =
        getContactResources().mapToChangeList(NetworkContactResource::id)


    override suspend fun getLocationResources(ids: List<String>?): List<NetworkLocationResource> =
        withContext(ioDispatcher) {
            assets.open(LOCATIONS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getLocationResourceChangeList(after: Int?): List<NetworkChangeList> =
        getLocationResources().mapToChangeList(NetworkLocationResource::id)

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getTaskResources(ids: List<String>?): List<NetworkTaskResource> =
        withContext(ioDispatcher) {
            assets.open(TASKS_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun getTaskResourceChangeList(after: Int?): List<NetworkChangeList> =
        getTaskResources().mapToChangeList(NetworkTaskResource::id)
}

/**
 * Converts a list of [T] to change list of all the items in it where [idGetter] defines the
 * [NetworkChangeList.id]
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String,
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter(item),
        changeListVersion = index,
        isDelete = false,
    )
}
