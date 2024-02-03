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
import com.ngapps.phototime.core.domain.GetDownloadUseCase
import com.ngapps.phototime.core.domain.contacts.GetDeleteContactUseCase
import com.ngapps.phototime.core.model.data.contact.ContactResource
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ngapps.phototime.core.result.Result

@HiltViewModel
class SingleContactViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    userDataRepository: UserDataRepository,
    contactsRepository: ContactsRepository,
    private val getImageDownload: GetDownloadUseCase,
    private val getDeleteContact: GetDeleteContactUseCase,
) : ViewModel() {

    private val contactArgs: ContactArgs = ContactArgs(savedStateHandle, stringDecoder)

    val contactId = contactArgs.contactId

    private val _contactUiState = MutableStateFlow<ContactUiState>(ContactUiState.Error)
    var contactUiState: StateFlow<ContactUiState> = _contactUiState

    private val _viewEvents = MutableSharedFlow<SingleContactViewEvent>()
    val viewEvents: SharedFlow<SingleContactViewEvent> = _viewEvents.asSharedFlow()

    init {
        contactUiState = contactUiState(
            contactId = contactArgs.contactId,
            userDataRepository = userDataRepository,
            contactsRepository = contactsRepository,
        )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ContactUiState.Loading,
            )
    }

    fun triggerAction(action: SingleContactAction) = when (action) {
        is SingleContactAction.DeleteContact -> deleteContact(action.contactId)
        is SingleContactAction.DownloadImage -> downloadImage(action.inputUrl)
    }

    private fun deleteContact(contactId: String) {
        viewModelScope.launch {
            _contactUiState.value = ContactUiState.Loading
            getDeleteContact(contactId).checkResult(
                onSuccess = {
                    _viewEvents.emit(SingleContactViewEvent.Message("Delete success"))
                    _viewEvents.emit(SingleContactViewEvent.NavigateBack)
                },
                onError = {
                    _contactUiState.value = ContactUiState.Error
                    _viewEvents.emit(SingleContactViewEvent.Message(it))
                },
            )
        }
    }

    private fun downloadImage(inputUrl: String) {
        getImageDownload(inputUrl)
    }
}

private fun contactUiState(
    contactId: String,
    userDataRepository: UserDataRepository,
    contactsRepository: ContactsRepository,
): Flow<ContactUiState> {

    // Observe the user location, as it could change over time.
    val userLocationStream: Flow<Pair<String, String>> =
        userDataRepository.userData
            .map { it.userLocation }

    // Observe location
    val contactStream: Flow<List<ContactResource>> =
        contactsRepository.getContactResources(
            ContactResourceEntityQuery(filterContactIds = setOf(contactId)),
        )

    return combine(
        userLocationStream,
        contactStream,
        ::Pair,
    )
        .asResult()
        .map { contactWithUserLocationResult ->
            when (contactWithUserLocationResult) {
                is Result.Success -> {
                    val (userLocation, contact) = contactWithUserLocationResult.data
                    ContactUiState.Success(
                        contact = contact.firstOrNull(),
                        userLocation = userLocation,
                    )
                }

                is Result.Loading -> {
                    ContactUiState.Loading
                }

                is Result.Error -> {
                    ContactUiState.Error
                }
            }
        }
}

sealed interface ContactUiState {
    data class Success(
        val contact: ContactResource?,
        val userLocation: Pair<String, String>
    ) : ContactUiState

    data object Error : ContactUiState
    data object Loading : ContactUiState
}

sealed class SingleContactViewEvent {
    data class Message(val message: String) : SingleContactViewEvent()
    data object NavigateBack : SingleContactViewEvent()
}

sealed interface SingleContactAction {
    data class DeleteContact(val contactId: String) : SingleContactAction
    data class DownloadImage(val inputUrl: String) : SingleContactAction
}
