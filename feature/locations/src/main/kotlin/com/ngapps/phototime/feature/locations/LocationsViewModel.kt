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

package com.ngapps.phototime.feature.locations

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.data.repository.locations.LocationResourceEntityQuery
import com.ngapps.phototime.core.data.repository.locations.LocationsRepository
import com.ngapps.phototime.core.data.repository.user.UserRepository
import com.ngapps.phototime.core.data.util.SyncManager
import com.ngapps.phototime.core.domain.locations.GetDeleteLocationUseCase
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.user.UserResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationsViewModel @Inject constructor(
    syncManager: SyncManager,
    userRepository: UserRepository,
    locationsRepository: LocationsRepository,
    private val getDeleteLocation: GetDeleteLocationUseCase,
) : ViewModel() {

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val locationsFeedState: StateFlow<LocationsUiState> = locationsUiState(
        userRepository = userRepository,
        locationsRepository = locationsRepository,
    )
        .map(LocationsUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LocationsUiState.Loading,
        )

    private val _viewEvents = MutableSharedFlow<LocationsViewEvent>()
    val viewEvents: SharedFlow<LocationsViewEvent> = _viewEvents.asSharedFlow()

    fun triggerAction(action: LocationsAction) = when (action) {
        is LocationsAction.DeleteLocation -> deleteLocation(action.locationId)
    }

    private fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            getDeleteLocation(locationId).checkResult(
                onSuccess = {
                    _viewEvents.emit(LocationsViewEvent.Message("Delete success"))
                },
                onError = {
                    _viewEvents.emit(LocationsViewEvent.Message(it))
                },
            )
        }
    }
}

private fun locationsUiState(
    userRepository: UserRepository,
    locationsRepository: LocationsRepository,
): Flow<Map<String, List<LocationResource>>> {
    val userStream: Flow<UserResource> = userRepository.getUserResource()

    return userStream.flatMapLatest { userResource ->
        locationsRepository.getLocationResources(
//            query = LocationResourceEntityQuery(
//                filterLocationCategories = userResource.categories.location.toSet(),
//            ),
        ).map { locationResources ->
            val categoriesWithLocations = mutableMapOf<String, List<LocationResource>>()

//            userResource.categories.location.forEach { category ->
//                categoriesWithLocations[category] = emptyList()
//            }

            locationResources.forEach { locationResource ->
                categoriesWithLocations[locationResource.category] = emptyList()
            }

            locationResources.forEach { locationResource ->
                val category = locationResource.category
                val locations =
                    categoriesWithLocations.getOrDefault(category, emptyList()) + locationResource
                categoriesWithLocations[category] = locations
            }

            categoriesWithLocations
        }
    }
}


sealed interface LocationsUiState {
    data object Loading : LocationsUiState
    data class Success(
        val feed: Map<String, List<LocationResource>>
    ) : LocationsUiState
}

sealed class LocationsViewEvent {
    data class Message(val message: String) : LocationsViewEvent()
}

sealed interface LocationsAction {
    data class DeleteLocation(val locationId: String) : LocationsAction
}
