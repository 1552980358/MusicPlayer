package app.skynight.musicplayer.util

import android.annotation.SuppressLint
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @File    : StackTraceLogger
 * @Author  : 1552980358
 * @Date    : 17 Aug 2019
 * @TIME    : 4:32 PM
 **/

class StackTraceLogger private constructor(){
    companion object {
        private val getStackTraceLogger by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { StackTraceLogger() }

        @Suppress("ConstantLocale")
        val simpleDateFormat by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
        fun takeLog(title: String, content: String) {
            getStackTraceLogger.takeLog("${simpleDateFormat.format(Date(System.currentTimeMillis()))} $title: $content")
        }
        fun logToFile() {
            getStackTraceLogger
        }
        fun getLogs(): MutableList<String> {
            return getStackTraceLogger.logs
        }
    }

    private val logs = mutableListOf<String>()

    @Synchronized
    fun takeLog(log: String) {
        logs.add(log)
    }

    @Synchronized
    fun logToFile() {
        Thread {
            //File(Environment.getExternalStorageDirectory().absolutePath)
        }.start()
    }
}