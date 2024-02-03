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

package com.ngapps.phototime.core.data.repository.user

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.ngapps.phototime.core.data.Synchronizer
import com.ngapps.phototime.core.data.changeListSync
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.model.user.asEntity
import com.ngapps.phototime.core.data.model.user.asUserWithDevicesEntity
import com.ngapps.phototime.core.database.dao.user.UserResourceDao
import com.ngapps.phototime.core.database.model.user.UserResourceEntityWithDevices
import com.ngapps.phototime.core.database.model.user.asExternalModel
import com.ngapps.phototime.core.datastore.ChangeListVersions
import com.ngapps.phototime.core.datastore.PtPreferencesDataSource
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.user.UserResource
import com.ngapps.phototime.core.network.SyncPtNetworkDataSource
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleException
import com.ngapps.phototime.core.network.base.handleThrowable
import com.ngapps.phototime.core.network.model.user.NetworkCategoriesResource
import com.ngapps.phototime.core.network.model.user.NetworkGoogleTokenResource
import com.ngapps.phototime.core.network.model.user.NetworkTokenResource
import com.ngapps.phototime.core.network.model.user.NetworkUserResource
import com.ngapps.phototime.core.network.model.util.InputStreamRequestBody
import com.ngapps.phototime.core.notifications.Notifier
import com.ngapps.phototime.core.result.DataResult
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [UserRepository].
 * Reads are exclusively from local storage to support offline access.
 */
class OfflineFirstUserRepository @Inject constructor(
    private val ptPreferencesDataSource: PtPreferencesDataSource,
    private val userResourceDao: UserResourceDao,
    private val syncNetwork: SyncPtNetworkDataSource,
    private val uploadNetwork: UploadPtNetworkDataSource,
    private val notifier: Notifier,
    @ApplicationContext private val context: Context,
) : UserRepository {

    override fun getUserResource(): Flow<UserResource> =
        userResourceDao.getUserResourceWithDevices().filterNotNull()
            .map(UserResourceEntityWithDevices::asExternalModel)

    override suspend fun uploadPhotos(
        id: String,
        photoUris: List<Uri>
    ): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val photoParts = ArrayList<MultipartBody.Part>()

                photoUris.forEachIndexed { index, photo ->
                    val resolver = getApplication(context).contentResolver
                    val doc = DocumentFile.fromSingleUri(getApplication(context), photo)!!
                    val type = (doc.type ?: DEFAULT_TYPE).toMediaType()
                    val contentPart = InputStreamRequestBody(type, resolver, photo)

                    val imagePart = MultipartBody.Part.createFormData(
                        "photos",
                        "photo_$index",
                        contentPart,
                    )
                    photoParts.add(imagePart)
                }
                val response = uploadNetwork.uploadPhotos(
                    id = id.toRequestBody(),
                    photos = photoParts,
                ).asExternalModel()
                DataResult.Success(response)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getSaveCategories(categories: List<String>): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val deleteResult = uploadNetwork.saveCategories(categories).asExternalModel()
                DataResult.Success(deleteResult)
            } catch (e: Exception) {
                DataResult.Error(e.handleException().message ?: "Unknown")
            }
        }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::userResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                syncNetwork.getUserResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion ->
                copy(userResourceVersion = latestVersion)
            },
            modelDeleter = { userResourceDao.deleteUserResource() },
            modelUpdater = { changedIds ->
//                val existingLocationResourceIdsThatHaveChanged =
//                    userResourceDao.getUserResourceWithDevices(
//                        useFilterLocationIds = true,
//                        filterLocationIds = changedIds.toSet(),
//                    )
//                        .first()
//                        .map { it.id }
//                        .toSet()
                if (isFirstSync) {
                    userResourceDao.insertOrIgnoreUserResource(
                        NetworkUserResource(
                            id = "",
                            username = "",
                            email = "",
                            lastLogin = "",
                            created = "",
                            isActive = true,
                            audience = "",
                            token = NetworkTokenResource(NetworkGoogleTokenResource("", "")),
                            devices = emptyList(),
                            categories = NetworkCategoriesResource(
                                contact = emptyList(),
                                task = emptyList(),
                                location = emptyList(),
                                connectTo = emptyList(),
                            ),
                        ).asEntity(),
                    )
                }

                // Obtain the tasks resources which have changed from the network and upsert them locally
                changedIds.chunked(SYNC_BATCH_SIZE).forEach { chunkedIds ->
                    val networkUserResource =
                        syncNetwork.getUserResource(id = chunkedIds.first())
                    userResourceDao.upsertUserResources(
                        userResourceEntity = networkUserResource.asUserWithDevicesEntity(),
                    )
                }

                val addedUserResource = userResourceDao.getUserResourceWithDevices()
                    .first()
                    .asExternalModel()

                notifier.postUserNotifications(
                    userResource = addedUserResource,
                )
            },
        )
    }

    companion object {
        // NOTE: Heuristic value to optimize for serialization and deserialization cost on client and server
        //  for each task resource batch.
        private const val SYNC_BATCH_SIZE = 40
        private const val DEFAULT_TYPE = "application/octet-stream"
    }
}