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

package com.ngapps.phototime.sync.status

import android.content.Context
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.ngapps.phototime.core.data.util.SyncManager
import com.ngapps.phototime.sync.initializers.SyncWorkName
import com.ngapps.phototime.sync.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import javax.inject.Inject

/**
 * [SyncManager] backed by [WorkInfo] from [WorkManager]
 */
class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkLiveData(SyncWorkName)
            .map(MutableList<WorkInfo>::anyRunning)
            .asFlow()
            .conflate()

    override fun requestSync() {
        val workManager = WorkManager.getInstance(context)
        // NOTE: Run sync on app startup and ensure only one sync worker runs at any time
        workManager.enqueueUniqueWork(
            SyncWorkName,
//            ExistingWorkPolicy.KEEP,
            ExistingWorkPolicy.REPLACE,
            SyncWorker.startUpSyncWork(),
        )
        Log.d("WorkManagerSyncManager", "requestSync")
    }
}

private val List<WorkInfo>.anyRunning get() = any { it.state == State.RUNNING }
