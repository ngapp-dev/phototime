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

package com.ngapps.phototime.sync.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.ngapps.phototime.core.data.repository.user.UserRepository
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.network.Dispatcher
import com.ngapps.phototime.core.network.SitDispatchers
import com.ngapps.phototime.core.result.DataResult
import com.ngapps.phototime.sync.initializers.UploadConstraints
import com.ngapps.phototime.sync.initializers.createForegroundInfo
import com.ngapps.phototime.sync.initializers.uploadForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Dispatcher(SitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.uploadForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {

        val id = inputData.getString(KEY_PHOTO_ID) ?: return@withContext Result.failure()
        val photoUris =
            inputData.getStringArray(KEY_PHOTO_URIS) ?: return@withContext Result.failure()

        return@withContext try {
            val progress = "Starting Download"
            setForeground(appContext.createForegroundInfo(progress))
            uploadImageFromUri(id, photoUris.map { Uri.parse(it) })
            Log.e("UploadWorker", "UploadWorker: Success")
            Result.success()
        } catch (e: Exception) {
            DataResult.Error(e.toString())
            Log.e("UploadWorker", "UploadWorker: Error")
            Result.failure()
        } catch (e: IOException) {
            DataResult.Error(e.toString())
            Log.e("UploadWorker", "UploadWorker: Error")
            Result.failure()
        }
    }


    private suspend fun uploadImageFromUri(
        id: String,
        photoUris: List<Uri>
    ): DataResult<ResponseResource> {
        return userRepository.uploadPhotos(id, photoUris).checkResultAndReturn(
            onSuccess = {
                Log.e("UploadWorker", "UploadWorker: DataResult: Success")
                DataResult.Success(it)
            },
            onError = {
                Log.e("UploadWorker", "UploadWorker: DataResult: Error")
                DataResult.Error(it)
            },
        )
    }

    companion object {
        fun startUpUploadWork(id: String?, uris: List<String>) =
            OneTimeWorkRequestBuilder<UploadDelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(UploadConstraints)
                .setInputData(UploadWorker::class.delegatedData(id, uris))
                .build()

    }
}