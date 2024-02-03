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

import com.ngapps.phototime.core.domain.auth.GetSignOutUseCase
import com.ngapps.phototime.core.model.data.DarkThemeConfig.DARK
import com.ngapps.phototime.core.testing.repository.TestAuthRepository
import com.ngapps.phototime.core.testing.repository.TestUserDataRepository
import com.ngapps.phototime.core.testing.util.MainDispatcherRule
import com.ngapps.phototime.feature.settings.SettingsUiState.Loading
import com.ngapps.phototime.feature.settings.SettingsUiState.Success
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userDataRepository = TestUserDataRepository()
    private val authRepository = TestAuthRepository()
    private val getSignOutUseCase = GetSignOutUseCase(authRepository)

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        viewModel = SettingsViewModel(userDataRepository, getSignOutUseCase)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(Loading, viewModel.settingsUiState.value)
    }

    @Test
    fun stateIsSuccessAfterUserDataLoaded() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.settingsUiState.collect() }

        userDataRepository.setDarkThemeConfig(DARK)

        assertEquals(
            Success(
                UserEditableSettings(
                    darkThemeConfig = DARK,
                    useDynamicColor = false,
                ),
            ),
            viewModel.settingsUiState.value,
        )

        collectJob.cancel()
    }
}
