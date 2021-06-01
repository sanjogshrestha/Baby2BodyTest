package com.sanjog.baby2bodytest.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sanjog.baby2bodytest.utils.TypeConverter

/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
@Entity
@TypeConverters(TypeConverter::class)
data class ComicEntity(@PrimaryKey
                       val id : Int,
                       val digitalId : Int,
                       val title : String,
                       val issueNumber : Int,
                       val variantDescription : String?,
                       val description : String?,
                       val modified : String?,
                       val isbn : String?,
                       val upc : String?,
                       val diamondCode : String,
                       val ean : String,
                       val issn : String,
                       val format : String,
                       val pageCount : Int,
                       val printPrice : Double,
                       val digitalPrintPrice : Double,
                       val onSaleDate : String?,
                       val focDate : String?,
                       val thumbnail : String?,
                       val isFavourite : Int = 0,
                       val yearOfRelease : String,
                       val images : List<String>?)