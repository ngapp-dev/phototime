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

package com.ngapps.phototime.core.network.model.shoot

import com.ngapps.phototime.core.network.model.task.NetworkScheduledTimeResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of [ShootResource]
 */
@Serializable
data class NetworkShootResource(
    @SerialName(value = "_id") val id: String,
    val title: String = "",
    val description: String = "",
    val photos: List<String> = emptyList(),
    @SerialName(value = "when") val scheduledTime: NetworkScheduledTimeResource = NetworkScheduledTimeResource(),
    val moodboards: List<String> = emptyList(),
    val contacts: List<String> = emptyList(),
    val locations: List<String> = emptyList(),
    val tasks: List<String> = emptyList(),
)
