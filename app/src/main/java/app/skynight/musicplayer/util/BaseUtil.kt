package app.skynight.musicplayer.util

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.skynight.musicplayer.BuildConfig
import app.skynight.musicplayer.MainApplication
import java.io.File
import java.lang.reflect.Array
import kotlin.Exception

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

fun Drawable.setColorFilter(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        @Suppress("DEPRECATION")
        this.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}

fun getPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(), Resources.getSystem().displayMetrics).toInt()
}
fun getPx(sp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp, Resources.getSystem().displayMetrics)
}

fun getTime(duration: Int): String {
    val s = (duration % 60).toString()
    return "${duration / 60}:${if (s.length == 1) "0$s" else s}"
}

fun checkAndCreateExternalStorageFile(file: String): Boolean {
    return checkAndCreateExternalStorageFile(File(file))
}

fun checkAndCreateExternalStorageFile(file: File): Boolean {
    try {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            return false
        }
        if (file.parentFile!!.exists()) {
            return try {
                file.parentFile!!.mkdir()
                file.mkdir()
                true
            } catch (e: Exception) {
                false
            }
        }
        if (!file.exists()) {
            return try {
                file.createNewFile()
                true
            } catch (e: Exception) {
                false
            }
        }
        return true
    } catch (e: Exception) {
        log("checkAndCreateExternalStorageFile", e)
        return false
    }
}

