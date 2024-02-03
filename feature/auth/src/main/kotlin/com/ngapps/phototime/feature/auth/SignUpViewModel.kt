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

package com.ngapps.phototime.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapps.phototime.core.domain.auth.GetSignUpUseCase
import com.ngapps.phototime.core.model.data.auth.signup.SignUpResourceQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val getSignUp: GetSignUpUseCase,
) : ViewModel() {

    private val _signUpUiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Error)
    val signInUpState: StateFlow<SignUpUiState> = _signUpUiState

    private val _viewEvents = MutableSharedFlow<SignUpEvent>()
    val viewEvents: SharedFlow<SignUpEvent> = _viewEvents

    init {
        _signUpUiState.value = SignUpUiState.Error
    }

    fun triggerAction(action: SignUpAction) = when (action) {
        is SignUpAction.SignUp -> doSignUp(action.signUpResourceQuery)
    }

    private fun doSignUp(signUpResourceQuery: SignUpResourceQuery) {
        viewModelScope.launch {
            _signUpUiState.value = SignUpUiState.Loading
            getSignUp(signUpResourceQuery).checkResult(
                onSuccess = {
                    _viewEvents.emit(SignUpEvent.Message("Sign up successful"))
                    _signUpUiState.value = SignUpUiState.Success
                },
                onError = {
                    _signUpUiState.value = SignUpUiState.Error
                    _viewEvents.emit(SignUpEvent.Message(it))
                },
            )
        }
    }
}

sealed interface SignUpUiState {
    data object Success : SignUpUiState
    data object Loading : SignUpUiState
    data object Error : SignUpUiState

}

sealed class SignUpEvent {
    data class Message(val message: String) : SignUpEvent()
}

sealed interface SignUpAction {
    data class SignUp(
        val signUpResourceQuery: SignUpResourceQuery,
    ) : SignUpAction
}