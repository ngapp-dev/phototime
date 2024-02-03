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

package com.ngapps.phototime.core.database.model.moodboards

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapps.phototime.core.model.data.moodboard.DescriptionWithPhotosResource
import com.ngapps.phototime.core.model.data.moodboard.MoodboardResource

/**
 * Defines an Pt moodboard resource.
 */
@Entity(tableName = "moodboard_resources")
data class MoodboardResourceEntity(
    @PrimaryKey
    val id: String,
    @Embedded(prefix = "model_")
    val model: DescriptionWithPhotosEntity,
    @Embedded(prefix = "hairStyle_")
    val hairStyle: DescriptionWithPhotosEntity,
    @Embedded(prefix = "makeup_")
    val makeup: DescriptionWithPhotosEntity,
    @Embedded(prefix = "clothes_")
    val clothes: DescriptionWithPhotosEntity,
    @Embedded(prefix = "other_")
    val other: DescriptionWithPhotosEntity,
)

data class DescriptionWithPhotosEntity(
    val description: String,
    val photos: List<String>
)

fun DescriptionWithPhotosEntity.asExternalModel() = DescriptionWithPhotosResource(
    description = description,
    photos = photos,
)

fun MoodboardResourceEntity.asExternalModel() = MoodboardResource(
    id = id,
    model = model.asExternalModel(),
    hairStyle = hairStyle.asExternalModel(),
    makeup = makeup.asExternalModel(),
    clothes = clothes.asExternalModel(),
    other = other.asExternalModel(),
)