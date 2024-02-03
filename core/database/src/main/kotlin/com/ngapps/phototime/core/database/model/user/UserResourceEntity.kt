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

package com.ngapps.phototime.core.database.model.user

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapps.phototime.core.model.data.user.CategoriesResource
import com.ngapps.phototime.core.model.data.user.GoogleTokenResource
import com.ngapps.phototime.core.model.data.user.TokenResource
import com.ngapps.phototime.core.model.data.user.UserResource

/**
 * Defines an Pt user resource.
 */
@Entity(tableName = "user_resource")
data class UserResourceEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    @ColumnInfo(name = "last_login")
    val lastLogin: String,
    val created: String,
    @ColumnInfo(name = "is_active")
    val isActive: Int,
    val audience: String,
    @Embedded(prefix = "token_")
    val token: TokenResourceEntity,
    @Embedded(prefix = "categories_")
    val categories: CategoriesResourceEntity
)

data class TokenResourceEntity(
    @Embedded(prefix = "google_")
    val google: GoogleTokenResourceEntity
)

data class GoogleTokenResourceEntity(
    val contact: String,
    val calendar: String,
)

data class CategoriesResourceEntity(
    val contact: List<String>,
    val task: List<String>,
    val location: List<String>,
    @ColumnInfo(name = "connect_to")
    val connectTo: List<String>
)

fun UserResourceEntityWithDevices.asExternalModel() = UserResource(
    id = userResourceEntity.id,
    username = userResourceEntity.username,
    email = userResourceEntity.email,
    lastLogin = userResourceEntity.lastLogin,
    created = userResourceEntity.created,
    isActive = userResourceEntity.isActive == 1,
    audience = userResourceEntity.audience,
    token = userResourceEntity.token.asExternalModel(),
    devices = devices.map { it.asExternalModel() },
    categories = userResourceEntity.categories.asExternalModel(),
)

fun TokenResourceEntity.asExternalModel() = TokenResource(
    google = google.asExternalModel(),
)

fun GoogleTokenResourceEntity.asExternalModel() = GoogleTokenResource(
    contact = contact,
    calendar = calendar,
)

fun CategoriesResourceEntity.asExternalModel() = CategoriesResource(
    contact = contact,
    task = task,
    location = location,
    connectTo = connectTo,
)




