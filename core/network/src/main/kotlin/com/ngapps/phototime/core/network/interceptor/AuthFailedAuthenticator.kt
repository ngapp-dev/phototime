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

package com.ngapps.phototime.core.network.interceptor

import android.os.Build
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ngapps.phototime.core.datastore.AuthTokensDataSource
import com.ngapps.phototime.core.network.model.auth.NetworkAuthResource
import com.ngapps.phototime.core.network.model.auth.NetworkTokenResourceQuery
import com.ngapps.phototime.core.network.retrofit.UploadPtNetworkApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject

class AuthFailedAuthenticator @Inject constructor(
    private val authTokenDataSource: AuthTokensDataSource,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val networkJson: Json,
    private val googleSignIn: GoogleSignInClient,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val tokens = NetworkTokenResourceQuery(
                authTokenDataSource.getTokens().token.orEmpty(),
                authTokenDataSource.getTokens().refresh.orEmpty(),
                Build.FINGERPRINT,
            )
            val newToken = getNewToken(tokens)
            if (!newToken.isSuccessful || newToken.body() == null) {
                authTokenDataSource.deleteTokens()
                googleSignIn.signOut()
            }
            newToken.body()?.let {
                authTokenDataSource.saveTokens(it.token, it.refresh)
                response.request.newBuilder()
                    .header(authHeaderName, it.refresh.withBearer())
                    .build()
            }
        }
    }

    private suspend fun getNewToken(tokens: NetworkTokenResourceQuery): retrofit2.Response<NetworkAuthResource> {
        val okHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("${SitBaseUrl}/api/")
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
        val service = retrofit.create(UploadPtNetworkApi::class.java)
        return service.refreshToken(tokens)
    }

    private fun String.withBearer() = "Bearer $this"

    companion object {
        private const val SitBaseUrl = com.ngapps.phototime.core.network.BuildConfig.BACKEND_URL
        private const val authHeaderName = "Authorization"
    }
}