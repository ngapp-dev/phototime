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

package com.ngapps.phototime.feature.contacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.data.repository.UserDataRepository
import com.ngapps.phototime.core.data.repository.contacts.ContactResourceEntityQuery
import com.ngapps.phototime.core.data.repository.contacts.ContactsRepository
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.core.domain.contacts.GetSaveContactUseCase
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.contact.ContactResourceQuery
import com.ngapps.phototime.core.result.asResult
import com.ngapps.phototime.feature.contacts.navigation.ContactArgs
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
import com.ngapps.phototime.core.result.Result

@HiltViewModel
class EditContactViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val userDataRepository: UserDataRepository,
    contactsRepository: ContactsRepository,
    private val getSaveContact: GetSaveContactUseCase
) : ViewModel() {

    private val contactArgs: ContactArgs = ContactArgs(savedStateHandle, stringDecoder)

    val contactId = contactArgs.contactId

    val editContactUiState: StateFlow<EditContactUiState> = editContactUiState(
        contactId = contactArgs.contactId,
        userDataRepository = userDataRepository,
        contactsRepository = contactsRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EditContactUiState.Loading,
        )

    private val _selectedImageUris = MutableStateFlow<List<String>>(emptyList())
    val selectedImageUris: StateFlow<List<String>> = _selectedImageUris

    private val _viewEvents = MutableSharedFlow<EditContactViewEvent>()
    val viewEvents: SharedFlow<EditContactViewEvent> = _viewEvents.asSharedFlow()

    fun triggerAction(action: EditContactAction) = when (action) {
        is EditContactAction.SaveContact -> saveContact(action.contact)
        is EditContactAction.SaveSelectedImageUris -> saveSelectedImageUris(action.uris)
        is EditContactAction.RemoveSelectedImageUri -> removeSelectedImageUri(action.index)
        is EditContactAction.UpdateContactCategories -> updateContactCategories(action.categories)
    }

    private fun saveContact(contact: ContactResourceQuery) {
        viewModelScope.launch {
            getSaveContact(contact).checkResult(
                onSuccess = {
                    _viewEvents.emit(EditContactViewEvent.Message("Save success"))
                    _viewEvents.emit(EditContactViewEvent.NavigateBack)
                },
                onError = {
                    _viewEvents.emit(EditContactViewEvent.Message(it))
                },
            )
        }
    }

    private fun updateContactCategories(categories: List<String>) {
        viewModelScope.launch {

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
}

private fun editContactUiState(
    contactId: String,
    userDataRepository: UserDataRepository,
    contactsRepository: ContactsRepository,
): Flow<EditContactUiState> {

    // Observe the user location, as it could change over time.
    val userLocationStream: Flow<Pair<String, String>> =
        userDataRepository.userData.map { it.userLocation }

    // Observe contact categories with user custom categories.
    val contactCategories = contactsRepository.getContactResourcesUniqueCategories()

    // Observe contact
    val contactStream: Flow<List<ContactResource>> = if (contactId != "0") {
        contactsRepository.getContactResources(
            ContactResourceEntityQuery(filterContactIds = setOf(element = contactId)),
        )
    } else {
        flowOf(emptyList())
    }

    return combine(
        userLocationStream,
        contactCategories,
        contactStream,
        ::Triple,
    )
        .asResult()
        .map { contactWithUserLocationResult ->
            when (contactWithUserLocationResult) {
                is Result.Success -> {
                    val (userLocation, categories, contact) = contactWithUserLocationResult.data
                    EditContactUiState.Success(
                        contact = contact.firstOrNull(),
                        categories = categories.toList(),
                        userLocation = userLocation,
                    )
                }

                is Result.Loading -> {
                    EditContactUiState.Loading
                }

                is Result.Error -> {
                    EditContactUiState.Error
                }
            }
        }
}

sealed interface EditContactUiState {
    data class Success(
        val contact: ContactResource?,
        val categories: List<String>,
        val userLocation: Pair<String, String>
    ) : EditContactUiState

    data object Error : EditContactUiState
    data object Loading : EditContactUiState
}

sealed class EditContactViewEvent {
    data class Message(val message: String) : EditContactViewEvent()
    data object NavigateBack : EditContactViewEvent()
}

sealed interface EditContactAction {
    data class SaveContact(val contact: ContactResourceQuery) : EditContactAction
    data class SaveSelectedImageUris(val uris: List<String>) : EditContactAction
    data class RemoveSelectedImageUri(val index: Int) : EditContactAction
    data class UpdateContactCategories(val categories: List<String>) : EditContactAction
}
