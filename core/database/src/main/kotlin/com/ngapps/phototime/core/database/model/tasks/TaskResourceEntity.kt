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

package com.ngapps.phototime.core.database.model.tasks

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ngapps.phototime.core.model.data.task.ScheduledTimeResource
import com.ngapps.phototime.core.model.data.task.TaskResource

/**
 * Defines an Pt task resource.
 */
@Entity(tableName = "task_resources")
data class TaskResourceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val photos: List<String>,
    val category: String,
    @Embedded
    val scheduledTime: ScheduledTimeEntity,
    val contacts: List<String>,
    val note: String,
)

data class ScheduledTimeEntity(
    val start: String,
    val notification: String
)

fun ScheduledTimeEntity.asExternalModel() = ScheduledTimeResource(
    start = start,
    notification = notification,
)

fun TaskResourceEntity.asExternalModel() = TaskResource(
    id = id,
    category = category,
    title = title,
    description = description,
    photos = photos,
    scheduledTime = scheduledTime.asExternalModel(),
    contacts = contacts,
    note = note,
)

