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

package com.ngapps.phototime.core.data.repository.fake

import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.datastore.PtPreferencesDataSource
import com.ngapps.phototime.core.model.data.DarkThemeConfig
import com.ngapps.phototime.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fake implementation of the [UserDataRepository] that returns hardcoded user data.
 *
 * This allows us to run the app with fake data, without needing an internet connection or working
 * backend.
 */
class FakeUserDataRepository @Inject constructor(
    private val ptPreferencesDataSource: PtPreferencesDataSource,
) : UserDataRepository {

    override val userData: Flow<UserData> = ptPreferencesDataSource.userData

    override suspend fun setUserLocation(userLocation: Pair<String, String>) =
        ptPreferencesDataSource.setUserLocation(userLocation)

    override suspend fun updateTaskResourceCompleted(taskResourceId: String, completed: Boolean) =
        ptPreferencesDataSource.toggleTaskResourceCompleted(taskResourceId, completed)

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) =
        ptPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) =
        ptPreferencesDataSource.setDynamicColorPreference(useDynamicColor)

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) =
        ptPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
}
