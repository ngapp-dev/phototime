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

package com.ngapps.phototime.core.database.model.locations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * Fts entity for the location resources. See https://developer.android.com/reference/androidx/room/Fts4.
 */
@Entity(tableName = "locationResourcesFts")
@Fts4
data class LocationResourceFtsEntity(

    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "lat")
    val lat: String,

    @ColumnInfo(name = "lng")
    val lng: String,
)

fun LocationResourceEntity.asFtsEntity() = LocationResourceFtsEntity(
    id = id,
    category = category,
    title = title,
    description = description,
    address = address,
    lat = lat,
    lng = lng,
)
