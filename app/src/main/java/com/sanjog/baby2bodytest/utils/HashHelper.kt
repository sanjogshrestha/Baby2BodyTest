package com.sanjog.baby2bodytest.utils

import okhttp3.internal.and
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by Sanjog Shrestha on 2021/05/29.
 * Copyright (c) Sanjog Shrestha
 */
internal object HashHelper {
    /**
     * generate a cryptographic hash function
     */
    fun generate(s: String): String? {
        return try {
            val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")
            val digest: ByteArray = messageDigest.digest(s.toByteArray())
            val md5 = StringBuilder()
            for (value in digest) {
                md5.append(Integer.toHexString(value and 0xFF or 0x100).substring(1, 3))
            }
            md5.toString()
        } catch (e: NoSuchAlgorithmException) {
            null
        }
    }
}