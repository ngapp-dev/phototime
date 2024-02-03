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

package com.ngapps.phototime.feature.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ngapps.phototime.core.model.data.DarkThemeConfig.DARK
import com.ngapps.phototime.feature.settings.SettingsUiState.Loading
import com.ngapps.phototime.feature.settings.SettingsUiState.Success
import com.ngapps.phototime.feature.user.settings.R
import org.junit.Rule
import org.junit.Test

class SettingsDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun getString(id: Int) = composeTestRule.activity.resources.getString(id)

    @Test
    fun whenLoading_showsLoadingText() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Loading,
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeDarkThemeConfig = {},
                onLogoutClick = {},
            )
        }

        composeTestRule
            .onNodeWithText(getString(R.string.loading))
            .assertExists()
    }

    @Test
    fun whenStateIsSuccess_allDefaultSettingsAreDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    UserEditableSettings(
                        useDynamicColor = false,
                        darkThemeConfig = DARK,
                    ),
                ),
                onDismiss = { },
                onChangeDynamicColorPreference = {},
                onChangeDarkThemeConfig = {},
                onLogoutClick = {},
            )
        }

        // NOTE: Check that all the possible settings are displayed.
        composeTestRule.onNodeWithText(
            getString(R.string.dark_mode_config_system_default),
        ).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.dark_mode_config_light)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.dark_mode_config_dark)).assertExists()

        // NOTE: Check that the correct settings are selected.
        composeTestRule.onNodeWithText(getString(R.string.dark_mode_config_dark)).assertIsSelected()
    }

    @Test
    fun whenStateIsSuccess_supportsDynamicColor_usesDefaultBrand_DynamicColorOptionIsDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                supportDynamicColor = true,
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeDarkThemeConfig = {},
                onLogoutClick = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_preference)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_yes)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_no)).assertExists()

        // NOTE: Check that the correct default dynamic color setting is selected.
        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_no)).assertIsSelected()
    }

    @Test
    fun whenStateIsSuccess_notSupportDynamicColor_DynamicColorOptionIsNotDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeDarkThemeConfig = {},
                onLogoutClick = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_preference))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_yes)).assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_no)).assertDoesNotExist()
    }

    @Test
    fun whenStateIsSuccess_usesAndroidBrand_DynamicColorOptionIsNotDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeDarkThemeConfig = {},
                onLogoutClick = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_preference))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_yes)).assertDoesNotExist()
        composeTestRule.onNodeWithText(getString(R.string.dynamic_color_no)).assertDoesNotExist()
    }

    @Test
    fun whenStateIsSuccess_allLinksAreDisplayed() {
        composeTestRule.setContent {
            SettingsDialog(
                settingsUiState = Success(
                    UserEditableSettings(
                        darkThemeConfig = DARK,
                        useDynamicColor = false,
                    ),
                ),
                onDismiss = {},
                onChangeDynamicColorPreference = {},
                onChangeDarkThemeConfig = {},
                onLogoutClick = {},
            )
        }

        composeTestRule.onNodeWithText(getString(R.string.privacy_policy)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.licenses)).assertExists()
        composeTestRule.onNodeWithText(getString(R.string.feedback)).assertExists()
    }
}
