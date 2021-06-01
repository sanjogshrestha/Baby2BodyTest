package com.sanjog.baby2bodytest.data.response

import com.google.gson.annotations.SerializedName


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
class ComicListResponse(@SerializedName("code") val code : Int,
                        @SerializedName("status") val status : String,
                        @SerializedName("attributionHTML") val attributionHTML : String,
                        @SerializedName("data") val data : Data){
    var nextPage: Int? = null
    data class Data (
        @SerializedName("offset") val offset : Int,
        @SerializedName("limit") val limit : Int,
        @SerializedName("total") val total : Int,
        @SerializedName("count") val count : Int,
        @SerializedName("results") val results : List<Results>
    ){
        data class Results (
            @SerializedName("id") val id : Int,
            @SerializedName("digitalId") val digitalId : Int,
            @SerializedName("title") val title : String,
            @SerializedName("issueNumber") val issueNumber : Int,
            @SerializedName("variantDescription") val variantDescription : String,
            @SerializedName("description") val description : String,
            @SerializedName("modified") val modified : String,
            @SerializedName("isbn") val isbn : String?,
            @SerializedName("upc") val upc : String?,
            @SerializedName("diamondCode") val diamondCode : String,
            @SerializedName("ean") val ean : String,
            @SerializedName("issn") val issn : String,
            @SerializedName("format") val format : String,
            @SerializedName("pageCount") val pageCount : Int,
            @SerializedName("resourceURI") val resourceURI : String,
            @SerializedName("creators") val creators : Creators,
            @SerializedName("prices") val prices : List<Prices>,
            @SerializedName("thumbnail") val thumbnail : Thumbnail,
            @SerializedName("dates") val dates : List<Dates>,
            @SerializedName("images") val images : List<Images>
        ){
            data class Prices (
                @SerializedName("type") val type : String,
                @SerializedName("price") val price : Double
            )
            data class Thumbnail (
                @SerializedName("path") val path : String,
                @SerializedName("extension") val extension : String
            )
            data class Images (
                @SerializedName("path") val path : String,
                @SerializedName("extension") val extension : String
            )
            data class Dates (
                    @SerializedName("type") val type : String,
                    @SerializedName("date") val date : String
            )
            data class Creators (
                    @SerializedName("items") val items : List<Items>?){
                data class Items (
                        @SerializedName("resourceURI") val resourceURI : String,
                        @SerializedName("name") val name : String,
                        @SerializedName("role") val role : String
                )
            }
        }
    }
}