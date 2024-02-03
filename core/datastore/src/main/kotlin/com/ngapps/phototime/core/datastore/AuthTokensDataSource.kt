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

package com.ngapps.phototime.core.datastore

import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import com.ngapps.phototime.core.model.data.auth.AuthResource
import java.io.IOException
import javax.inject.Inject

class AuthTokensDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val encryptedSharedPreferences: EncryptedSharedPreferences,
) {
    fun saveTokens(token: String?, refresh: String?) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                encryptedSharedPreferences.edit()
                    .putString(TOKEN_KEY, token)
                    .putString(REFRESH_TOKEN_KEY, refresh)
                    .apply()
            } else {
                sharedPreferences.edit()
                    .putString(TOKEN_KEY, token)
                    .putString(REFRESH_TOKEN_KEY, refresh)
                    .apply()
            }
        } catch (ioException: IOException) {
            Log.e("AuthTokensDataSource", "Failed to update tokens", ioException)
        }
    }

    fun getTokens(): AuthResource {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            encryptedSharedPreferences.let { value ->
                AuthResource(
                    token = value.getString(TOKEN_KEY, null),
                    refresh = value.getString(REFRESH_TOKEN_KEY, null),
                )
            }
        } else {
            sharedPreferences.let { value ->
                AuthResource(
                    token = value.getString(TOKEN_KEY, null),
                    refresh = value.getString(REFRESH_TOKEN_KEY, null),
                )
            }
        }
    }

    fun deleteTokens() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            encryptedSharedPreferences.edit().clear().apply()
        } else {
            sharedPreferences.edit().clear().apply()
        }
    }

    companion object {
        private const val TOKEN_KEY = "token"
        private const val REFRESH_TOKEN_KEY = "refresh"
    }
}