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

package com.ngapps.phototime.core.model.data.location

import com.ngapps.phototime.core.model.data.UserData

/**
 * A [LocationResource] with additional user information.
 */
data class UserLocationResource internal constructor(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    val photos: List<String>,
    val address: String,
    val lat: String,
    val lng: String,
    val userLocation: Pair<String, String>,
) {
    constructor(locationResource: LocationResource, userData: UserData) : this(
        id = locationResource.id,
        category = locationResource.category,
        title = locationResource.title,
        description = locationResource.description,
        photos = locationResource.photos,
        address = locationResource.address,
        lat = locationResource.lat,
        lng = locationResource.lng,
        userLocation = userData.userLocation,
    )
}

fun List<LocationResource>.mapToUserLocationResources(userData: UserData): List<UserLocationResource> {
    return map { UserLocationResource(it, userData) }
}




