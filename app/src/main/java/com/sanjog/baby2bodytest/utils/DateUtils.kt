package com.sanjog.baby2bodytest.utils

import okhttp3.internal.UTC
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Sanjog Shrestha on 2021/05/31.
 * Copyright (c) Sanjog Shrestha
 */
object DateUtils {
    private const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss"
    private const val YEAR_FORMAT = "YYYY"
    private const val YMD_FORMAT = "MMM dd, yyyy"

    fun getYear(inputDate: String?): String {
        if(inputDate.isNullOrEmpty()) return "N/A"
        val formatter = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.ENGLISH)
        formatter.timeZone = UTC

        val targetFormat = SimpleDateFormat(YEAR_FORMAT, Locale.ENGLISH)
        val date: Date = formatter.parse(inputDate) ?: return "N/A"
        return targetFormat.format(date)
    }

    fun getYMD(inputDate: String?): String {
        if(inputDate.isNullOrEmpty()) return "N/A"
        val formatter = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.ENGLISH)
        formatter.timeZone = UTC

        val targetFormat = SimpleDateFormat(YMD_FORMAT, Locale.ENGLISH)
        val date: Date = formatter.parse(inputDate) ?: return "N/A"
        return targetFormat.format(date)
    }
}