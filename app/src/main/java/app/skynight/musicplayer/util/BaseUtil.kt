package app.skynight.musicplayer.util

import android.util.Log
import android.widget.Toast
import app.skynight.musicplayer.BuildConfig
import app.skynight.musicplayer.MainApplication
import java.lang.Exception

/**
 * @File    : BaseUtil
 * @Author  : 1552980358
 * @Date    : 5 Aug 2019
 * @TIME    : 1:19 AM
 **/

fun makeToast(content: Int): Unit =
    makeToast(MainApplication.getMainApplication().getString(content))

fun makeToast(content: CharSequence): Unit =
    Toast.makeText(MainApplication.getMainApplication(), content, Toast.LENGTH_SHORT).show()

fun log(tag: String, list: List<Any>) = try {
    if (BuildConfig.DEBUG) Log.e(tag, list.toString())
    StackTraceLogger.takeLog(tag, list.toString())
} catch (e: Exception) {
    e.printStackTrace()
}

fun log(tag: String, msg: CharSequence) = try {
    if (BuildConfig.DEBUG) Log.e(tag, msg.toString())
    StackTraceLogger.takeLog(tag, msg)
} catch (e: Exception) {
    e.printStackTrace()
}

fun log(tag: String, msg: String) = try {
    if (BuildConfig.DEBUG) Log.e(tag, msg)
    StackTraceLogger.takeLog(tag, msg)
} catch (e: Exception) {
    e.printStackTrace()
}

fun log(tag: String, e: Exception) = try {
    if (BuildConfig.DEBUG)  e.printStackTrace()
    StackTraceLogger.takeLog(tag, e)
} catch (e: Exception) {
    e.printStackTrace()
}

fun log(tag: String, msg: Any?) = try {
    if (BuildConfig.DEBUG) Log.e(tag, msg.toString())
    StackTraceLogger.takeLog(tag, msg.toString())
} catch (e: Exception) {
    e.printStackTrace()
}
