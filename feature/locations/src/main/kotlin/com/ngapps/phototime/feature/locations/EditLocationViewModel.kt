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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.data.repository.locations.LocationResourceEntityQuery
import com.ngapps.phototime.core.data.repository.locations.LocationsRepository
import com.ngapps.phototime.core.data.repository.user.UserRepository
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.core.domain.GetSavePhotosUseCase
import com.ngapps.phototime.core.domain.locations.GetSaveCategoriesUseCase
import com.ngapps.phototime.core.domain.locations.GetSaveLocationUseCase
import com.ngapps.phototime.core.domain.locations.GetSearchAutocompleteUseCase
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.location.LocationResourceQuery
import com.ngapps.phototime.core.result.Result
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLocationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    userDataRepository: UserDataRepository,
    locationsRepository: LocationsRepository,
    userRepository: UserRepository,
    private val getSaveLocation: GetSaveLocationUseCase,
    private val getSavePhotos: GetSavePhotosUseCase,
    private val getSearchAutocomplete: GetSearchAutocompleteUseCase,
    private val getSaveCategories: GetSaveCategoriesUseCase
) : ViewModel() {

    private val locationArg: LocationArg = LocationArg(savedStateHandle, stringDecoder)

    var locationId = locationArg.locationId

    private val _autocompleteSearch = MutableStateFlow<List<String>>(emptyList())
    val autocompleteSearch: StateFlow<List<String>> = _autocompleteSearch

    val editLocationUiState: StateFlow<EditLocationUiState> = editLocationUiState(
        locationId = locationArg.locationId,
        userDataRepository = userDataRepository,
        locationsRepository = locationsRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EditLocationUiState.Loading,
        )

    val categoriesUiState: StateFlow<EditCategoriesUiState> = editCategoriesUiState(
        userRepository = userRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EditCategoriesUiState.Loading,
        )

    private val _selectedImageUris = MutableStateFlow<List<String>>(emptyList())
    val selectedImageUris: StateFlow<List<String>> = _selectedImageUris


    private val _searchQueryChannel = MutableStateFlow<String>("")

    private val _viewEvents = MutableSharedFlow<EditLocationViewEvent>()
    val viewEvents: SharedFlow<EditLocationViewEvent> = _viewEvents.asSharedFlow()

    fun triggerAction(action: EditLocationAction) = when (action) {
        is EditLocationAction.SaveLocation -> saveLocation(action.location)
        is EditLocationAction.SaveSelectedImageUris -> saveSelectedImageUris(action.uris)
        is EditLocationAction.RemoveSelectedImageUri -> removeSelectedImageUri(action.index)
        is EditLocationAction.UpdateLocationCategories -> updateLocationCategories(action.categories)
        is EditLocationAction.SearchAutocomplete -> searchAutocomplete(action.query)
    }

    private fun saveLocation(location: LocationResourceQuery) {
        viewModelScope.launch {
            getSaveLocation(location).checkResult(
                onSuccess = {
                    getSavePhotos("123", selectedImageUris.value)
                    Log.e("saveLocation", selectedImageUris.value.toString())
                    _viewEvents.emit(EditLocationViewEvent.Message("Save success"))
                    _viewEvents.emit(EditLocationViewEvent.NavigateBack)
                },
                onError = {
                    _viewEvents.emit(EditLocationViewEvent.Message(it))
                },
            )

        }
    }

    private fun updateLocationCategories(categories: List<String>) {
        viewModelScope.launch {
            getSaveCategories(categories).checkResult(
                onSuccess = {
                    _viewEvents.emit(EditLocationViewEvent.Message("Save success"))
                },
                onError = {
                    _viewEvents.emit(EditLocationViewEvent.Message(it))
                },
            )
        }
    }

    private fun saveSelectedImageUris(uris: List<String>) {
        val currentList = _selectedImageUris.value.toMutableList()
        currentList.addAll(uris)
        _selectedImageUris.value = currentList
    }

    private fun removeSelectedImageUri(index: Int) {
        val currentList = _selectedImageUris.value.toMutableList()
        if (index in 0 until currentList.size) {
            currentList.removeAt(index)
            _selectedImageUris.value = currentList
        }
    }

    private fun searchAutocomplete(query: String) {
        _searchQueryChannel.value = query
    }

//    init {
//        viewModelScope.launch {
//            _searchQueryChannel
//                .debounce(500)
//                .distinctUntilChanged()
//                .collect { query ->
//                    // Выполняем поиск при каждом изменении значения query
//                    getSearchAutocomplete(query = query).checkResult(
//                        onSuccess = { result ->
//                            val addresses = result.map { it.address }
//                            _autocompleteSearch.value = addresses
//                        },
//                        onError = { error ->
//                            _viewEvents.emit(EditLocationViewEvent.Message(error))
//                        },
//                    )
//                }
//        }
//    }
}


private fun editLocationUiState(
    locationId: String,
    userDataRepository: UserDataRepository,
    locationsRepository: LocationsRepository,
): Flow<EditLocationUiState> {

    // Observe location
    val locationStream: Flow<List<LocationResource>> = if (locationId != "0") {
        locationsRepository.getLocationResources(
            LocationResourceEntityQuery(filterLocationIds = setOf(element = locationId)),
        )
    } else {
        flowOf(emptyList())
    }

    // Observe the user location, as it could change over time.
    val userLocationStream: Flow<Pair<String, String>> =
        userDataRepository.userData.map { it.userLocation }


    return combine(
        locationStream,
        userLocationStream,
        ::Pair,
    )
        .asResult()
        .map { locationWithUserLocationResult ->
            when (locationWithUserLocationResult) {
                is Result.Success -> {
                    val (location, userLocation) = locationWithUserLocationResult.data
                    EditLocationUiState.Success(
                        location = location.firstOrNull(),
                        userLocation = userLocation,
                    )
                }

                is Result.Loading -> {
                    EditLocationUiState.Loading
                }

                is Result.Error -> {
                    EditLocationUiState.Error
                }
            }
        }
}

fun editCategoriesUiState(
    userRepository: UserRepository,
): Flow<EditCategoriesUiState> {

    return userRepository.getUserResource()
        .asResult()
        .map { locationCategories ->
            when (locationCategories) {
                is Result.Success -> {
                    EditCategoriesUiState.Success(locationCategories.data.categories.location)
                }

                is Result.Loading -> {
                    EditCategoriesUiState.Loading
                }

                is Result.Error -> {
                    EditCategoriesUiState.Error
                }
            }
        }
}

sealed interface EditLocationUiState {
    data class Success(
        val location: LocationResource?,
        val userLocation: Pair<String, String>,
    ) : EditLocationUiState

    data object Error : EditLocationUiState
    data object Loading : EditLocationUiState
}

sealed interface EditCategoriesUiState {
    data class Success(
        val categories: List<String>,
    ) : EditCategoriesUiState

    data object Error : EditCategoriesUiState
    data object Loading : EditCategoriesUiState
}

sealed class EditLocationViewEvent {
    data class Message(val message: String) : EditLocationViewEvent()
    data object NavigateBack : EditLocationViewEvent()
}

sealed interface EditLocationAction {
    data class SaveLocation(val location: LocationResourceQuery) : EditLocationAction
    data class SaveSelectedImageUris(val uris: List<String>) : EditLocationAction
    data class RemoveSelectedImageUri(val index: Int) : EditLocationAction
    data class UpdateLocationCategories(val categories: List<String>) : EditLocationAction
    data class SearchAutocomplete(val query: String) : EditLocationAction
}
