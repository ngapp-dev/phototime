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

package com.ngapps.phototime.core.decoder

import android.net.Uri
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import javax.inject.Inject
import kotlin.math.pow
import kotlin.random.Random

class UriDecoder @Inject constructor() : StringDecoder {
    override fun decodeString(encodedString: String): String = Uri.decode(encodedString)
}

fun getFileTypeFromUrl(urlString: String): String? {
    try {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        connection.connect()

        val contentType = connection.getHeaderField("Content-Type")

        connection.disconnect()

        return contentType
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}


fun getFileNameFromUrl(urlString: String): String {
    val url = URL(urlString)
    val path = url.path
    val segments = path.split("/")
    if (segments.isNotEmpty()) {
        val lastSegment = segments.last()
        return URLDecoder.decode("Shoot-in-time: $lastSegment", "UTF-8")
    }
    return "Shoot-in-time: ${generateRandomNumber(10)}"
}

private fun generateRandomNumber(digits: Int): String {
    require(digits > 0) { "Number of digits must be greater than 0" }

    val min = 10.0.pow(digits - 1).toInt()
    val max = 10.0.pow(digits).toInt() - 1
    return Random.nextInt(min, max).toString()
}
