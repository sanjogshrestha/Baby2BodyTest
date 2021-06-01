/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sanjog.baby2bodytest.data.response

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.regex.Pattern

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        private const val generic_error_message = "Oops! Something went wrong.\nPlease try again."
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            Timber.e(error)
            error.stackTrace
            val errorMessage = when (error) {
                is HttpException -> "An error has occurred: ${error.code()}\nPlease try again."
                is SocketTimeoutException -> "A timeout error has occurred,\nplease check your internet connection and try again"
                is ConnectException , is IOException -> "An error has occurred, most likely a network issue.\nPlease check your internet connection and try again"
                is JsonSyntaxException -> generic_error_message
                else -> error.message
            }
            return ApiErrorResponse(errorMessage ?: generic_error_message)
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            val body = response.body()
            return if (response.isSuccessful) {
                if (body == null || response.code() == 204) {
                     ApiEmptyResponse()
                } else {
                    ApiSuccessResponse(
                        body = body,
                        linkHeader = response.headers()["link"]
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if(msg.isNullOrEmpty()){
                    val message = response.message()
                    if(response.code() == 404){
                        message.replace(" ", "\n")
                    }else {
                        message
                    }
                }else{
                    try {
                        val errorResponse = Gson().fromJson(msg, ErrorResponse::class.java)
                        val data = errorResponse.data
                        if(data == null){
                            errorResponse.message
                        }else{
                            data[0].errorMessage
                        }
                    }catch (e: Throwable){
                        msg
                    }
                }

                if(response.code() == 401){
                    return ApiNotFoundResponse(errorMsg ?: generic_error_message)
                }
                return ApiErrorResponse(errorMsg ?: generic_error_message)
            }
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

class ApiNotFoundResponse<T>(val errorMessage: String) : ApiResponse<T>()

data class ApiSuccessResponse<T>(
    val body: T,
    val links: Map<String, String>
) : ApiResponse<T>() {
    constructor(body: T, linkHeader: String?) : this(
        body = body,
        links = linkHeader?.extractLinks() ?: emptyMap()
    )

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1)!!)
                } catch (ex: NumberFormatException) {
                    Timber.w("cannot parse next page from %s", next)
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)!!] = matcher.group(1)!!
                }
            }
            return links
        }
    }
}

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()
