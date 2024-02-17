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

package com.ngapps.phototime.core.network.fake

import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.model.auth.NetworkAuthResource
import com.ngapps.phototime.core.network.model.auth.NetworkTokenResourceQuery
import com.ngapps.phototime.core.network.model.contact.NetworkContactResourceQuery
import com.ngapps.phototime.core.network.model.location.NetworkLocationResourceQuery
import com.ngapps.phototime.core.network.model.location.NetworkSearchAutocomplete
import com.ngapps.phototime.core.network.model.response.NetworkResponseResource
import com.ngapps.phototime.core.network.model.shoot.NetworkShootResourceQuery
import com.ngapps.phototime.core.network.model.signin.NetworkGoogleSignInResourceQuery
import com.ngapps.phototime.core.network.model.signin.NetworkSignInResourceQuery
import com.ngapps.phototime.core.network.model.signup.NetworkSignUpResourceQuery
import com.ngapps.phototime.core.network.model.task.NetworkTaskResourceQuery
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class FakeUploadRetrofitPtNetwork @Inject constructor(

) : UploadPtNetworkDataSource {
    override suspend fun getSignIn(query: NetworkSignInResourceQuery): NetworkAuthResource {
        TODO("Not yet implemented")
    }

    override suspend fun getGoogleSignIn(query: NetworkGoogleSignInResourceQuery): NetworkAuthResource {
        TODO("Not yet implemented")
    }

    override suspend fun refreshToken(query: NetworkTokenResourceQuery): Response<NetworkAuthResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getSignUp(query: NetworkSignUpResourceQuery): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPhotos(
        id: RequestBody,
        photos: List<MultipartBody.Part>
    ): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun saveLocation(query: NetworkLocationResourceQuery): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocation(id: String): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun saveContact(query: NetworkContactResourceQuery): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun deleteContact(id: String): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(query: NetworkTaskResourceQuery): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(id: String): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun saveShoot(query: NetworkShootResourceQuery): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun deleteShoot(id: String): NetworkResponseResource {
        TODO("Not yet implemented")
    }

    override suspend fun searchAutocomplete(query: String): List<NetworkSearchAutocomplete> {
        TODO("Not yet implemented")
    }

    override suspend fun saveCategories(categories: List<String>): NetworkResponseResource {
        TODO("Not yet implemented")
    }
}