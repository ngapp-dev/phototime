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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.data.repository.shoots.ShootResourceEntityQuery
import com.ngapps.phototime.core.data.repository.shoots.ShootsRepository
import com.ngapps.phototime.core.data.repository.tasks.TaskResourceEntityQuery
import com.ngapps.phototime.core.data.repository.tasks.TasksRepository
import com.ngapps.phototime.core.data.util.SyncManager
import com.ngapps.phototime.core.domain.shoots.GetDeleteShootUseCase
import com.ngapps.phototime.core.domain.tasks.GetDeleteTaskUseCase
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.result.asResult
import com.ngapps.phototime.feature.calendar.calendar.CalendarUiState
import com.ngapps.phototime.feature.tasks.navigation.LINKED_TASK_RESOURCE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import com.ngapps.phototime.core.result.Result

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
    private val userDataRepository: UserDataRepository,
    shootsRepository: ShootsRepository,
    tasksRepository: TasksRepository,
    private val getDeleteShoot: GetDeleteShootUseCase,
    private val getDeleteTask: GetDeleteTaskUseCase,
) : ViewModel() {

    private val _selectedDay =
        MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    val selectedDay: StateFlow<LocalDate> = _selectedDay.asStateFlow()

    val deepLinkedTaskResource = savedStateHandle.getStateFlow<String?>(
        key = LINKED_TASK_RESOURCE_ID,
        null,
    )
        .flatMapLatest { taskResourceId ->
            if (taskResourceId == null) {
                flowOf(emptyList())
            } else {
                tasksRepository.getTaskResources(
                    TaskResourceEntityQuery(
                        filterTaskIds = setOf(taskResourceId),
                    ),
                )
            }
        }
        .map { it.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val calendarUiState: StateFlow<CalendarUiState> = taskCalendarUiState(
        tasksRepository = tasksRepository,
        shootsRepository = shootsRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CalendarUiState.Loading,
        )

    val tasksUiState: StateFlow<TasksUiState> = taskFeedUiState(
        selectedDay = selectedDay,
        shootsRepository = shootsRepository,
        tasksRepository = tasksRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TasksUiState.Loading,
        )

    private val _viewEvents = MutableSharedFlow<CalendarViewEvent>()
    val viewEvents: SharedFlow<CalendarViewEvent> = _viewEvents.asSharedFlow()

    fun triggerAction(action: CalendarAction) = when (action) {
        is CalendarAction.DeleteShoot -> deleteShoot(action.shootId)
        is CalendarAction.DeleteTask -> deleteTask(action.taskId)
    }

    private fun deleteShoot(shootId: String) {
        viewModelScope.launch {
            getDeleteShoot(shootId).checkResult(
                onSuccess = {
                    _viewEvents.emit(CalendarViewEvent.Message("Delete success"))
                },
                onError = {
                    _viewEvents.emit(CalendarViewEvent.Message(it))
                },
            )
        }
    }

    private fun deleteTask(taskId: String) {
        viewModelScope.launch {
            getDeleteTask(taskId).checkResult(
                onSuccess = {
                    _viewEvents.emit(CalendarViewEvent.Message("Delete success"))
                },
                onError = {
                    _viewEvents.emit(CalendarViewEvent.Message(it))
                },
            )
        }
    }

    fun updateDaySelection(date: LocalDate) {
        _selectedDay.value = date
    }

    fun updateTaskResourceCompleted(taskResourceId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            userDataRepository.updateTaskResourceCompleted(taskResourceId, isCompleted)
        }
    }

    fun onTaskDeepLinkOpened(taskResourceId: String) {
        if (taskResourceId == deepLinkedTaskResource.value?.id) {
            savedStateHandle[LINKED_TASK_RESOURCE_ID] = null
        }
        viewModelScope.launch {
            userDataRepository.updateTaskResourceCompleted(
                taskResourceId = taskResourceId,
                completed = false,
            )
        }
    }
}


private fun taskCalendarUiState(
    tasksRepository: TasksRepository,
    shootsRepository: ShootsRepository
): Flow<CalendarUiState> {

    val tasksStream: Flow<List<TaskResource>> =
        tasksRepository.getTaskResources()

    val shootsStream: Flow<List<ShootResource>> =
        shootsRepository.getShootResources()

    return combine(
        tasksStream,
        shootsStream,
        ::Pair,
    )
        .asResult()
        .map { userTaskResourceWithShootsResult ->
            when (userTaskResourceWithShootsResult) {
                is Result.Success -> {
                    val (tasks, shoots) = userTaskResourceWithShootsResult.data
                    CalendarUiState.Success(
                        calendarTasks = tasks,
                        calendarShoots = shoots,
                    )
                }

                is Result.Loading -> {
                    CalendarUiState.Loading
                }

                is Result.Error -> {
                    CalendarUiState.Error
                }
            }
        }
}

private fun taskFeedUiState(
    selectedDay: StateFlow<LocalDate>,
    shootsRepository: ShootsRepository,
    tasksRepository: TasksRepository,
): Flow<TasksUiState> {

    val shootsStream: Flow<List<ShootResource>> =
        selectedDay.flatMapLatest { date ->
            shootsRepository.getShootResources(query = ShootResourceEntityQuery(filterShootDate = date.toString()))
        }

    val tasksStream: Flow<List<TaskResource>> =
        selectedDay.flatMapLatest { date ->
            tasksRepository.getTaskResources(TaskResourceEntityQuery(filterTaskDate = date.toString()))
        }

    return shootsStream.flatMapLatest { shoots ->
        val shootResource = shoots.firstOrNull()

        val shootTasksStream: Flow<List<TaskResource>> =
            if (shootResource != null) {
                tasksRepository.getTaskResources(TaskResourceEntityQuery(filterTaskIds = shootResource.tasks.toSet()))
            } else {
                flowOf(emptyList())
            }

        return@flatMapLatest combine(
            shootsStream,
            shootTasksStream,
            tasksStream,
            ::Triple,
        )
    }.asResult()
        .map { shootWithTasks ->
            when (shootWithTasks) {
                is Result.Success -> {
                    val (shoots, shootTasks, tasks) = shootWithTasks.data
                    TasksUiState.Success(
                        shoots = shoots,
                        shootTasks = shootTasks,
                        tasks = tasks,
                    )
                }

                is Result.Loading -> {
                    TasksUiState.Loading
                }

                is Result.Error -> {
                    TasksUiState.Error
                }
            }
        }

}

sealed interface TasksUiState {
    data class Success(
        val shoots: List<ShootResource>,
        val shootTasks: List<TaskResource>,
        val tasks: List<TaskResource>,
    ) : TasksUiState

    data object Error : TasksUiState
    data object Loading : TasksUiState
}

sealed class CalendarViewEvent {
    data class Message(val message: String) : CalendarViewEvent()
}

sealed interface CalendarAction {
    data class DeleteShoot(val shootId: String) : CalendarAction
    data class DeleteTask(val taskId: String) : CalendarAction
}
