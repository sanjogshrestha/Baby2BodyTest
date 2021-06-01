package com.sanjog.baby2bodytest.utils

import androidx.room.TypeConverter
import timber.log.Timber


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
object TypeConverter {
    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map {number->
                try {
                    number.toInt()
                } catch (ex: NumberFormatException) {
                    Timber.e(ex, "Cannot convert $number to number")
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }

    @TypeConverter
    @JvmStatic
    fun stringToStringList(data: String?): List<String>? {
        return data?.let {
            it.split(",").map { value -> value}
        }
    }

    @TypeConverter
    @JvmStatic
    fun stringListToString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}