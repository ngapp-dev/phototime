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

package com.ngapps.phototime.core.database.dao.shoots

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapps.phototime.core.database.model.shoots.ShootResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [ShootResourceEntity] access
 */
@Dao
interface ShootResourceDao {

    /**
     * Fetches shoot resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM shoot_resources
            WHERE 
                CASE WHEN :useFilterShootIds
                    THEN id IN (:filterShootIds)
                    ELSE 1
                END
            AND
                CASE WHEN :useFilterShootDate
                    THEN SUBSTR(start, 1, 10) = :filterShootDate
                    ELSE 1
                END
            ORDER BY start DESC
    """,
    )
    fun getShootResources(
        useFilterShootIds: Boolean = false,
        filterShootIds: Set<String> = emptySet(),
        useFilterShootDate: Boolean = false,
        filterShootDate: String? = ""
    ): Flow<List<ShootResourceEntity>>

    /**
     * Fetches single shoot resource that match the id parameter
     */
    @Query(value = "SELECT * FROM shoot_resources WHERE id = :id")
    fun getShootResource(id: String): Flow<ShootResourceEntity>

    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreShootResources(entities: List<ShootResourceEntity>): List<Long>

    /**
     * Inserts or updates [shootResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertShootResources(shootResourceEntities: List<ShootResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = "DELETE FROM shoot_resources WHERE id in (:ids)")
    suspend fun deleteShootResources(ids: List<String>)
}
