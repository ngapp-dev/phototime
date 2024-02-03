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

package com.ngapps.phototime.core.database.dao.contacts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapps.phototime.core.database.model.contacts.ContactResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [ContactResourceEntity] access
 */
@Dao
interface ContactResourceDao {

    /**
     * Fetches contact resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM contact_resources
            WHERE 
                CASE WHEN :useFilterContactIds
                    THEN id IN (:filterContactIds)
                    ELSE 1
                END
            AND
                 CASE WHEN :useFilterContactCategories
                    THEN category IN (:filterContactCategories)
                    ELSE 1
                END               
            ORDER BY id DESC
    """,
    )
    fun getContactResources(
        useFilterContactIds: Boolean = false,
        filterContactIds: Set<String> = emptySet(),
        useFilterContactCategories: Boolean = false,
        filterContactCategories: Set<String> = emptySet(),
    ): Flow<List<ContactResourceEntity>>

    @Query(value = """SELECT * FROM contact_resources WHERE id = :id""")
    fun getContactResource(id: String): Flow<ContactResourceEntity>

    @Query(value = "SELECT DISTINCT category FROM contact_resources ORDER BY category")
    fun getContactResourcesUniqueCategories(): Flow<List<String>>

    /**
     * Inserts [entities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreContactResources(entities: List<ContactResourceEntity>): List<Long>

    /**
     * Inserts or updates [contactResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertContactResources(contactResourceEntities: List<ContactResourceEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(value = """DELETE FROM contact_resources WHERE id in (:ids)""")
    suspend fun deleteContactResources(ids: List<String>)
}
