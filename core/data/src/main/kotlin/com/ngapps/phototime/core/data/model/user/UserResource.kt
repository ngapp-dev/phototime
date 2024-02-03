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

package com.ngapps.phototime.core.data.model.user

import com.ngapps.phototime.core.database.model.user.CategoriesResourceEntity
import com.ngapps.phototime.core.database.model.user.DeviceResourceEntity
import com.ngapps.phototime.core.database.model.user.GoogleTokenResourceEntity
import com.ngapps.phototime.core.database.model.user.TokenResourceEntity
import com.ngapps.phototime.core.database.model.user.UserResourceEntity
import com.ngapps.phototime.core.database.model.user.UserResourceEntityWithDevices
import com.ngapps.phototime.core.network.model.user.NetworkCategoriesResource
import com.ngapps.phototime.core.network.model.user.NetworkDeviceResource
import com.ngapps.phototime.core.network.model.user.NetworkGoogleTokenResource
import com.ngapps.phototime.core.network.model.user.NetworkTokenResource
import com.ngapps.phototime.core.network.model.user.NetworkUserResource

fun NetworkUserResource.asUserWithDevicesEntity() = UserResourceEntityWithDevices(
    userResourceEntity = this.asEntity(),
    devices = this.devices.map { it.asEntity() },
)

fun NetworkUserResource.asEntity() = UserResourceEntity(
    id = id,
    username = username,
    email = email,
    lastLogin = lastLogin,
    created = created,
    isActive = if (isActive) 1 else 0,
    audience = audience,
    token = token.asEntity(),
    categories = categories.asEntity(),
)

fun NetworkTokenResource.asEntity() = TokenResourceEntity(
    google = google.asEntity(),
)

fun NetworkGoogleTokenResource.asEntity() = GoogleTokenResourceEntity(
    contact = contact,
    calendar = calendar,
)

fun NetworkDeviceResource.asEntity() = DeviceResourceEntity(
    name = name,
    model = model,
    fingerprint = fingerprint,
    fcmToken = fcmToken,
)

fun NetworkCategoriesResource.asEntity() = CategoriesResourceEntity(
    contact = contact,
    task = task,
    location = location,
    connectTo = connectTo,
)
