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

package com.ngapps.phototime.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ngapps.phototime.feature.auth.SignInRoute
import com.ngapps.phototime.feature.auth.SignUpRoute

const val signUpRoute = "sign_up_route"
const val signInRoute = "sign_in_route"

fun NavController.navigateToSignUp(navOptions: NavOptions? = null) {
    this.navigate(signUpRoute, navOptions)
}

fun NavGraphBuilder.signUpScreen(
    onSignInClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = signUpRoute) {
        SignUpRoute(
            onSignInClick = onSignInClick,
            onSignUpSuccess = onSignUpSuccess,
            onShowSnackbar = onShowSnackbar,
        )
    }
}

fun NavController.navigateToSignIn(navOptions: NavOptions? = null) {
    this.navigate(signInRoute, navOptions)
}

fun NavGraphBuilder.signInScreen(
    onSignUpClick: () -> Unit,
    onSignInSuccess: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(route = signInRoute) {
        SignInRoute(
            onSignUpClick = onSignUpClick,
            onSignInSuccess = onSignInSuccess,
            onShowSnackbar = onShowSnackbar,
        )
    }
}

