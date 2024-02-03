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

package com.ngapps.phototime.core.model.data.shoot

import com.ngapps.phototime.core.model.data.UserData
import com.ngapps.phototime.core.model.data.task.ScheduledTimeResource

/**
 * A [ShootResource] with additional user information.
 */
data class UserShootResource internal constructor(
    val id: String,
    val title: String,
    val description: String,
    val photos: List<String>,
    val scheduledTime: ScheduledTimeResource,
    val moodboards: List<String>,
    val contacts: List<String>,
    val locations: List<String>,
    val tasks: List<String>,
    val userLocation: Pair<String, String>,
) {
    constructor(shootResource: ShootResource, userData: UserData) : this(
        id = shootResource.id,
        title = shootResource.title,
        description = shootResource.description,
        photos = shootResource.photos,
        scheduledTime = shootResource.scheduledTime,
        moodboards = shootResource.moodboards,
        contacts = shootResource.contacts,
        locations = shootResource.locations,
        tasks = shootResource.tasks,
        userLocation = userData.userLocation,
    )
}

fun List<ShootResource>.mapToUserShootResources(userData: UserData): List<UserShootResource> {
    return map { UserShootResource(it, userData) }
}




