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

package com.ngapps.phototime.core.database.dao.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ngapps.phototime.core.database.model.user.DeviceResourceEntity
import com.ngapps.phototime.core.database.model.user.UserResourceEntity
import com.ngapps.phototime.core.database.model.user.UserResourceEntityWithDevices
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [UserResourceEntity] access
 */
@Dao
interface UserResourceDao {

    /**
     * Fetches user resource
     */
    @Query(value = "SELECT * FROM user_resource")
    fun getUserResourceWithDevices(): Flow<UserResourceEntityWithDevices>

    /**
     * Inserts [entity] into the db if it doesn't exist, and ignores these that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreUserResource(entity: UserResourceEntity): Long

    /**
     * Inserts or updates [userResourceEntityWithDevices] in the db
     */
    @Transaction
    @Upsert
    suspend fun upsertUserResources(userResourceEntity: UserResourceEntityWithDevices) {
        upsertUserResource(userResourceEntity.userResourceEntity)
        upsertDeviceResource(userResourceEntity.devices)
    }

    @Transaction
    @Upsert
    suspend fun upsertUserResource(userResourceEntity: UserResourceEntity)

    @Transaction
    @Upsert
    suspend fun upsertDeviceResource(deviceResourceEntities: List<DeviceResourceEntity>)

    /**
     * Deletes row in the db
     */
    @Transaction
    @Query(value = "DELETE FROM user_resource")
    suspend fun deleteUserResource()
}
