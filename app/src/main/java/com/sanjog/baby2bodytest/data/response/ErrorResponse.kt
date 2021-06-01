package com.sanjog.baby2bodytest.data.response

import com.google.gson.annotations.SerializedName

/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
class ErrorResponse (val status : String,
                     val message: String,
                     val validationCode : String?,
                     val data : List<Data>?,
                     val statusCode : Int){
    data class Data (
        @SerializedName("target_element") val targetElement : String,
        @SerializedName("error_message") val errorMessage : String
    )
}