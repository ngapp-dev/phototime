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

package com.ngapps.phototime.core.network.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of [UserResource]
 */
@Serializable
data class NetworkUserResource(
    @SerialName(value = "_id") val id: String = "",
    val username: String = "",
    val email: String = "",
    @SerialName(value = "last_login") val lastLogin: String = "",
    val created: String = "",
    @SerialName(value = "is_active") val isActive: Boolean = true,
    val audience: String = "",
    val token: NetworkTokenResource = NetworkTokenResource(),
    val categories: NetworkCategoriesResource = NetworkCategoriesResource(),
    val devices: List<NetworkDeviceResource> = emptyList()
)

/**
 * Network representation of [TokenResource]
 */
@Serializable
data class NetworkTokenResource(
    val google: NetworkGoogleTokenResource = NetworkGoogleTokenResource()
)

/**
 * Network representation of [GoogleTokenResource]
 */
@Serializable
data class NetworkGoogleTokenResource(
    val contact: String = "",
    val calendar: String = ""
)

/**
 * Network representation of [CategoriesResource]
 */
@Serializable
data class NetworkCategoriesResource(
    val contact: List<String> = listOf(),
    val task: List<String> = listOf(),
    val location: List<String> = listOf(),
    @SerialName(value = "connect_to") val connectTo: List<String> = emptyList()
)

/**
 * Network representation of [DeviceResource]
 */
@Serializable
data class NetworkDeviceResource(
    val name: String = "",
    val model: String = "",
    val fingerprint: String = "",
    @SerialName(value = "fcm_token") val fcmToken: String = "",
    val refresh: String = ""
)