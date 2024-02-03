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

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.ngapps.phototime.core.GoogleSignInResultContract
import com.ngapps.phototime.core.designsystem.component.PtButton
import com.ngapps.phototime.core.designsystem.component.PtDivider
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtPasswordTextField
import com.ngapps.phototime.core.designsystem.component.PtTextButton
import com.ngapps.phototime.core.designsystem.component.PtTextFieldWithErrorState
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.model.data.auth.signin.SignInResourceQuery
import com.ngapps.phototime.core.ui.TrackScreenViewEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
internal fun SignInRoute(
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
    onSignUpClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val googleSignInClient by remember { viewModel.googleSignInClient }
    val signInUiState: SignInUiState by viewModel.signInUiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.viewEvents) {
        viewModel.viewEvents.collectLatest { event ->
            when (event) {
                is SignInViewEvent.Message -> onShowSnackbar.invoke(event.message, null)
            }
        }
    }

    SignInScreen(
        signInUiState = signInUiState,
        googleSignInClient = googleSignInClient,
        modifier = modifier,
        onSignInActionClick = { viewModel.triggerAction(SignInAction.SignIn(it)) },
        onGoogleSignInCompleted = { viewModel.triggerAction(SignInAction.GoogleSignIn(it)) },
        onSignInSuccess = onSignInSuccess,
        onSignUpClick = onSignUpClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@VisibleForTesting
@Composable
internal fun SignInScreen(
    signInUiState: SignInUiState,
    googleSignInClient: GoogleSignInClient,
    onSignInActionClick: (SignInResourceQuery) -> Unit,
    onGoogleSignInCompleted: (String) -> Unit,
    onSignInSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {


    Column(Modifier.fillMaxSize()) {
        PtTopAppBar(
            titleRes = R.string.title_sign_in,
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )
        Box(Modifier.fillMaxSize()) {
            when (signInUiState) {
                SignInUiState.Success -> LaunchedEffect(key1 = Unit) { onSignInSuccess() }
                SignInUiState.Loading -> {
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

                is SignInUiState.Error -> {

                }
            }
            SignInContent(
                modifier = modifier,
                enabled = true,
                googleSignInClient = googleSignInClient,
                onSignInActionClick = onSignInActionClick,
                onSignUpClick = onSignUpClick,
                onGoogleSignInCompleted = onGoogleSignInCompleted,
                onGoogleSignInError = {},
            )
        }
    }

    TrackScreenViewEvent(screenName = "Sign In screen")
}

@VisibleForTesting
@Composable
internal fun SignInContent(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    googleSignInClient: GoogleSignInClient,
    onSignInActionClick: (SignInResourceQuery) -> Unit,
    onSignUpClick: () -> Unit,
    onGoogleSignInCompleted: (String) -> Unit,
    onGoogleSignInError: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 32.dp),
    ) {
        Text(
            text = stringResource(R.string.please_sign_in),
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
        Spacer(Modifier.height(40.dp))
        PtButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = username.isNotBlank() && password.isNotBlank(),
            onClick = { onSignInActionClick(SignInResourceQuery(username, password)) },
        ) {
            Text(text = stringResource(R.string.title_sign_in))
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
            onClick = { onSignUpClick.invoke() },
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }
    }
}

@Composable
fun ButtonGoogleSignIn(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onGoogleSignInCompleted: (String) -> Unit,
    onError: () -> Unit,
    googleSignInClient: GoogleSignInClient,
) {
    val coroutineScope = rememberCoroutineScope()
    val signInRequestCode = 1

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = GoogleSignInResultContract(googleSignInClient)) {
            try {
                val account = it?.getResult(ApiException::class.java)
                if (account == null) {
                    onError()
                } else {
                    coroutineScope.launch {
                        onGoogleSignInCompleted(account.idToken ?: "id token empty")
                    }
                }
            } catch (e: ApiException) {
                onError()
            }
        }

    PtButton(
        modifier = modifier,
        enabled = enabled,
        onClick = { authResultLauncher.launch(signInRequestCode) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = PtIcons.Google),
                contentDescription = stringResource(R.string.continue_with_google),
            )
        },
        text = {
            Text(text = stringResource(R.string.continue_with_google))
        },
    )
}

