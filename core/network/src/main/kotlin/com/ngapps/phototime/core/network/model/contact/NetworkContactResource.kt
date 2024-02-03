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

package com.ngapps.phototime.core.network.model.contact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of [ContactResource]
 */
//@Serializable
//data class NetworkContactResource(
//    @SerialName(value = "_id") val id: String,
//    val category: String = "",
//    val name: Name,
//    val description: String = "",
////    val photos: List<String> = emptyList(),
//    val photos: String = "",
//    val phone: String = "",
//    val messenger: String = "",
//)
@Serializable
data class NetworkContactResource(
    @SerialName(value = "_id") val id: String,
    val category: String = "",
    val name: Name,
    val description: String = "",
//    val photos: List<String> = emptyList(),
    val photos: String = "",
    val phone: String = "",
    val messenger: String = "",
)

@Serializable
data class Name(
    @SerialName(value = "first_name") val firstName: String,
    @SerialName(value = "last_name") val lastName: String,
)