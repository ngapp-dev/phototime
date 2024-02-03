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
import com.ngapps.phototime.core.domain.GetDownloadUseCase
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.moodboard.MoodboardResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.shoot.ShootResourceWithData
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.result.asResult
import com.ngapps.phototime.feature.tasks.navigation.ShootArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ngapps.phototime.core.result.Result

@HiltViewModel
class SingleShootViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    shootsRepository: ShootsRepository,
    moodboardsRepository: MoodboardsRepository,
    contactsRepository: ContactsRepository,
    locationsRepository: LocationsRepository,
    tasksRepository: TasksRepository,
    private val getImageDownload: GetDownloadUseCase,
) : ViewModel() {

    private val shootArgs: ShootArgs = ShootArgs(savedStateHandle, stringDecoder)

    val shootId = shootArgs.shootId

    val singleShootUiState: StateFlow<SingleShootUiState> = shootUiState(
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
            initialValue = SingleShootUiState.Loading,
        )

    fun triggerAction(action: SingleShootAction) = when (action) {
        is SingleShootAction.DeleteShoot -> doDeleteShoot(action.shootId)
        is SingleShootAction.DownloadImage -> doDownloadImage(action.inputUrl)
    }

    private fun doDeleteShoot(shootId: String) {
        viewModelScope.launch { }
    }

    private fun doDownloadImage(inputUrl: String) {
        getImageDownload(inputUrl)
    }
}

private fun shootUiState(
    shootId: String,
    shootsRepository: ShootsRepository,
    moodboardsRepository: MoodboardsRepository,
    contactsRepository: ContactsRepository,
    locationsRepository: LocationsRepository,
    tasksRepository: TasksRepository
): Flow<SingleShootUiState> {

    val shootsStream: Flow<List<ShootResource>> =
        shootsRepository.getShootResources(
            ShootResourceEntityQuery(filterShootIds = setOf(element = shootId)),
        )

    return shootsStream.flatMapLatest { shoots ->
        val shootResource = shoots.let { it[0] }

        val moodboardsStream: Flow<List<MoodboardResource>> =
            moodboardsRepository.getMoodboardResources(
                MoodboardResourceEntityQuery(filterMoodboardIds = shootResource.moodboards.toSet()),
            )

        val contactsStream: Flow<List<ContactResource>> =
            contactsRepository.getContactResources(
                ContactResourceEntityQuery(filterContactIds = shootResource.contacts.toSet()),
            )
        val locationsStream: Flow<List<LocationResource>> =
            locationsRepository.getLocationResources(
                LocationResourceEntityQuery(filterLocationIds = shootResource.locations.toSet()),
            )

        val tasksStream: Flow<List<TaskResource>> =
            tasksRepository.getTaskResources(
                TaskResourceEntityQuery(filterTaskIds = shootResource.tasks.toSet()),
            )
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
                    SingleShootUiState.Success(
                        shoot = shoot.let { it[0] },
                        moodboards = moodboards,
                        contacts = contacts,
                        locations = locations,
                        tasks = tasks,
                    )
                }

                is Result.Loading -> {
                    SingleShootUiState.Loading
                }

                is Result.Error -> {
                    SingleShootUiState.Error
                }
            }
        }
}

sealed interface SingleShootUiState {
    data class Success(
        val shoot: ShootResource,
        val moodboards: List<MoodboardResource>,
        val contacts: List<ContactResource>,
        val locations: List<LocationResource>,
        val tasks: List<TaskResource>
    ) : SingleShootUiState

    data object Error : SingleShootUiState
    data object Loading : SingleShootUiState
}

sealed interface SingleShootAction {
    data class DeleteShoot(val shootId: String) : SingleShootAction
    data class DownloadImage(val inputUrl: String) : SingleShootAction
}
