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

package com.ngapps.phototime.core.network.base

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

sealed class Failure : IOException() {
    data object JsonError : Failure()
    data object UnknownError : Failure()
    data object UnknownHostError : Failure()
    data object EmptyResponse : Failure()
    data class ConnectivityError(override var message: String) : Failure()
    data object InternetError : Failure()
    data class UnAuthorizedException(override var message: String) : Failure()
    data object ParsingDataError : Failure()
    data object IgnorableError : Failure()
    data object UnProcessableEntity : Failure()
    data class TimeOutError(override var message: String) : Failure()
    data class ApiError(var code: Int = 0, override var message: String) : Failure()
    data class ServerError(var code: Int = 0, override var message: String) : Failure()
    data class NotFoundException(override var message: String) : Failure()
    data class SocketTimeoutError(override var message: String) : Failure()
    data class BusinessError(override var message: String, val stackTrace: String) : Failure()
    data class HttpError(var code: Int, override var message: String) : Failure()
    data class BadRequestError(var code: Int, override var message: String) : Failure()
}

fun Throwable.handleThrowable(): Failure {
    Log.e("HandleError", this.message.orEmpty())
    return if (this is ConnectException) {
        Failure.ConnectivityError(message = "Unable to connect to the server")
    } else if (this is HttpException && this.code() == HttpStatusCode.BadRequest.code) {
        Failure.BadRequestError(code = 400, message = "Bad request")
    } else if (this is HttpException && this.code() == HttpStatusCode.UnProcessableEntity.code) {
        Failure.UnProcessableEntity
    } else if (this is HttpException && this.code() == HttpStatusCode.NotFound.code) {
        Failure.NotFoundException(message = "User not found")
    } else if (this is SocketTimeoutException) {
        Failure.SocketTimeoutError(message = "Unable to connect to the server")
    } else if (this.message != null) {
        Failure.NotFoundException(this.message!!)
    } else {
        Failure.UnknownError
    }
}

fun Exception.handleException() = when (this) {
    is Failure.BadRequestError -> Failure.BadRequestError(code = 400, message = "Bad request")
    else -> Failure.UnknownError
}
