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

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.ngapps.phototime.core.designsystem.component.PtButton
import com.ngapps.phototime.core.designsystem.component.PtDivider
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtPasswordTextField
import com.ngapps.phototime.core.designsystem.component.PtTextButton
import com.ngapps.phototime.core.designsystem.component.PtTextFieldWithErrorState
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.model.data.auth.signup.SignUpResourceQuery
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SignUpRoute(
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel = hiltViewModel(),
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignInClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {

    val googleSignInClient by remember { signInViewModel.googleSignInClient }
    val signUpUiState by viewModel.signInUpState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is SignUpEvent.Message -> onShowSnackbar.invoke(event.message, null)
            }
        }
    }

    SignUpScreen(
        signUpUiState = signUpUiState,
        googleSignInClient = googleSignInClient,
        modifier = modifier,
        onSignUpActionClick = { viewModel.triggerAction(SignUpAction.SignUp(it)) },
        onGoogleSignInCompleted = { signInViewModel.triggerAction(SignInAction.GoogleSignIn(it)) },
        onSignUpSuccess = onSignUpSuccess,
        onSignInClick = onSignInClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpScreen(
    signUpUiState: SignUpUiState,
    googleSignInClient: GoogleSignInClient,
    onSignUpActionClick: (SignUpResourceQuery) -> Unit,
    onGoogleSignInCompleted: (String) -> Unit,
    onSignUpSuccess: () -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(Modifier.fillMaxSize()) {
        PtTopAppBar(
            titleRes = R.string.title_sign_up,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )
        Box(Modifier.fillMaxSize()) {
            when (signUpUiState) {
                SignUpUiState.Success -> {
                    LaunchedEffect(key1 = Unit) { onSignUpSuccess() }
                }

                SignUpUiState.Loading -> {
                    this@Column.AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { fullHeight -> -fullHeight },
                        ) + fadeIn(),
                        exit = slideOutVertically(
                            targetOffsetY = { fullHeight -> -fullHeight },
                        ) + fadeOut(),
                    ) {
                        val loadingContentDescription = stringResource(id = R.string.loading)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                        ) {
                            PtOverlayLoadingWheel(
                                modifier = Modifier.align(Alignment.Center),
                                contentDesc = loadingContentDescription,
                            )
                        }
                    }
                }

                else -> {}
            }
            SignUpContent(
                modifier = modifier,
                enabled = true,
                googleSignInClient = googleSignInClient,
                onSignUpActionClick = onSignUpActionClick,
                onSignInClick = onSignInClick,
                onGoogleSignInCompleted = onGoogleSignInCompleted,
                onGoogleSignInError = {},
            )
        }
    }

    TrackScreenViewEvent(screenName = "Sign Up screen")
}

@VisibleForTesting
@Composable
internal fun SignUpContent(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    googleSignInClient: GoogleSignInClient,
    onSignUpActionClick: (SignUpResourceQuery) -> Unit,
    onSignInClick: () -> Unit,
    onGoogleSignInCompleted: (String) -> Unit,
    onGoogleSignInError: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordRepeat by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 32.dp),
    ) {
        Text(
            text = stringResource(R.string.please_sign_up),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        PtDivider()
        Spacer(Modifier.height(16.dp))
        PtTextFieldWithErrorState(
            enabled = enabled,
            text = username,
            label = "Username",
            isError = false,
            validate = { },
            errorMessage = "",
            textResult = { username = it },
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        PtTextFieldWithErrorState(
            enabled = enabled,
            text = email,
            label = "Email",
            isError = false,
            validate = { },
            errorMessage = "",
            textResult = { email = it },
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        PtPasswordTextField(
            enabled = enabled,
            text = password,
            label = "Password",
            isError = false,
            validate = { },
            errorMessage = "",
            textResult = { password = it },
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        PtPasswordTextField(
            enabled = enabled,
            text = passwordRepeat,
            label = "Repeat password",
            isError = false,
            validate = { },
            errorMessage = "",
            textResult = { passwordRepeat = it },
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(40.dp))
        PtButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && passwordRepeat.isNotBlank(),
            onClick = {
                onSignUpActionClick(
                    SignUpResourceQuery(
                        username,
                        email,
                        password,
                        passwordRepeat,
                    ),
                )
            },
        ) {
            Text(text = stringResource(R.string.title_sign_up))
        }
        Spacer(modifier = Modifier.height(12.dp))
        ButtonGoogleSignIn(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = enabled,
            onGoogleSignInCompleted = onGoogleSignInCompleted,
            onError = onGoogleSignInError,
            googleSignInClient = googleSignInClient,
        )
        PtTextButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            onClick = { onSignInClick.invoke() },
        ) {
            Text(text = stringResource(id = R.string.sign_in))
        }
    }
}

