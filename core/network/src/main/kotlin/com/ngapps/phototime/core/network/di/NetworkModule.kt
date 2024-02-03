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

package com.ngapps.phototime.core.network.di

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.ngapps.phototime.core.datastore.AuthTokensDataSource
import com.ngapps.phototime.core.network.BuildConfig
import com.ngapps.phototime.core.network.base.createHttpLoggingInterceptor
import com.ngapps.phototime.core.network.base.createHttpRequestInterceptor
import com.ngapps.phototime.core.network.fake.FakeAssetManager
import com.ngapps.phototime.core.network.interceptor.AuthFailedAuthenticator
import com.ngapps.phototime.core.network.interceptor.AuthInterceptor
import com.ngapps.phototime.core.network.interceptor.HttpRequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = createHttpLoggingInterceptor(isDev = true)

    @Provides
    @Singleton
    fun provideHttpRequestInterceptor(): HttpRequestInterceptor = createHttpRequestInterceptor()

    @Provides
    @Singleton
    fun provideAuthFailedAuthenticator(
        authTokenDataSource: AuthTokensDataSource,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        networkJson: Json,
        googleSignInClient: GoogleSignInClient
    ): Authenticator {
        return AuthFailedAuthenticator(
            authTokenDataSource,
            httpLoggingInterceptor,
            networkJson,
            googleSignInClient
        )
    }

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun providesFakeAssetManager(
        @ApplicationContext context: Context,
    ): FakeAssetManager = FakeAssetManager(context.assets::open)

    @Provides
    @Singleton
    fun okHttpCallFactory(
        authInterceptor: AuthInterceptor,
        authenticator: AuthFailedAuthenticator
    ): Call.Factory = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
                },
        )
        .authenticator(authenticator)
        .build()

    /**
     * Since we're displaying SVGs in the app, Coil needs an ImageLoader which supports this
     * format. During Coil's initialization it will call `applicationContext.newImageLoader()` to
     * obtain an ImageLoader.
     *
     * @see <a href="https://github.com/coil-kt/coil/blob/main/coil-singleton/src/main/java/coil/Coil.kt">Coil</a>
     */
    @Provides
    @Singleton
    fun imageLoader(
        okHttpCallFactory: Call.Factory,
        @ApplicationContext application: Context,
    ): ImageLoader = ImageLoader.Builder(application)
        .callFactory(okHttpCallFactory)
        .components {
            add(SvgDecoder.Factory())
        }
        // NOTE: Assume most content images are versioned urls
        //  but some problematic images are fetching each time
        .respectCacheHeaders(false)
        .apply {
            if (BuildConfig.DEBUG) {
                logger(DebugLogger())
            }
        }
        .build()
}

