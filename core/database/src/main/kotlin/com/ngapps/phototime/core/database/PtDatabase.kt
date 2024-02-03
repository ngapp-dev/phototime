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

package com.ngapps.phototime.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ngapps.phototime.core.database.dao.RecentSearchQueryDao
import com.ngapps.phototime.core.database.dao.contacts.ContactResourceDao
import com.ngapps.phototime.core.database.dao.contacts.ContactResourceFtsDao
import com.ngapps.phototime.core.database.dao.locations.LocationResourceDao
import com.ngapps.phototime.core.database.dao.locations.LocationResourceFtsDao
import com.ngapps.phototime.core.database.dao.moodboards.MoodboardResourceDao
import com.ngapps.phototime.core.database.dao.moodboards.MoodboardResourceFtsDao
import com.ngapps.phototime.core.database.dao.shoots.ShootResourceDao
import com.ngapps.phototime.core.database.dao.shoots.ShootResourceFtsDao
import com.ngapps.phototime.core.database.dao.tasks.TaskResourceDao
import com.ngapps.phototime.core.database.dao.tasks.TaskResourceFtsDao
import com.ngapps.phototime.core.database.dao.user.UserResourceDao
import com.ngapps.phototime.core.database.model.RecentSearchQueryEntity
import com.ngapps.phototime.core.database.model.contacts.ContactResourceEntity
import com.ngapps.phototime.core.database.model.contacts.ContactResourceFtsEntity
import com.ngapps.phototime.core.database.model.locations.LocationResourceEntity
import com.ngapps.phototime.core.database.model.locations.LocationResourceFtsEntity
import com.ngapps.phototime.core.database.model.moodboards.MoodboardResourceEntity
import com.ngapps.phototime.core.database.model.moodboards.MoodboardResourceFtsEntity
import com.ngapps.phototime.core.database.model.shoots.ShootResourceEntity
import com.ngapps.phototime.core.database.model.shoots.ShootResourceFtsEntity
import com.ngapps.phototime.core.database.model.tasks.TaskResourceEntity
import com.ngapps.phototime.core.database.model.tasks.TaskResourceFtsEntity
import com.ngapps.phototime.core.database.model.user.DeviceResourceEntity
import com.ngapps.phototime.core.database.model.user.UserResourceEntity
import com.ngapps.phototime.core.database.util.InstantConverter
import com.ngapps.phototime.core.database.util.ListStringConverter

// TODO: Rebuild with current task
@Database(
    entities = [
        UserResourceEntity::class,
        DeviceResourceEntity::class,
        MoodboardResourceEntity::class,
        MoodboardResourceFtsEntity::class,
        ShootResourceEntity::class,
        ShootResourceFtsEntity::class,
        ContactResourceEntity::class,
        ContactResourceFtsEntity::class,
        LocationResourceEntity::class,
        LocationResourceFtsEntity::class,
        TaskResourceEntity::class,
        TaskResourceFtsEntity::class,
        RecentSearchQueryEntity::class,
    ],
//    version = 14,
    version = 1,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 2, to = 3, spec = DatabaseMigrations.Schema2to3::class),
//        AutoMigration(from = 3, to = 4),
//        AutoMigration(from = 4, to = 5),
//        AutoMigration(from = 5, to = 6),
//        AutoMigration(from = 6, to = 7),
//        AutoMigration(from = 7, to = 8),
//        AutoMigration(from = 8, to = 9),
//        AutoMigration(from = 9, to = 10),
//        AutoMigration(from = 10, to = 11, spec = DatabaseMigrations.Schema10to11::class),
//        AutoMigration(from = 11, to = 12, spec = DatabaseMigrations.Schema11to12::class),
//        AutoMigration(from = 12, to = 13),
//        AutoMigration(from = 13, to = 14),
//    ],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
    ListStringConverter::class,
)
abstract class PtDatabase : RoomDatabase() {
    abstract fun userResourceDao(): UserResourceDao
    abstract fun moodboardResourceDao(): MoodboardResourceDao
    abstract fun moodboardResourceFtsDao(): MoodboardResourceFtsDao
    abstract fun shootResourceDao(): ShootResourceDao
    abstract fun shootResourceFtsDao(): ShootResourceFtsDao
    abstract fun contactResourceDao(): ContactResourceDao
    abstract fun contactResourceFtsDao(): ContactResourceFtsDao
    abstract fun locationResourceDao(): LocationResourceDao
    abstract fun locationResourceFtsDao(): LocationResourceFtsDao
    abstract fun taskResourceDao(): TaskResourceDao
    abstract fun taskResourceFtsDao(): TaskResourceFtsDao
    abstract fun recentSearchQueryDao(): RecentSearchQueryDao
}
