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

/**
 * External data layer representation of a Pt User
 */
data class UserResource(
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
)

/**
 * External data layer representation of a Pt Token
 */
data class TokenResource(
    val google: GoogleTokenResource
)

/**
 * External data layer representation of a Pt GoogleToken
 */
data class GoogleTokenResource(
    val contact: String,
    val calendar: String,
)

/**
 * External data layer representation of a Pt Device
 */
data class DeviceResource(
    val name: String,
    val model: String,
    val fingerprint: String,
    val fcmToken: String
)

/**
 * External data layer representation of a Pt Categories
 */
data class CategoriesResource(
    val contact: List<String>,
    val task: List<String>,
    val location: List<String>,
    val connectTo: List<String>
)
