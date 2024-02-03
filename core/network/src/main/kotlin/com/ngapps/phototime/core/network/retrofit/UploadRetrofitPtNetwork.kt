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
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.model.auth.NetworkAuthResource
import com.ngapps.phototime.core.network.model.auth.NetworkTokenResourceQuery
import com.ngapps.phototime.core.network.model.contact.NetworkContactResourceQuery
import com.ngapps.phototime.core.network.model.location.NetworkLocationResourceQuery
import com.ngapps.phototime.core.network.model.location.NetworkSearchAutocomplete
import com.ngapps.phototime.core.network.model.response.NetworkResponse
import com.ngapps.phototime.core.network.model.response.NetworkResponseResource
import com.ngapps.phototime.core.network.model.shoot.NetworkShootResourceQuery
import com.ngapps.phototime.core.network.model.signin.NetworkGoogleSignInResourceQuery
import com.ngapps.phototime.core.network.model.signin.NetworkSignInResourceQuery
import com.ngapps.phototime.core.network.model.signup.NetworkSignUpResourceQuery
import com.ngapps.phototime.core.network.model.task.NetworkTaskResourceQuery
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

/**
 * Retrofit API declaration for Pt Network API
 */

interface UploadPtNetworkApi {

    @POST("auth/token")
    suspend fun getSignIn(
        @Body query: NetworkSignInResourceQuery,
    ): NetworkResponse<NetworkAuthResource>

    @POST("auth/token")
    suspend fun getGoogleSignIn(
        @Body query: NetworkGoogleSignInResourceQuery,
    ): NetworkResponse<NetworkAuthResource>

    @PUT("auth/token")
    suspend fun refreshToken(
        @Body query: NetworkTokenResourceQuery
    ): retrofit2.Response<NetworkAuthResource>

    @POST("auth/user")
    suspend fun getSignUp(
        @Body query: NetworkSignUpResourceQuery,
    ): NetworkResponse<NetworkResponseResource>

    @Multipart
    @POST("photos")
    suspend fun uploadPhotos(
        @Part("photo_id") id: RequestBody,
        @Part photos: List<MultipartBody.Part>
    ): NetworkResponse<NetworkResponseResource>

    @POST("location")
    suspend fun saveLocation(
        @Body query: NetworkLocationResourceQuery
    ): NetworkResponse<NetworkResponseResource>

    @DELETE("location/{id}")
    suspend fun deleteLocation(
        @Path("id") id: String,
    ): NetworkResponse<NetworkResponseResource>

    @POST("contact")
    suspend fun saveContact(
        @Body query: NetworkContactResourceQuery,
    ): NetworkResponse<NetworkResponseResource>

    @DELETE("contact/{id}")
    suspend fun deleteContact(
        @Path("id") id: String,
    ): NetworkResponse<NetworkResponseResource>

    @POST("contact")
    suspend fun saveTask(
        @Body query: NetworkTaskResourceQuery,
    ): NetworkResponse<NetworkResponseResource>

    @DELETE("task/{id}")
    suspend fun deleteTask(
        @Path("id") id: String,
    ): NetworkResponse<NetworkResponseResource>

    @POST("contact")
    suspend fun saveShoot(
        @Body query: NetworkShootResourceQuery,
    ): NetworkResponse<NetworkResponseResource>

    @DELETE("shoot/{id}")
    suspend fun deleteShoot(
        @Path("id") id: String,
    ): NetworkResponse<NetworkResponseResource>

    @GET("/location/autocomplete")
    suspend fun searchAutocomplete(
        @Query("query") query: String
    ): NetworkResponse<List<NetworkSearchAutocomplete>>

    @PUT("/auth/user")
    suspend fun saveCategories(
        @Query("categories") categories: LocationCategories
    ): NetworkResponse<NetworkResponseResource>

}

private const val SIT_BASE_URL = "${BuildConfig.BACKEND_URL}/api/"

/**
 * [Retrofit] backed [UploadPtNetworkDataSource]
 */

class UploadRetrofitPtNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: Call.Factory,
) : UploadPtNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl(SIT_BASE_URL)
        .callFactory(okhttpCallFactory)
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(UploadPtNetworkApi::class.java)

    override suspend fun getSignIn(query: NetworkSignInResourceQuery): NetworkAuthResource =
        networkApi.getSignIn(query).result

    override suspend fun getGoogleSignIn(query: NetworkGoogleSignInResourceQuery): NetworkAuthResource =
        networkApi.getGoogleSignIn(query).result

    override suspend fun refreshToken(query: NetworkTokenResourceQuery): retrofit2.Response<NetworkAuthResource> =
        networkApi.refreshToken(query)

    override suspend fun getSignUp(query: NetworkSignUpResourceQuery): NetworkResponseResource =
        networkApi.getSignUp(query).result

    override suspend fun uploadPhotos(
        id: RequestBody,
        photos: List<MultipartBody.Part>
    ): NetworkResponseResource =
        networkApi.uploadPhotos(id, photos).result

    override suspend fun saveLocation(query: NetworkLocationResourceQuery): NetworkResponseResource =
        networkApi.saveLocation(query).result

    override suspend fun deleteLocation(id: String): NetworkResponseResource =
        networkApi.deleteLocation(id).result

    override suspend fun saveContact(query: NetworkContactResourceQuery): NetworkResponseResource =
        networkApi.saveContact(query).result

    override suspend fun deleteContact(id: String): NetworkResponseResource =
        networkApi.deleteContact(id).result

    override suspend fun saveTask(query: NetworkTaskResourceQuery): NetworkResponseResource =
        networkApi.saveTask(query).result

    override suspend fun deleteTask(id: String): NetworkResponseResource =
        networkApi.deleteTask(id).result

    override suspend fun saveShoot(query: NetworkShootResourceQuery): NetworkResponseResource =
        networkApi.saveShoot(query).result

    override suspend fun deleteShoot(id: String): NetworkResponseResource =
        networkApi.deleteShoot(id).result

    override suspend fun searchAutocomplete(query: String): List<NetworkSearchAutocomplete> =
        networkApi.searchAutocomplete(query).result

    override suspend fun saveCategories(categories: List<String>): NetworkResponseResource =
        networkApi.saveCategories(LocationCategories(categories = LocationCategories.Categories(location = categories))).result
}
@Serializable
data class LocationCategories(
    val categories: Categories
) {
    @Serializable
    data class Categories(
        val location: List<String>
    )
}