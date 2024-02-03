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

package com.ngapps.phototime.core.data.repository.auth

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.ngapps.phototime.core.data.model.auth.asExternalModel
import com.ngapps.phototime.core.data.model.response.asExternalModel
import com.ngapps.phototime.core.data.model.signin.asNetworkModel
import com.ngapps.phototime.core.data.model.signup.asNetworkModel
import com.ngapps.phototime.core.datastore.AuthTokensDataSource
import com.ngapps.phototime.core.model.data.auth.AuthResource
import com.ngapps.phototime.core.model.data.response.ResponseResource
import com.ngapps.phototime.core.model.data.auth.signin.GoogleSignInResourceQuery
import com.ngapps.phototime.core.model.data.auth.signin.SignInResourceQuery
import com.ngapps.phototime.core.model.data.auth.signup.SignUpResourceQuery
import com.ngapps.phototime.core.network.UploadPtNetworkDataSource
import com.ngapps.phototime.core.network.base.handleThrowable
import com.ngapps.phototime.core.result.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val network: UploadPtNetworkDataSource,
    private val authTokensDataSource: AuthTokensDataSource,
    private val googleSignIn: GoogleSignInClient,
) : AuthRepository {

    override suspend fun getSignIn(
        query: SignInResourceQuery
    ): DataResult<AuthResource> =
        withContext(Dispatchers.IO) {
            try {
                val authResource = network.getSignIn(query.asNetworkModel()).asExternalModel()
                DataResult.Success(authResource)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getGoogleSignIn(
        query: GoogleSignInResourceQuery
    ): DataResult<AuthResource> =
        withContext(Dispatchers.IO) {
            try {
                val authResource = network.getGoogleSignIn(query.asNetworkModel()).asExternalModel()
                DataResult.Success(authResource)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }

    override suspend fun getSignUp(
        query: SignUpResourceQuery
    ): DataResult<ResponseResource> =
        withContext(Dispatchers.IO) {
            try {
                val responseResource = network.getSignUp(query.asNetworkModel()).asExternalModel()
                DataResult.Success(responseResource)
            } catch (e: Exception) {
                DataResult.Error(e.handleThrowable().message ?: "Unknown")
            }
        }


    override suspend fun getSignOut() {
        authTokensDataSource.deleteTokens()
        googleSignIn.signOut()
    }

    override fun saveTokens(authResource: AuthResource) =
        authTokensDataSource.saveTokens(authResource.token, authResource.refresh)

    override fun getTokens(): AuthResource = authTokensDataSource.getTokens()
}

