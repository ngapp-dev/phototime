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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesUserResourceDao(
        database: PtDatabase,
    ): UserResourceDao = database.userResourceDao()

    @Provides
    fun providesMoodboardResourceDao(
        database: PtDatabase,
    ): MoodboardResourceDao = database.moodboardResourceDao()

    @Provides
    fun providesMoodboardResourceFtsDao(
        database: PtDatabase,
    ): MoodboardResourceFtsDao = database.moodboardResourceFtsDao()

    @Provides
    fun providesShootResourceDao(
        database: PtDatabase,
    ): ShootResourceDao = database.shootResourceDao()

    @Provides
    fun providesShootResourceFtsDao(
        database: PtDatabase,
    ): ShootResourceFtsDao = database.shootResourceFtsDao()

    @Provides
    fun providesContactResourceDao(
        database: PtDatabase,
    ): ContactResourceDao = database.contactResourceDao()

    @Provides
    fun providesContactResourceFtsDao(
        database: PtDatabase,
    ): ContactResourceFtsDao = database.contactResourceFtsDao()

    @Provides
    fun providesLocationResourceDao(
        database: PtDatabase,
    ): LocationResourceDao = database.locationResourceDao()

    @Provides
    fun providesLocationResourceFtsDao(
        database: PtDatabase,
    ): LocationResourceFtsDao = database.locationResourceFtsDao()

    @Provides
    fun providesTaskResourceDao(
        database: PtDatabase,
    ): TaskResourceDao = database.taskResourceDao()

    @Provides
    fun providesTaskResourceFtsDao(
        database: PtDatabase,
    ): TaskResourceFtsDao = database.taskResourceFtsDao()

    @Provides
    fun providesRecentSearchQueryDao(
        database: PtDatabase,
    ): RecentSearchQueryDao = database.recentSearchQueryDao()
}
