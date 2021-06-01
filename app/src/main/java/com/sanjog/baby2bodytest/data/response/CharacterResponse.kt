package com.sanjog.baby2bodytest.data.response

import com.google.gson.annotations.SerializedName


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
class CharacterResponse(@SerializedName("code") val code : Int,
                        @SerializedName("status") val status : String,
                        @SerializedName("attributionHTML") val attributionHTML : String,
                        @SerializedName("data") val data : Data){
    data class Data (
        @SerializedName("offset") val offset : Int,
        @SerializedName("limit") val limit : Int,
        @SerializedName("total") val total : Int,
        @SerializedName("count") val count : Int,
        @SerializedName("results") val results : List<Results>
    ){
        data class Results (
            @SerializedName("id") val id : Int,
            @SerializedName("name") val name : String,
            @SerializedName("thumbnail") val thumbnail : Thumbnail
        ){
            data class Thumbnail (
                @SerializedName("path") val path : String,
                @SerializedName("extension") val extension : String
            )
        }
    }
}