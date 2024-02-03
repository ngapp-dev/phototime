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

package com.ngapps.phototime.core.notifications

import android.Manifest.permission
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.moodboard.MoodboardResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.model.data.user.UserResource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val MAX_NUM_NOTIFICATIONS = 5
private const val TARGET_ACTIVITY_NAME = "com.ngapps.phototime.MainActivity"

private const val TASK_NOTIFICATION_REQUEST_CODE = 0
private const val TASK_NOTIFICATION_SUMMARY_ID = 1
private const val TASK_NOTIFICATION_CHANNEL_ID = ""
private const val TASK_NOTIFICATION_GROUP = "TASK_NOTIFICATIONS"

private const val LOCATION_NOTIFICATION_REQUEST_CODE = 2
private const val LOCATION_NOTIFICATION_SUMMARY_ID = 3
private const val LOCATION_NOTIFICATION_CHANNEL_ID = ""
private const val LOCATION_NOTIFICATION_GROUP = "LOCATION_NOTIFICATIONS"

private const val CONTACT_NOTIFICATION_REQUEST_CODE = 4
private const val CONTACT_NOTIFICATION_SUMMARY_ID = 5
private const val CONTACT_NOTIFICATION_CHANNEL_ID = ""
private const val CONTACT_NOTIFICATION_GROUP = "CONTACT_NOTIFICATIONS"

private const val SHOOT_NOTIFICATION_REQUEST_CODE = 6
private const val SHOOT_NOTIFICATION_SUMMARY_ID = 7
private const val SHOOT_NOTIFICATION_CHANNEL_ID = ""
private const val SHOOT_NOTIFICATION_GROUP = "SHOOT_NOTIFICATIONS"

private const val MOODBOARD_NOTIFICATION_REQUEST_CODE = 8
private const val MOODBOARD_NOTIFICATION_SUMMARY_ID = 9
private const val MOODBOARD_NOTIFICATION_CHANNEL_ID = ""
private const val MOODBOARD_NOTIFICATION_GROUP = "MOODBOARD_NOTIFICATIONS"

private const val USER_NOTIFICATION_REQUEST_CODE = 10
private const val USER_NOTIFICATION_SUMMARY_ID = 11
private const val USER_NOTIFICATION_CHANNEL_ID = ""
private const val USER_NOTIFICATION_GROUP = "USER_NOTIFICATIONS"

// FIXME: Change to valuable data
private const val DEEP_LINK_SCHEME_AND_HOST = "https://www.ngapps.phototime.com"
private const val HOME_PATH = "home"
private const val TASKS_PATH = "tasks"
private const val LOCATIONS_PATH = "locations"
private const val CONTACTS_PATH = "contacts"

/**
 * Implementation of [Notifier] that displays notifications in the system tray.
 */
@Singleton
class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {

    override fun postTaskNotifications(
        taskResources: List<TaskResource>
    ) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val truncatedTaskResources = taskResources
            .take(MAX_NUM_NOTIFICATIONS)

        val taskNotifications = truncatedTaskResources
            .map { taskResource ->
                createTaskNotification {
                    setSmallIcon(
                        com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                    )
                        .setContentTitle(taskResource.title)
                        .setContentText(taskResource.description)
                        .setContentIntent(taskPendingIntent(taskResource))
                        .setGroup(TASK_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }
        val summaryNotification = createTaskNotification {
            val title = getString(
                R.string.task_notification_group_summary,
                truncatedTaskResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(
                    com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                )
                // Build summary info into InboxStyle template.
                .setStyle(taskNotificationStyle(truncatedTaskResources, title))
                .setGroup(TASK_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // NOTE: Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        taskNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedTaskResources[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(TASK_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    override fun postLocationNotifications(
        locationResources: List<LocationResource>
    ) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val truncatedLocationResources = locationResources
            .take(MAX_NUM_NOTIFICATIONS)

        val locationNotifications = truncatedLocationResources
            .map { locationResource ->
                createLocationNotification {
                    setSmallIcon(
                        com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                    )
                        .setContentTitle(locationResource.title)
                        .setContentText(locationResource.description)
                        .setContentIntent(locationPendingIntent(locationResource))
                        .setGroup(LOCATION_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }
        val summaryNotification = createLocationNotification {
            val title = getString(
                R.string.location_notification_group_summary,
                truncatedLocationResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(
                    com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                )
                // Build summary info into InboxStyle template.
                .setStyle(locationNotificationStyle(truncatedLocationResources, title))
                .setGroup(LOCATION_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // NOTE: Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        locationNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedLocationResources[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(LOCATION_NOTIFICATION_SUMMARY_ID, summaryNotification)

    }

    override fun postContactNotifications(
        contactResources: List<ContactResource>
    ) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val truncatedContactResources = contactResources
            .take(MAX_NUM_NOTIFICATIONS)

        val contactNotifications = truncatedContactResources
            .map { contactResource ->
                createContactNotification {
                    setSmallIcon(
                        com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                    )
                        .setContentTitle(contactResource.name)
                        .setContentText(contactResource.description)
                        .setContentIntent(contactPendingIntent(contactResource))
                        .setGroup(CONTACT_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }
        val summaryNotification = createLocationNotification {
            val title = getString(
                R.string.contact_notification_group_summary,
                truncatedContactResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(
                    com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                )
                // Build summary info into InboxStyle template.
                .setStyle(contactNotificationStyle(truncatedContactResources, title))
                .setGroup(CONTACT_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // NOTE: Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        contactNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedContactResources[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(CONTACT_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    override fun postShootNotifications(
        shootResources: List<ShootResource>
    ) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val truncatedShootResources = shootResources
            .take(MAX_NUM_NOTIFICATIONS)

        val shootNotifications = truncatedShootResources
            .map { shootResource ->
                createShootNotification {
                    setSmallIcon(
                        com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                    )
                        .setContentTitle(shootResource.title)
                        .setContentText(shootResource.description)
                        .setContentIntent(shootPendingIntent(shootResource))
                        .setGroup(SHOOT_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }
        val summaryNotification = createShootNotification {
            val title = getString(
                R.string.shoot_notification_group_summary,
                truncatedShootResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(
                    com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                )
                // Build summary info into InboxStyle template.
                .setStyle(shootNotificationStyle(truncatedShootResources, title))
                .setGroup(SHOOT_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // NOTE: Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        shootNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedShootResources[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(SHOOT_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    override fun postMoodboardNotifications(
        moodboardResources: List<MoodboardResource>
    ) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val truncatedMoodboardResources = moodboardResources
            .take(MAX_NUM_NOTIFICATIONS)

        val moodboardNotifications = truncatedMoodboardResources
            .map { moodboardResource ->
                createMoodboardNotification {
                    setSmallIcon(
                        com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                    )
                        .setContentTitle(moodboardResource.id)
                        .setContentIntent(moodboardPendingIntent(moodboardResource))
                        .setGroup(MOODBOARD_NOTIFICATION_GROUP)
                        .setAutoCancel(true)
                }
            }
        val summaryNotification = createMoodboardNotification {
            val title = getString(
                R.string.moodboard_notification_group_summary,
                truncatedMoodboardResources.size,
            )
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(
                    com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                )
                // Build summary info into InboxStyle template.
                .setStyle(moodboardNotificationStyle(truncatedMoodboardResources, title))
                .setGroup(MOODBOARD_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // NOTE: Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        moodboardNotifications.forEachIndexed { index, notification ->
            notificationManager.notify(
                truncatedMoodboardResources[index].id.hashCode(),
                notification,
            )
        }
        notificationManager.notify(MOODBOARD_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    override fun postUserNotifications(
        userResource: UserResource
    ) = with(context) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val userNotification = createUserNotification {
            setSmallIcon(
                com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
            )
                .setContentTitle(userResource.id)
                .setContentIntent(userPendingIntent(userResource))
                .setGroup(USER_NOTIFICATION_GROUP)
                .setAutoCancel(true)
        }

        val summaryNotification = createUserNotification {
            val title = getString(R.string.user_notification_group_summary)
            setContentTitle(title)
                .setContentText(title)
                .setSmallIcon(
                    com.ngapps.phototime.core.common.R.drawable.ic_pt_notification,
                )
                // Build summary info into InboxStyle template.
                .setStyle(userNotificationStyle(userResource, title))
                .setGroup(USER_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
        }

        // NOTE: Send the notifications
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(
            userResource.id.hashCode(),
            userNotification,
        )
        notificationManager.notify(USER_NOTIFICATION_SUMMARY_ID, summaryNotification)
    }

    /**
     * Creates an inbox style summary notification for task updates
     */
    private fun taskNotificationStyle(
        taskResources: List<TaskResource>,
        title: String,
    ): InboxStyle = taskResources
        .fold(InboxStyle()) { inboxStyle, taskResource ->
            inboxStyle.addLine(taskResource.title)
        }
        .setBigContentTitle(title)
        .setSummaryText(title)

    /**
     * Creates an inbox style summary notification for location updates
     */
    private fun locationNotificationStyle(
        locationResources: List<LocationResource>,
        title: String,
    ): InboxStyle = locationResources
        .fold(InboxStyle()) { inboxStyle, locationResource ->
            inboxStyle.addLine(locationResource.title)
        }
        .setBigContentTitle(title)
        .setSummaryText(title)

    /**
     * Creates an inbox style summary notification for contact updates
     */
    private fun contactNotificationStyle(
        contactResources: List<ContactResource>,
        title: String,
    ): InboxStyle = contactResources
        .fold(InboxStyle()) { inboxStyle, contactResource ->
            inboxStyle.addLine(contactResource.name)
        }
        .setBigContentTitle(title)
        .setSummaryText(title)

    /**
     * Creates an inbox style summary notification for shoot updates
     */
    private fun shootNotificationStyle(
        shootResources: List<ShootResource>,
        title: String,
    ): InboxStyle = shootResources
        .fold(InboxStyle()) { inboxStyle, shootResource ->
            inboxStyle.addLine(shootResource.title)
        }
        .setBigContentTitle(title)
        .setSummaryText(title)

    /**
     * Creates an inbox style summary notification for moodboard updates
     */
    private fun moodboardNotificationStyle(
        moodboardResources: List<MoodboardResource>,
        title: String,
    ): InboxStyle = moodboardResources
        .fold(InboxStyle()) { inboxStyle, moodboardResource ->
            inboxStyle.addLine(moodboardResource.id)
        }
        .setBigContentTitle(title)
        .setSummaryText(title)

    /**
     * Creates an inbox style summary notification for user updates
     */
    private fun userNotificationStyle(
        userResource: UserResource,
        title: String,
    ): InboxStyle = InboxStyle()
        .addLine(userResource.id)
        .setBigContentTitle(title)
        .setSummaryText(title)

}

/**
 * Creates a notification for configured for task updates
 */
private fun Context.createTaskNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureTaskNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        TASK_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures the a notification channel is is present if applicable
 */
private fun Context.ensureTaskNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        TASK_NOTIFICATION_CHANNEL_ID,
        getString(R.string.task_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.task_notification_channel_description)
    }
    // NOTE: Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.taskPendingIntent(
    taskResource: TaskResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    TASK_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = taskResource.taskDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/**
 * Creates a notification for configured for location updates
 */
private fun Context.createLocationNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureLocationNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        LOCATION_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures the a notification channel is is present if applicable
 */
private fun Context.ensureLocationNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        LOCATION_NOTIFICATION_CHANNEL_ID,
        getString(R.string.location_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.location_notification_channel_description)
    }
    // NOTE: Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.locationPendingIntent(
    locationResource: LocationResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    LOCATION_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = locationResource.locationDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/**
 * Creates a notification for configured for contact updates
 */
private fun Context.createContactNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureContactNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        CONTACT_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures the a notification channel is is present if applicable
 */
private fun Context.ensureContactNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        CONTACT_NOTIFICATION_CHANNEL_ID,
        getString(R.string.contact_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.contact_notification_channel_description)
    }
    // NOTE: Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.contactPendingIntent(
    contactResource: ContactResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    CONTACT_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = contactResource.contactDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/**
 * Creates a notification for configured for shoot updates
 */
private fun Context.createShootNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureShootNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        SHOOT_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures the a notification channel is is present if applicable
 */
private fun Context.ensureShootNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        SHOOT_NOTIFICATION_CHANNEL_ID,
        getString(R.string.shoot_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.shoot_notification_channel_description)
    }
    // NOTE: Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.shootPendingIntent(
    shootResource: ShootResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    SHOOT_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = shootResource.shootDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/**
 * Creates a notification for configured for moodboard updates
 */
private fun Context.createMoodboardNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureMoodboardNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        MOODBOARD_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures the a notification channel is is present if applicable
 */
private fun Context.ensureMoodboardNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        MOODBOARD_NOTIFICATION_CHANNEL_ID,
        getString(R.string.moodboard_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.moodboard_notification_channel_description)
    }
    // NOTE: Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.moodboardPendingIntent(
    moodboardResource: MoodboardResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    MOODBOARD_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = moodboardResource.moodboardDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

/**
 * Creates a notification for configured for user updates
 */
private fun Context.createUserNotification(
    block: NotificationCompat.Builder.() -> Unit,
): Notification {
    ensureUserNotificationChannelExists()
    return NotificationCompat.Builder(
        this,
        USER_NOTIFICATION_CHANNEL_ID,
    )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .apply(block)
        .build()
}

/**
 * Ensures the a notification channel is is present if applicable
 */
private fun Context.ensureUserNotificationChannelExists() {
    if (VERSION.SDK_INT < VERSION_CODES.O) return

    val channel = NotificationChannel(
        USER_NOTIFICATION_CHANNEL_ID,
        getString(R.string.user_notification_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT,
    ).apply {
        description = getString(R.string.user_notification_channel_description)
    }
    // NOTE: Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
}

private fun Context.userPendingIntent(
    userResource: UserResource,
): PendingIntent? = PendingIntent.getActivity(
    this,
    USER_NOTIFICATION_REQUEST_CODE,
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = userResource.userDeepLinkUri()
        component = ComponentName(
            packageName,
            TARGET_ACTIVITY_NAME,
        )
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
)

private fun TaskResource.taskDeepLinkUri() = "$DEEP_LINK_SCHEME_AND_HOST/$TASKS_PATH/$id".toUri()
private fun LocationResource.locationDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$LOCATIONS_PATH/$id".toUri()

private fun ContactResource.contactDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$CONTACTS_PATH/$id".toUri()

private fun ShootResource.shootDeepLinkUri() = "$DEEP_LINK_SCHEME_AND_HOST/$TASKS_PATH/$id".toUri()
private fun MoodboardResource.moodboardDeepLinkUri() =
    "$DEEP_LINK_SCHEME_AND_HOST/$TASKS_PATH/$id".toUri()

private fun UserResource.userDeepLinkUri() = "$DEEP_LINK_SCHEME_AND_HOST/$HOME_PATH/$id".toUri()
