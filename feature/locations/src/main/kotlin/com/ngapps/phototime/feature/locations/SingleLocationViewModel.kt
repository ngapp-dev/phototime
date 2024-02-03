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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.data.repository.locations.LocationResourceEntityQuery
import com.ngapps.phototime.core.data.repository.locations.LocationsRepository
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.core.domain.GetDownloadUseCase
import com.ngapps.phototime.core.domain.locations.GetDeleteLocationUseCase
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.result.asResult
import com.ngapps.phototime.feature.locations.navigation.LocationArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ngapps.phototime.core.result.Result

@HiltViewModel
class SingleLocationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    userDataRepository: UserDataRepository,
    locationsRepository: LocationsRepository,
    private val getImageDownload: GetDownloadUseCase,
    private val getDeleteLocation: GetDeleteLocationUseCase,
) : ViewModel() {

    private val locationArg: LocationArg = LocationArg(savedStateHandle, stringDecoder)

    val locationId = locationArg.locationId

    private val _locationUiState = MutableStateFlow<LocationUiState>(LocationUiState.Error)
    var locationUiState: StateFlow<LocationUiState> = _locationUiState

    private val _viewEvents = MutableSharedFlow<SingleLocationViewEvent>()
    val viewEvents: SharedFlow<SingleLocationViewEvent> = _viewEvents.asSharedFlow()

    init {
        locationUiState = locationUiState(
            locationId = locationArg.locationId,
            userDataRepository = userDataRepository,
            locationsRepository = locationsRepository,
        )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LocationUiState.Loading,
            )
    }

    fun triggerAction(action: SingleLocationAction) = when (action) {
        is SingleLocationAction.DeleteLocation -> doDeleteLocation(action.locationId)
        is SingleLocationAction.DownloadImage -> doDownloadImage(action.inputUrl)
    }

    private fun doDeleteLocation(locationId: String) {
        viewModelScope.launch {
            _locationUiState.value = LocationUiState.Loading
            getDeleteLocation(locationId).checkResult(
                onSuccess = {
                    _viewEvents.emit(SingleLocationViewEvent.Message("Delete success"))
                    _viewEvents.emit(SingleLocationViewEvent.NavigateBack)
                },
                onError = {
                    _locationUiState.value = LocationUiState.Error
                    _viewEvents.emit(SingleLocationViewEvent.Message(it))
                },
            )
        }
    }

    private fun doDownloadImage(inputUrl: String) {
        getImageDownload(inputUrl)
    }
}


private fun locationUiState(
    locationId: String,
    locationsRepository: LocationsRepository,
    userDataRepository: UserDataRepository,
): Flow<LocationUiState> {

    // Observe the user location, as it could change over time.
    val userLocationStream: Flow<Pair<String, String>> =
        userDataRepository.userData
            .map { it.userLocation }

    // Observe location
    val locationStream: Flow<List<LocationResource>> =
        locationsRepository.getLocationResources(
            LocationResourceEntityQuery(filterLocationIds = setOf(element = locationId)),
        )

    return combine(
        userLocationStream,
        locationStream,
        ::Pair,
    )
        .asResult()
        .map { locationWithUserLocationResult ->
            when (locationWithUserLocationResult) {
                is Result.Success -> {
                    val (userLocation, location) = locationWithUserLocationResult.data
                    LocationUiState.Success(
                        location = location.firstOrNull(),
                        userLocation = userLocation,
                    )
                }

                Result.Loading -> {
                    LocationUiState.Loading
                }

                is Result.Error -> {
                    LocationUiState.Error
                }
            }
        }
}

sealed interface LocationUiState {
    data class Success(
        val location: LocationResource?,
        val userLocation: Pair<String, String>
    ) : LocationUiState

    data object Error : LocationUiState
    data object Loading : LocationUiState
}

sealed class SingleLocationViewEvent {
    data class Message(val message: String) : SingleLocationViewEvent()
    data object NavigateBack : SingleLocationViewEvent()
}

sealed interface SingleLocationAction {
    data class DeleteLocation(val locationId: String) : SingleLocationAction
    data class DownloadImage(val inputUrl: String) : SingleLocationAction
}
