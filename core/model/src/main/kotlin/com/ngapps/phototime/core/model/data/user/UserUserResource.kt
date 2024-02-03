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

package com.ngapps.phototime.core.model.data.user

import com.ngapps.phototime.core.model.data.UserData

/**
 * A [UserResource] with additional user information.
 */
data class UserUserResource(
    val id: String,
    val username: String,
    val email: String,
    val lastLogin: String,
    val created: String,
    val isActive: Boolean,
    val audience: String,
    val token: TokenResource,
    val devices: List<DeviceResource>,
    val categories: CategoriesResource,
    val userLocation: Pair<String, String>,
) {
    constructor(userResource: UserResource, userData: UserData) : this(
        id = userResource.id,
        username = userResource.username,
        email = userResource.email,
        lastLogin = userResource.lastLogin,
        created = userResource.created,
        isActive = userResource.isActive,
        audience = userResource.audience,
        token = userResource.token,
        devices = userResource.devices,
        categories = userResource.categories,
        userLocation = userData.userLocation,
    )
}

fun UserResource.joinToUserUserResource(userData: UserData): UserUserResource {
    return  UserUserResource(this, userData)
}
