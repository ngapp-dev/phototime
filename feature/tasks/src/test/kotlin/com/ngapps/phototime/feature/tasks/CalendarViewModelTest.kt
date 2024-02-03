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

package com.ngapps.phototime.feature.tasks

import androidx.lifecycle.SavedStateHandle
import com.ngapps.phototime.core.testing.repository.TestShootsRepository
import com.ngapps.phototime.core.testing.repository.TestTasksRepository
import com.ngapps.phototime.core.testing.repository.TestUserDataRepository
import com.ngapps.phototime.core.testing.util.MainDispatcherRule
import com.ngapps.phototime.core.testing.util.TestSyncManager
import com.ngapps.phototime.feature.calendar.calendar.CalendarUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class CalendarViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()
    private val userDataRepository = TestUserDataRepository()
    private val tasksRepository = TestTasksRepository()
    private val shootsRepository = TestShootsRepository()

    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: CalendarViewModel

    @Before
    fun setup() {
        viewModel = CalendarViewModel(
            syncManager = syncManager,
            savedStateHandle = savedStateHandle,
            userDataRepository = userDataRepository,
            shootsRepository = shootsRepository,
            tasksRepository = tasksRepository,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(TasksUiState.Loading, viewModel.tasksUiState.value)
        assertEquals(CalendarUiState.Loading, viewModel.calendarUiState.value)
    }

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoTasks() = runTest {
        syncManager.setSyncing(true)

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            true,
            viewModel.isSyncing.value,
        )

        collectJob.cancel()
    }
}