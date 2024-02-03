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

package com.ngapps.phototime.core.network

import com.ngapps.phototime.core.network.model.NetworkChangeList
import com.ngapps.phototime.core.network.model.contact.NetworkContactResource
import com.ngapps.phototime.core.network.model.location.NetworkLocationResource
import com.ngapps.phototime.core.network.model.moodboard.NetworkMoodboardResource
import com.ngapps.phototime.core.network.model.shoot.NetworkShootResource
import com.ngapps.phototime.core.network.model.task.NetworkTaskResource
import com.ngapps.phototime.core.network.model.user.NetworkUserResource

/**
 * Interface representing network calls to the Pt backend synchronization and downloads
 */
interface SyncPtNetworkDataSource {

    suspend fun getUserResource(id: String? = null): NetworkUserResource

    suspend fun getUserResourceChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getMoodboardResources(ids: List<String>? = null): List<NetworkMoodboardResource>

    suspend fun getMoodboardResourceChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getShootResources(ids: List<String>? = null): List<NetworkShootResource>

    suspend fun getShootResourceChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getContactResources(ids: List<String>? = null): List<NetworkContactResource>

    suspend fun getContactResourceChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getLocationResources(ids: List<String>? = null): List<NetworkLocationResource>

    suspend fun getLocationResourceChangeList(after: Int? = null): List<NetworkChangeList>

    suspend fun getTaskResources(ids: List<String>? = null): List<NetworkTaskResource>

    suspend fun getTaskResourceChangeList(after: Int? = null): List<NetworkChangeList>
}
