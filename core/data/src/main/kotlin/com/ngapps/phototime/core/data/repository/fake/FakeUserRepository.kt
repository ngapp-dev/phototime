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

import android.net.Uri
import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.model.user.asUserWithDevicesEntity
import com.ngapps.phototime.core.data.repository.user.UserRepository
import com.ngapps.phototime.core.database.model.user.asExternalModel
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.user.UserResource
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers.IO
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.fake.FakeSyncRetrofitPtNetwork
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Fake implementation of the [UserRepository] that retrieves the user resource from a JSON String.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeUserRepository @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val datasource: FakeSyncRetrofitPtNetwork,
    private val uploadNetwork: UploadPtNetworkDataSource,
) : UserRepository {

    override fun getUserResource(): Flow<UserResource> =
        flow {
            emit(
                datasource
                    .getUserResource()
                    .asUserWithDevicesEntity()
                    .asExternalModel(),
            )
        }.flowOn(ioDispatcher)

    override suspend fun uploadPhotos(
        id: String,
        photoUris: List<Uri>
    ): DataResult<ResponseResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getSaveCategories(categories: List<String>): DataResult<ResponseResource> =
        withContext(ioDispatcher) {
            try {
                val deleteResult = uploadNetwork.saveCategories(categories).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }


    override suspend fun syncWith(synchronizer: Synchronizer) = true
}
