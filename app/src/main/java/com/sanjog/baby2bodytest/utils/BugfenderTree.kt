package com.sanjog.baby2bodytest.utils

import android.util.Log
import com.bugfender.sdk.Bugfender
import com.bugfender.sdk.LogLevel
import timber.log.Timber


/**
 * Created by Sanjog Shrestha on 2021/05/30.
 * Copyright (c) Sanjog Shrestha
 */
class BugfenderTree : Timber.Tree() {
    companion object {
        private fun findCaller(t: Throwable): StackTraceElement? {
            val stack = t.stackTrace
            for (i in 2 until stack.size) {
                // 0th will be the caller of this method, inside this class,
                // 1st will be Timber
                // 2nd might be Timber or the real caller
                val stackTraceElement = stack[i]
                if (!stackTraceElement.className.startsWith("timber.log.")) {
                    return stackTraceElement
                }
            }
            return null
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // determine log level
        var logLevel = LogLevel.Debug
        when (priority) {
            Log.VERBOSE -> logLevel = LogLevel.Trace
            Log.DEBUG -> logLevel = LogLevel.Debug
            Log.INFO -> logLevel = LogLevel.Info
            Log.WARN -> logLevel = LogLevel.Warning
            Log.ERROR -> logLevel = LogLevel.Error
            Log.ASSERT -> logLevel = LogLevel.Fatal
        }
        // fill in caller info, skipping Timber's calls
        var lineNumber = -1
        var method = ""
        var fileName = ""
        val callerInfo = findCaller(Exception())
        if (callerInfo != null) {
            lineNumber = callerInfo.lineNumber
            method = callerInfo.className + "." + callerInfo.methodName
            fileName = callerInfo.fileName
        }
        Bugfender.log(lineNumber, method, fileName, logLevel, tag, message)
    }
}