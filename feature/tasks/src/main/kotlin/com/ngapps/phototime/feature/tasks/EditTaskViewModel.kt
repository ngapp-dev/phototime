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
import com.ngapps.phototime.core.domain.tasks.GetSaveTaskUseCase
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.model.data.task.TaskResourceQuery
import com.ngapps.phototime.core.model.data.task.TaskResourceWithData
import com.ngapps.phototime.core.result.asResult
import com.ngapps.phototime.feature.tasks.navigation.TaskArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class EditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    contactsRepository: ContactsRepository,
    tasksRepository: TasksRepository,
    private val getSaveTask: GetSaveTaskUseCase
) : ViewModel() {

    private val taskArgs: TaskArgs = TaskArgs(savedStateHandle, stringDecoder)

    val taskId = taskArgs.taskId

    val editTaskUiState: StateFlow<EditTaskUiState> = editTaskUiState(
        taskId = taskId,
        tasksRepository = tasksRepository,
        contactsRepository = contactsRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EditTaskUiState.Loading,
        )

    private val _selectedImageUris = MutableStateFlow<List<String>>(emptyList())
    val selectedImageUris: StateFlow<List<String>> = _selectedImageUris


    fun triggerAction(action: EditTaskAction) = when (action) {
        is EditTaskAction.SaveTask -> doSaveTask(action.task)
        is EditTaskAction.SaveSelectedImageUris -> doSaveSelectedImageUris(action.uris)
        is EditTaskAction.RemoveSelectedImageUri -> doRemoveSelectedImageUri(action.index)
    }

    private fun doSaveTask(task: TaskResourceQuery) {
        viewModelScope.launch {  }
    }

    private fun doSaveSelectedImageUris(uris: List<String>) {
        val currentList = _selectedImageUris.value.toMutableList()
        currentList.addAll(uris)
        _selectedImageUris.value = currentList
    }


    private fun doRemoveSelectedImageUri(index: Int) {
        val currentList = _selectedImageUris.value.toMutableList()
        if (index in 0 until currentList.size) {
            currentList.removeAt(index)
            _selectedImageUris.value = currentList
        }
    }

}

private fun editTaskUiState(
    taskId: String,
    tasksRepository: TasksRepository,
    contactsRepository: ContactsRepository,
): Flow<EditTaskUiState> {

    // Observe location
    val tasksStream: Flow<List<TaskResource>> = if (taskId != "0") {
        tasksRepository.getTaskResources(
            TaskResourceEntityQuery(filterTaskIds = setOf(element = taskId)),
        )
    } else {
        flowOf(emptyList())
    }

    return tasksStream.flatMapLatest { tasks ->
        val taskResource = tasks.firstOrNull()


        val contactsStream: Flow<List<ContactResource>> = if (taskResource != null) {
            contactsRepository.getContactResources(
                ContactResourceEntityQuery(filterContactIds = taskResource.contacts.toSet()),
            )
        } else {
            flowOf(emptyList())
        }

        return@flatMapLatest combine(
            tasksStream,
            contactsStream,
            ::TaskResourceWithData,
        )
    }.asResult()
        .map { taskWithDataResult ->
            when (taskWithDataResult) {
                is Result.Success -> {
                    val (task, contacts) = taskWithDataResult.data
                    EditTaskUiState.Success(
                        task = task.firstOrNull(),
                        contacts = contacts,
                    )
                }

                is Result.Loading -> {
                    EditTaskUiState.Loading
                }

                is Result.Error -> {
                    EditTaskUiState.Error
                }
            }
        }
}

sealed interface EditTaskUiState {
    data class Success(
        val task: TaskResource?,
        val contacts: List<ContactResource>,
    ) : EditTaskUiState

    data object Error : EditTaskUiState
    data object Loading : EditTaskUiState
}

sealed interface EditTaskAction {
    data class SaveTask(val task: TaskResourceQuery) : EditTaskAction
    data class SaveSelectedImageUris(val uris: List<String>) : EditTaskAction
    data class RemoveSelectedImageUri(val index: Int) : EditTaskAction
}
