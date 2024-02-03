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

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * An [androidx.datastore.core.Serializer] for the [UserPreferences] proto.
 */
class UserPreferencesSerializer @Inject constructor() : Serializer<com.ngapps.phototime.core.datastore.UserPreferences> {
    override val defaultValue: com.ngapps.phototime.core.datastore.UserPreferences = com.ngapps.phototime.core.datastore.UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): com.ngapps.phototime.core.datastore.UserPreferences =
        try {
            // NOTE: readFrom is already called on the data store background thread
            com.ngapps.phototime.core.datastore.UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: com.ngapps.phototime.core.datastore.UserPreferences, output: OutputStream) {
        // NOTE: writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}
