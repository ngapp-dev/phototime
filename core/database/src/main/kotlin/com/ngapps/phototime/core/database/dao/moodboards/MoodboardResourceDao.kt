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

package com.ngapps.phototime.core.database.dao.moodboards

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapps.phototime.core.database.model.moodboards.MoodboardResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [MoodboardResourceEntity] access
 */
@Dao
interface MoodboardResourceDao {

    /**
     * Fetches moodboard resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM moodboard_resources
            WHERE 
                CASE WHEN :useFilterMoodboardIds
                    THEN id IN (:filterMoodboardIds)
                    ELSE 1
                END
            ORDER BY id DESC
    """,
    )
    fun getMoodboardResources(
        useFilterMoodboardIds: Boolean = false,
        filterMoodboardIds: Set<String> = emptySet(),
    ): Flow<List<MoodboardResourceEntity>>

    /**
     * Fetches single moodboard resource that match the id parameter
     */
    @Query(value = "SELECT * FROM moodboard_resources WHERE id = :id")
    fun getMoodboardResource(id: String): Flow<MoodboardResourceEntity>

    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreMoodboardResources(entities: List<MoodboardResourceEntity>): List<Long>

    /**
     * Inserts or updates [moodboardResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertMoodboardResources(moodboardResourceEntities: List<MoodboardResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = "DELETE FROM moodboard_resources WHERE id in (:ids)")
    suspend fun deleteMoodboardResources(ids: List<String>)
}
