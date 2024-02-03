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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.ngapps.phototime.sync.R

const val UPLOAD_NOTIFICATION_ID = 99
private const val UPLOAD_NOTIFICATION_CHANNEL_ID  = "UploadNotificationChannel"

// NOTE: All sync work needs an internet connectionS
val UploadConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

/**
 * Foreground information for sync on lower API levels when sync workers are being
 * run with a foreground service
 */
fun Context.uploadForegroundInfo() = ForegroundInfo(
    UPLOAD_NOTIFICATION_ID,
    uploadWorkNotification(),
)

/**
 * Notification displayed on lower API levels when sync workers are being
 * run with a foreground service
 */
private fun Context.uploadWorkNotification(): Notification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            UPLOAD_NOTIFICATION_CHANNEL_ID,
            getString(R.string.upload_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.upload_notification_channel_description)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    return NotificationCompat.Builder(
        this,
        UPLOAD_NOTIFICATION_CHANNEL_ID,
    )
        .setSmallIcon(
            com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
        )
        .setContentTitle(getString(R.string.upload_notification_title))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
}

fun Context.createForegroundInfo(progress: String): ForegroundInfo {
    val title = "Upload channel"

    // Create a Notification channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            UPLOAD_NOTIFICATION_CHANNEL_ID,
            getString(R.string.upload_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(R.string.upload_notification_channel_description)
        }
        // Register the channel with the system
        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(this, UPLOAD_NOTIFICATION_CHANNEL_ID)
        .setContentTitle(title)
        .setTicker(title)
        .setContentText(progress)
        .setSmallIcon(com.ngapps.phototime.core.common.R.drawable.ic_pt_notification)
        .setOngoing(true)
        // Add the cancel action to the notification which can
        // be used to cancel the worker
        .build()

    return ForegroundInfo(2, notification)
}
