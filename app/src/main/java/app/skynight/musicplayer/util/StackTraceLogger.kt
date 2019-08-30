package app.skynight.musicplayer.util

import android.os.Environment
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @File    : StackTraceLogger
 * @Author  : 1552980358
 * @Date    : 17 Aug 2019
 * @TIME    : 4:32 PM
 **/

class StackTraceLogger private constructor() {
    companion object {
        private val getStackTraceLogger by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { StackTraceLogger() }

        @Suppress("ConstantLocale")
        val simpleDateFormat by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
            )
        }

        fun takeLog(title: String, content: CharSequence) =
            getStackTraceLogger.takeLog("${simpleDateFormat.format(Date(System.currentTimeMillis()))} $title: $content")

        fun takeLog(title: String, content: String) =
            getStackTraceLogger.takeLog("${simpleDateFormat.format(Date(System.currentTimeMillis()))} $title: $content")

        fun takeLog(title: String, e: Exception) {
            try {
                StringWriter().also {
                    e.printStackTrace(PrintWriter(it).apply {
                        Companion.takeLog(title, it.toString())
                        close()
                        it.close()
                    })
                }

                @Suppress("DEPRECATION") File(
                    Environment.getExternalStorageDirectory().absolutePath, SimpleDateFormat(
                        "yyyyMMdd-HHmmss", Locale.getDefault()
                    ).format(Date(System.currentTimeMillis())).plus(".txt")
                ).writeText(getStackTraceLogger.getLogString())
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }

        fun getLogList(): MutableList<String> {
            return getStackTraceLogger.getLogList()
        }

        fun getLogString(): String {
            return getStackTraceLogger.getLogString()
        }
    }

    private val logs = mutableListOf<String>()
    @Synchronized
    private fun takeLog(log: String) {
        logs.add(log)
    }

    private fun getLogString(): String {
        return StringBuilder().apply {
            logs.forEach { append(it.plus("\n")) }
        }.toString()
    }

    private fun getLogList(): MutableList<String> {
        return logs
    }
}