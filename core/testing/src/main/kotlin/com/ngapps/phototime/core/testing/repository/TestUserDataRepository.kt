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

package com.ngapps.phototime.core.testing.repository

import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.model.data.DarkThemeConfig
import com.ngapps.phototime.core.model.data.UserData
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull

val emptyUserData = UserData(
    completedTaskResources = emptySet(),
    followedTopics = emptySet(),
    userLocation = Pair("53.90385844181326", "27.55627241172367"),
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    useDynamicColor = false,
    shouldHideOnboarding = false,
    locationCategories = emptySet(),
    contactCategories = emptySet(),
    taskCategories = emptySet(),
)

class TestUserDataRepository : UserDataRepository {
    /**
     * The backing hot flow for the list of followed topic ids for testing.
     */
    private val _userData = MutableSharedFlow<UserData>(replay = 1, onBufferOverflow = DROP_OLDEST)

    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    override val userData: Flow<UserData> = _userData.filterNotNull()
    override suspend fun setUserLocation(userLocation: Pair<String, String>) {

    }

    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {
        _userData.tryEmit(currentUserData.copy(followedTopics = followedTopicIds))
    }

    override suspend fun toggleFollowedTopicId(followedTopicId: String, followed: Boolean) {
        currentUserData.let { current ->
            val followedTopics = if (followed) {
                current.followedTopics + followedTopicId
            } else {
                current.followedTopics - followedTopicId
            }

            _userData.tryEmit(current.copy(followedTopics = followedTopics))
        }
    }

    override suspend fun updateNewsResourceBookmark(newsResourceId: String, bookmarked: Boolean) {
        currentUserData.let { current ->

        }
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        currentUserData.let { current ->

        }
    }

    override suspend fun updateTaskResourceCompleted(taskResourceId: String, completed: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    completedTaskResources =
                    if (completed) {
                        current.completedTaskResources + taskResourceId
                    } else {
                        current.completedTaskResources - taskResourceId
                    },
                ),
            )
        }
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(darkThemeConfig = darkThemeConfig))
        }
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(useDynamicColor = useDynamicColor))
        }
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        currentUserData.let { current ->
            _userData.tryEmit(current.copy(shouldHideOnboarding = shouldHideOnboarding))
        }
    }

    override suspend fun updateLocationCategories(categories: Set<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateContactCategories(categories: Set<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTaskCategories(categories: Set<String>) {
        TODO("Not yet implemented")
    }

    /**
     * A test-only API to allow setting of user data directly.
     */
    fun setUserData(userData: UserData) {
        _userData.tryEmit(userData)
    }
}
