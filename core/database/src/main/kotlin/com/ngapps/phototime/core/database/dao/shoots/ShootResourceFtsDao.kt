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
import com.ngapps.phototime.core.database.model.shoots.ShootResourceFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [ShootResourceFtsEntity] access.
 */
@Dao
interface ShootResourceFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shootResources: List<ShootResourceFtsEntity>)

    @Query("SELECT id FROM shootResourcesFts WHERE shootResourcesFts MATCH :query")
    fun searchAllShootResources(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM shootResourcesFts")
    fun getCount(): Flow<Int>
}
