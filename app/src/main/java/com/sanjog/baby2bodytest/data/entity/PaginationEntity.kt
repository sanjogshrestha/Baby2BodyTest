package com.sanjog.baby2bodytest.data.entity

import androidx.room.Entity
import androidx.room.TypeConverters
import com.sanjog.baby2bodytest.utils.TypeConverter


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
@Entity(primaryKeys = ["type"])
@TypeConverters(TypeConverter::class)
data class PaginationEntity (val type: String,
                             val ids: List<Int>,
                             val totalCount: Int,
                             val next: Int?)