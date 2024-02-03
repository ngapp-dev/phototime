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

package com.ngapps.phototime.core.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ngapps.phototime.core.network.BuildConfig
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.model.NetworkChangeList
import com.ngapps.phototime.core.network.model.contact.NetworkContactResource
import com.ngapps.phototime.core.network.model.location.NetworkLocationResource
import com.ngapps.phototime.core.network.model.moodboard.NetworkMoodboardResource
import com.ngapps.phototime.core.network.model.response.NetworkResponse
import com.ngapps.phototime.core.network.model.shoot.NetworkShootResource
import com.ngapps.phototime.core.network.model.task.NetworkTaskResource
import com.ngapps.phototime.core.network.model.user.NetworkUserResource
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for Pt Network API
 */

private interface SyncSitNetworkApi {

    @GET(value = "auth/user")
    suspend fun getUser(
        @Query("id") id: String?,
    ): NetworkResponse<NetworkUserResource>

    @GET(value = "changelists/user")
    suspend fun getUserResourcesChangeList(
        @Query("after") after: Int?,
    ): NetworkResponse<List<NetworkChangeList>>

    @GET(value = "moodboard/moodboards")
    suspend fun getMoodboards(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkMoodboardResource>>

    @GET(value = "changelists/moodboard")
    suspend fun getMoodboardResourcesChangeList(
        @Query("after") after: Int?,
    ): NetworkResponse<List<NetworkChangeList>>

    @GET(value = "shoot/shoots")
    suspend fun getShoots(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkShootResource>>

    @GET(value = "changelists/shoot")
    suspend fun getShootResourcesChangeList(
        @Query("after") after: Int?,
    ): NetworkResponse<List<NetworkChangeList>>

    @GET(value = "contact/contacts")
    suspend fun getContacts(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkContactResource>>

    @GET(value = "changelists/contact")
    suspend fun getContactResourcesChangeList(
        @Query("after") after: Int?,
    ): NetworkResponse<List<NetworkChangeList>>

    @GET(value = "location/locations")
    suspend fun getLocations(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkLocationResource>>

    @GET(value = "changelists/location")
    suspend fun getLocationResourcesChangeList(
        @Query("after") after: Int?,
    ): NetworkResponse<List<NetworkChangeList>>

    @GET(value = "task/tasks")
    suspend fun getTasks(
        @Query("id") ids: List<String>?,
    ): NetworkResponse<List<NetworkTaskResource>>

    @GET(value = "changelists/task")
    suspend fun getTaskResourcesChangeList(
        @Query("after") after: Int?,
    ): NetworkResponse<List<NetworkChangeList>>
}

private const val SIT_BASE_URL = "${BuildConfig.BACKEND_URL}/api/"

/**
 * [Retrofit] backed [SyncPtNetworkDataSource]
 */
@Singleton
class SyncRetrofitPtNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: Call.Factory,
) : SyncPtNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl(SIT_BASE_URL)
        .callFactory(okhttpCallFactory)
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(SyncSitNetworkApi::class.java)

    override suspend fun getUserResource(id: String?): NetworkUserResource =
        networkApi.getUser(id = id).result

    override suspend fun getUserResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getUserResourcesChangeList(after = after).result

    override suspend fun getMoodboardResources(ids: List<String>?): List<NetworkMoodboardResource> =
        networkApi.getMoodboards(ids = ids).result

    override suspend fun getMoodboardResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getMoodboardResourcesChangeList(after = after).result

    override suspend fun getShootResources(ids: List<String>?): List<NetworkShootResource> =
        networkApi.getShoots(ids = ids).result

    override suspend fun getShootResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getShootResourcesChangeList(after = after).result

    override suspend fun getContactResources(ids: List<String>?): List<NetworkContactResource> =
        networkApi.getContacts(ids = ids).result

    override suspend fun getContactResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getContactResourcesChangeList(after = after).result

    override suspend fun getLocationResources(ids: List<String>?): List<NetworkLocationResource> =
        networkApi.getLocations(ids = ids).result

    override suspend fun getLocationResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getLocationResourcesChangeList(after = after).result

    override suspend fun getTaskResources(ids: List<String>?): List<NetworkTaskResource> =
        networkApi.getTasks(ids = ids).result

    override suspend fun getTaskResourceChangeList(after: Int?): List<NetworkChangeList> =
        networkApi.getTaskResourcesChangeList(after = after).result
}
