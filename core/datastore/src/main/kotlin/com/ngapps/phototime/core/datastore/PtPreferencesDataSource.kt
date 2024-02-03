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

package com.ngapps.phototime.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.ngapps.phototime.core.model.data.DarkThemeConfig
import com.ngapps.phototime.core.model.data.UserData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class PtPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                completedTaskResources = it.completedTaskResourceIdsMap.keys,
                userLocation = Pair(
                    it.userLocationMap.keys.toString(),
                    it.userLocationMap.keys.toString(),
                ),
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    -> DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor,
                shouldHideOnboarding = it.shouldHideOnboarding,
            )
        }


    suspend fun setUserLocation(userLocationValues: Pair<String, String>) {
        userPreferences.updateData {
            it.copy {
                userLocation.put(userLocationValues.first, userLocationValues.second)
            }
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.useDynamicColor = useDynamicColor
            }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun toggleTaskResourceCompleted(taskResourceId: String, completed: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (completed) {
                        completedTaskResourceIds.put(taskResourceId, true)
                    } else {
                        completedTaskResourceIds.remove(taskResourceId)
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("SitPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun getChangeListVersions() = userPreferences.data
        .map {
            ChangeListVersions(
                taskResourceVersion = it.taskResourceChangeListVersion,
                locationResourceVersion = it.locationResourceChangeListVersion,
                contactResourceVersion = it.contactResourceChangeListVersion,
                shootResourceVersion = it.shootResourceChangeListVersion,
                moodboardResourceVersion = it.moodboardResourceChangeListVersion,
                userResourceVersion = it.userResourceChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    /**
     * Update the [ChangeListVersions] using [update].
     */
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.updateData { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        taskResourceVersion = currentPreferences.taskResourceChangeListVersion,
                        locationResourceVersion = currentPreferences.locationResourceChangeListVersion,
                        contactResourceVersion = currentPreferences.contactResourceChangeListVersion,
                        shootResourceVersion = currentPreferences.shootResourceChangeListVersion,
                        moodboardResourceVersion = currentPreferences.moodboardResourceChangeListVersion,
                        userResourceVersion = currentPreferences.userResourceChangeListVersion,
                    ),
                )

                currentPreferences.copy {
                    taskResourceChangeListVersion = updatedChangeListVersions.taskResourceVersion
                    locationResourceChangeListVersion =
                        updatedChangeListVersions.locationResourceVersion
                    contactResourceChangeListVersion =
                        updatedChangeListVersions.contactResourceVersion
                    shootResourceChangeListVersion = updatedChangeListVersions.shootResourceVersion
                    moodboardResourceChangeListVersion =
                        updatedChangeListVersions.moodboardResourceVersion
                    userResourceChangeListVersion = updatedChangeListVersions.userResourceVersion
                }
            }
        } catch (ioException: IOException) {
            Log.e("SitPreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy {
                this.shouldHideOnboarding = shouldHideOnboarding
            }
        }
    }
}