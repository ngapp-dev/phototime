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

package com.ngapps.phototime.core.testing.repository

import com.ngapps.phototime.core.data.repository.auth.AuthRepository
import com.ngapps.phototime.core.model.data.auth.AuthResource
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.auth.signin.GoogleSignInResourceQuery
import com.ngapps.phototime.core.model.data.auth.signin.SignInResourceQuery
import com.ngapps.phototime.core.model.data.auth.signup.SignUpResourceQuery
import com.ngapps.phototime.core.result.DataResult

class TestAuthRepository : AuthRepository {

    override suspend fun getSignIn(
        query: SignInResourceQuery
    ): DataResult<AuthResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getGoogleSignIn(
        query: GoogleSignInResourceQuery
    ): DataResult<AuthResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getSignUp(
        query: SignUpResourceQuery
    ): DataResult<ResponseResource> {
        TODO("Not yet implemented")
    }

    override suspend fun getSignOut() {
        TODO("Not yet implemented")
    }

    override fun saveTokens(authResource: AuthResource) {
        TODO("Not yet implemented")
    }

    override fun getTokens(): AuthResource {
        TODO("Not yet implemented")
    }
}
