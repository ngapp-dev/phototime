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

package com.ngapps.phototime.sync.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.ngapps.phototime.sync.workers.UploadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UploadInitializerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UploadInitializer {

    override fun initialize(id: String?, photos: List<String>) {
        WorkManager.getInstance(context).apply {
            // NOTE: Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniqueWork(
                UploadWorkName,
                ExistingWorkPolicy.KEEP,
                UploadWorker.startUpUploadWork(id, photos),
            )
        }
    }

    // NOTE: This name should not be changed otherwise the app may have concurrent sync requests running
    companion object {
        private const val UploadWorkName = "UploadWorkName"
    }
}