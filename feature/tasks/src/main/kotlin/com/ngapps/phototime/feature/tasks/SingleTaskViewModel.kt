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
import com.ngapps.phototime.core.data.repository.contacts.ContactResourceEntityQuery
import com.ngapps.phototime.core.data.repository.contacts.ContactsRepository
import com.ngapps.phototime.core.data.repository.tasks.TaskResourceEntityQuery
import com.ngapps.phototime.core.data.repository.tasks.TasksRepository
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.core.domain.GetDownloadUseCase
import com.ngapps.phototime.core.domain.tasks.GetDeleteTaskUseCase
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.result.asResult
import com.ngapps.phototime.feature.tasks.navigation.TaskArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ngapps.phototime.core.result.Result

@HiltViewModel
class SingleTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    tasksRepository: TasksRepository,
    contactsRepository: ContactsRepository,
    private val getImageDownload: GetDownloadUseCase,
    private val getDeleteTask: GetDeleteTaskUseCase
) : ViewModel() {

    private val taskArgs: TaskArgs = TaskArgs(savedStateHandle, stringDecoder)

    val taskId = taskArgs.taskId

    private val _viewEvents = MutableSharedFlow<SingleTaskViewEvent>()
    val viewEvents: SharedFlow<SingleTaskViewEvent> = _viewEvents

    val singleTaskUiState: StateFlow<SingleTaskUiState> = singleTaskUiState(
        taskId = taskArgs.taskId,
        tasksRepository = tasksRepository,
        contactsRepository = contactsRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SingleTaskUiState.Loading,
        )

    fun triggerAction(action: SingleTaskAction) = when (action) {
        is SingleTaskAction.DeleteTask -> doDeleteTask(action.taskId)
        is SingleTaskAction.DownloadImage -> doDownloadImage(action.inputUrl)
    }

    private fun doDeleteTask(taskId: String) {
        viewModelScope.launch {
            getDeleteTask(taskId).checkResult(
                onSuccess = {
                    _viewEvents.emit(SingleTaskViewEvent.Message("Delete success"))
                    _viewEvents.emit(SingleTaskViewEvent.NavigateBack)
                },
                onError = { _viewEvents.emit(SingleTaskViewEvent.Message(it)) },
            )
        }
    }

    private fun doDownloadImage(inputUrl: String) {
        getImageDownload(inputUrl)
    }
}


private fun singleTaskUiState(
    taskId: String,
    tasksRepository: TasksRepository,
    contactsRepository: ContactsRepository,
): Flow<SingleTaskUiState> {

    // Observe task
    val taskStream: Flow<List<TaskResource>> = if (taskId != "null") {
        tasksRepository.getTaskResources(
            TaskResourceEntityQuery(filterTaskIds = setOf(element = taskId)),
        )
    } else {
        flowOf(emptyList())
    }

    return taskStream.flatMapLatest { taskResources ->
        val taskResource = taskResources.firstOrNull()

        val contactsStream: Flow<List<ContactResource>> = if (taskResource != null) {
            contactsRepository.getContactResources(
                ContactResourceEntityQuery(filterContactIds = taskResource.contacts.toSet()),
            )
        } else {
            flowOf(emptyList())
        }

        return@flatMapLatest combine(
            taskStream,
            contactsStream,
            ::Pair,
        ).asResult()
            .map { taskWithDataResult ->
                when (taskWithDataResult) {
                    is Result.Success -> {
                        val (task, contacts) = taskWithDataResult.data
                        SingleTaskUiState.Success(
                            task = task.firstOrNull(),
                            contacts = contacts,
                        )
                    }

                    is Result.Loading -> {
                        SingleTaskUiState.Loading
                    }

                    is Result.Error -> {
                        SingleTaskUiState.Error
                    }
                }
            }
    }
}


sealed interface SingleTaskUiState {
    data class Success(
        val task: TaskResource?,
        val contacts: List<ContactResource>
    ) : SingleTaskUiState

    data object Error : SingleTaskUiState
    data object Loading : SingleTaskUiState
}

sealed class SingleTaskViewEvent {
    data class Message(val message: String) : SingleTaskViewEvent()
    data object NavigateBack : SingleTaskViewEvent()
}

sealed interface SingleTaskAction {
    data class DeleteTask(val taskId: String) : SingleTaskAction
    data class DownloadImage(val inputUrl: String) : SingleTaskAction
}
