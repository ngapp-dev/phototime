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

package com.ngapps.phototime.core.database.dao.locations

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapps.phototime.core.database.model.locations.LocationResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [LocationResourceEntity] access
 */
@Dao
interface LocationResourceDao {

    /**
     * Fetches location resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM location_resources
            WHERE 
                CASE WHEN :useFilterLocationIds
                    THEN id IN (:filterLocationIds)
                    ELSE 1
                END
            AND
                 CASE WHEN :useFilterLocationCategories
                    THEN category IN (:filterLocationCategories)
                    ELSE 1
                END               
            ORDER BY id DESC
    """,
    )
    fun getLocationResources(
        useFilterLocationIds: Boolean = false,
        filterLocationIds: Set<String> = emptySet(),
        useFilterLocationCategories: Boolean = false,
        filterLocationCategories: Set<String> = emptySet(),
    ): Flow<List<LocationResourceEntity>>

    @Query(value = """SELECT * FROM location_resources WHERE id = :id""")
    fun getLocationResource(id: String): Flow<LocationResourceEntity>

    @Query(value = "SELECT DISTINCT category FROM location_resources ORDER BY category")
    fun getLocationResourcesUniqueCategories(): Flow<List<String>>

    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreLocationResources(entities: List<LocationResourceEntity>): List<Long>

    /**
     * Inserts or updates [locationResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertLocationResources(locationResourceEntities: List<LocationResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = """DELETE FROM location_resources WHERE id in (:ids)""")
    suspend fun deleteLocationResources(ids: List<String>)
}
