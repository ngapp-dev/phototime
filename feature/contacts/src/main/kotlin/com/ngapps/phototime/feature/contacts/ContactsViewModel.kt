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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.data.repository.contacts.ContactResourceEntityQuery
import com.ngapps.phototime.core.data.repository.contacts.ContactsRepository
import com.ngapps.phototime.core.data.repository.user.UserRepository
import com.ngapps.phototime.core.data.util.SyncManager
import com.ngapps.phototime.core.domain.contacts.GetDeleteContactUseCase
import com.ngapps.phototime.core.model.data.contact.ContactResource
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
class ContactsViewModel @Inject constructor(
    syncManager: SyncManager,
    userRepository: UserRepository,
    contactsRepository: ContactsRepository,
    private val getDeleteContact: GetDeleteContactUseCase,
) : ViewModel() {

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val contactsUiState: StateFlow<ContactsUiState> = contactsUiState(
        userRepository = userRepository,
        contactsRepository = contactsRepository,
    )
        .map(ContactsUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ContactsUiState.Loading,
        )

    private val _viewEvents = MutableSharedFlow<ContactsViewEvent>()
    val viewEvents: SharedFlow<ContactsViewEvent> = _viewEvents.asSharedFlow()

    fun triggerAction(action: ContactsAction) = when (action) {
        is ContactsAction.DeleteContact -> deleteContact(action.contactId)
    }

    private fun deleteContact(contactId: String) {
        viewModelScope.launch {
            getDeleteContact(contactId).checkResult(
                onSuccess = {
                    _viewEvents.emit(ContactsViewEvent.Message("Delete success"))
                },
                onError = {
                    _viewEvents.emit(ContactsViewEvent.Message(it))
                },
            )
        }
    }
}

private fun contactsUiState(
    userRepository: UserRepository,
    contactsRepository: ContactsRepository,
): Flow<Map<String, List<ContactResource>>> {
    val userStream: Flow<UserResource> = userRepository.getUserResource()

    return userStream.flatMapLatest { userResource ->
        contactsRepository.getContactResources(
//            query = ContactResourceEntityQuery(
//                filterContactCategories = userResource.categories.contact.toSet(),
//            ),
        ).map { contactResources ->
            val categoriesWithContacts = mutableMapOf<String, List<ContactResource>>()

//            userResource.categories.contact.forEach { category ->
//                categoriesWithContacts[category] = emptyList()
//            }

            contactResources.forEach { contactResource ->
                categoriesWithContacts[contactResource.category] = emptyList()
            }

            contactResources.forEach { contactResource ->
                val category = contactResource.category
                val contacts =
                    categoriesWithContacts.getOrDefault(category, emptyList()) + contactResource
                categoriesWithContacts[category] = contacts
            }
            categoriesWithContacts
        }
    }
}


sealed interface ContactsUiState {
    data object Loading : ContactsUiState
    data class Success(
        val feed: Map<String, List<ContactResource>>,
    ) : ContactsUiState
}

sealed class ContactsViewEvent {
    data class Message(val message: String) : ContactsViewEvent()
}

sealed interface ContactsAction {
    data class DeleteContact(val contactId: String) : ContactsAction
}

