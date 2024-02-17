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

import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.ngapps.phototime.core.domain.auth.GetGoogleSignInUseCase
import com.ngapps.phototime.core.domain.auth.GetSignInStatusUseCase
import com.ngapps.phototime.core.domain.auth.GetSignInUseCase
import com.ngapps.phototime.core.model.data.auth.signin.GoogleSignInResourceQuery
import com.ngapps.phototime.core.model.data.auth.signin.SignInResourceQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val getGoogleSignInUseCase: GetGoogleSignInUseCase,
    private val getSignIn: GetSignInUseCase,
    googleSignInClient: GoogleSignInClient,
    getSignInStatus: GetSignInStatusUseCase,
) : ViewModel() {

    val googleSignInClient = mutableStateOf(googleSignInClient)

    private val _signInUiState = MutableStateFlow<SignInUiState>(SignInUiState.Error)
    val signInUiState: StateFlow<SignInUiState> = _signInUiState

    private val _viewEvents = MutableSharedFlow<SignInViewEvent>()
    val viewEvents: SharedFlow<SignInViewEvent> = _viewEvents.asSharedFlow()

    init {
        val tokens = getSignInStatus()
        if (!tokens.token.isNullOrEmpty() && !tokens.refresh.isNullOrEmpty()) {
            _signInUiState.value = SignInUiState.Success
        } else {
            _signInUiState.value = SignInUiState.Error
        }
    }

    fun triggerAction(action: SignInAction) = when (action) {
        is SignInAction.SignIn -> doSignIn(action.signInResourceQuery)
        is SignInAction.GoogleSignIn -> doGoogleSignIn(action.googleToken)
        is SignInAction.SignInDemo -> doSignInDemo()
    }

    private fun doSignIn(signInResourceQuery: SignInResourceQuery) {
        viewModelScope.launch {
            _signInUiState.value = SignInUiState.Loading
            getSignIn(
                SignInResourceQuery(
                    username = signInResourceQuery.username,
                    password = signInResourceQuery.password,
                    fingerprint = Build.FINGERPRINT,
                ),
            ).checkResult(
                onSuccess = {
                    _viewEvents.emit(SignInViewEvent.Message("Welcome"))
                    _signInUiState.value = SignInUiState.Success
                },
                onError = {
                    _signInUiState.value = SignInUiState.Error
                    _viewEvents.emit(SignInViewEvent.Message(it))
                },
            )
        }
    }

    private fun doGoogleSignIn(googleToken: String) {
        viewModelScope.launch {
            _signInUiState.value = SignInUiState.Loading
            getGoogleSignInUseCase(
                GoogleSignInResourceQuery(
                    googleToken,
                    Build.FINGERPRINT,
                ),
            ).checkResult(
                onSuccess = { _signInUiState.value = SignInUiState.Success },
                onError = {
                    _signInUiState.value = SignInUiState.Error
                    _viewEvents.emit(SignInViewEvent.Message(it))
                },
            )
        }
    }

    private fun doSignInDemo() {
        _signInUiState.value = SignInUiState.Success
    }
}

sealed interface SignInUiState {
    data object Success : SignInUiState
    data object Loading : SignInUiState
    data object Error : SignInUiState
}

sealed class SignInViewEvent {
    data class Message(val message: String) : SignInViewEvent()
}

sealed interface SignInAction {
    data class SignIn(val signInResourceQuery: SignInResourceQuery) : SignInAction
    data class GoogleSignIn(val googleToken: String) : SignInAction
    data object SignInDemo : SignInAction
}
