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

package com.ngapps.phototime.core.database.dao.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapps.phototime.core.database.model.tasks.TaskResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [TaskResourceEntity] access
 */
@Dao
interface TaskResourceDao {

    /**
     * Fetches task resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM task_resources
            WHERE 
                CASE WHEN :useFilterTaskIds
                    THEN id IN (:filterTaskIds)
                    ELSE 1
                END
            AND
                CASE WHEN :useFilterTaskDate
                    THEN SUBSTR(start, 1, 10) = :filterTaskDate
                    ELSE 1
                END
            ORDER BY start DESC
    """,
    )
    fun getTaskResources(
        useFilterTaskIds: Boolean = false,
        filterTaskIds: Set<String> = emptySet(),
        useFilterTaskDate: Boolean = false,
        filterTaskDate: String? = ""
    ): Flow<List<TaskResourceEntity>>

    @Query(value = "SELECT * FROM task_resources WHERE id = :id")
    fun getTaskResource(id: String): Flow<TaskResourceEntity>

    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTaskResources(entities: List<TaskResourceEntity>): List<Long>

    /**
     * Inserts or updates [taskResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertTaskResources(taskResourceEntities: List<TaskResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = "DELETE FROM task_resources WHERE id in (:ids)")
    suspend fun deleteTaskResources(ids: List<String>)
}
