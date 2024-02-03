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
import com.ngapps.phototime.core.data.repository.locations.LocationResourceEntityQuery
import com.ngapps.phototime.core.data.repository.locations.LocationsRepository
import com.ngapps.phototime.core.data.repository.moodboards.MoodboardResourceEntityQuery
import com.ngapps.phototime.core.data.repository.moodboards.MoodboardsRepository
import com.ngapps.phototime.core.data.repository.shoots.ShootResourceEntityQuery
import com.ngapps.phototime.core.data.repository.shoots.ShootsRepository
import com.ngapps.phototime.core.data.repository.tasks.TaskResourceEntityQuery
import com.ngapps.phototime.core.data.repository.tasks.TasksRepository
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.core.domain.shoots.GetSaveShootUseCase
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.moodboard.MoodboardResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.shoot.ShootResourceQuery
import com.ngapps.phototime.core.model.data.shoot.ShootResourceWithData
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.result.Result
import com.ngapps.phototime.core.result.asResult
import com.ngapps.phototime.feature.tasks.navigation.ShootArgs
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

@HiltViewModel
class EditShootViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    shootsRepository: ShootsRepository,
    moodboardsRepository: MoodboardsRepository,
    contactsRepository: ContactsRepository,
    locationsRepository: LocationsRepository,
    tasksRepository: TasksRepository,
    private val getSaveShoot: GetSaveShootUseCase
) : ViewModel() {

    private val shootArgs: ShootArgs = ShootArgs(savedStateHandle, stringDecoder)

    val shootId = shootArgs.shootId

    val editShootUiState: StateFlow<EditShootUiState> = editShootUiState(
        shootId = shootId,
        shootsRepository = shootsRepository,
        moodboardsRepository = moodboardsRepository,
        contactsRepository = contactsRepository,
        locationsRepository = locationsRepository,
        tasksRepository = tasksRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EditShootUiState.Loading,
        )

    private val _selectedImageUris = MutableStateFlow<List<String>>(emptyList())
    val selectedImageUris: StateFlow<List<String>> = _selectedImageUris


    fun triggerAction(action: EditShootAction) = when (action) {
        is EditShootAction.SaveShoot -> doSaveShoot(action.shoot)
        is EditShootAction.SaveSelectedImageUris -> doSaveSelectedImageUris(action.uris)
        is EditShootAction.RemoveSelectedImageUri -> doRemoveSelectedImageUri(action.index)
    }

    private fun doSaveShoot(shoot: ShootResourceQuery) {
        viewModelScope.launch { getSaveShoot(shoot) }
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

private fun editShootUiState(
    shootId: String,
    shootsRepository: ShootsRepository,
    moodboardsRepository: MoodboardsRepository,
    contactsRepository: ContactsRepository,
    locationsRepository: LocationsRepository,
    tasksRepository: TasksRepository
): Flow<EditShootUiState> {

    // Observe location
    val shootsStream: Flow<List<ShootResource>> = if (shootId != "null") {
        shootsRepository.getShootResources(
            ShootResourceEntityQuery(filterShootIds = setOf(element = shootId)),
        )
    } else {
        flowOf(emptyList())
    }

    return shootsStream.flatMapLatest { shoots ->
        val shootResource = shoots.firstOrNull()


        val moodboardsStream: Flow<List<MoodboardResource>> = if (shootResource != null) {
            moodboardsRepository.getMoodboardResources(
                MoodboardResourceEntityQuery(filterMoodboardIds = shootResource.moodboards.toSet()),
            )
        } else {
            flowOf(emptyList())
        }


        val contactsStream: Flow<List<ContactResource>> = if (shootResource != null) {
            contactsRepository.getContactResources(
                ContactResourceEntityQuery(filterContactIds = shootResource.contacts.toSet()),
            )
        } else {
            flowOf(emptyList())
        }

        val locationsStream: Flow<List<LocationResource>> = if (shootResource != null) {
            locationsRepository.getLocationResources(
                LocationResourceEntityQuery(filterLocationIds = shootResource.locations.toSet()),
            )
        } else {
            flowOf(emptyList())
        }

        val tasksStream: Flow<List<TaskResource>> = if (shootResource != null) {
            tasksRepository.getTaskResources(
                TaskResourceEntityQuery(filterTaskIds = shootResource.tasks.toSet()),
            )
        } else {
            flowOf(emptyList())
        }
        return@flatMapLatest combine(
            shootsStream,
            moodboardsStream,
            contactsStream,
            locationsStream,
            tasksStream,
            ::ShootResourceWithData,
        )
    }.asResult()
        .map { shootWithDataResult ->
            when (shootWithDataResult) {
                is Result.Success -> {
                    val (shoot, moodboards, contacts, locations, tasks) = shootWithDataResult.data
                    EditShootUiState.Success(
                        shoot = shoot.firstOrNull(),
                        moodboards = moodboards,
                        contacts = contacts,
                        locations = locations,
                        tasks = tasks,
                    )
                }

                is Result.Loading -> {
                    EditShootUiState.Loading
                }

                is Result.Error -> {
                    EditShootUiState.Error
                }
            }
        }
}

sealed interface EditShootUiState {
    data class Success(
        val shoot: ShootResource?,
        val moodboards: List<MoodboardResource>,
        val contacts: List<ContactResource>,
        val locations: List<LocationResource>,
        val tasks: List<TaskResource>
    ) : EditShootUiState

    data object Error : EditShootUiState
    data object Loading : EditShootUiState
}

sealed interface EditShootAction {
    data class SaveShoot(val shoot: ShootResourceQuery) : EditShootAction
    data class SaveSelectedImageUris(val uris: List<String>) : EditShootAction
    data class RemoveSelectedImageUri(val index: Int) : EditShootAction
}
