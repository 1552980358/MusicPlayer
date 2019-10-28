package app.fokkusu.music.base

import android.util.Log
import android.widget.Toast
import app.fokkusu.music.Application
import app.fokkusu.music.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter

/**
 * @File    : ApplicationUtil
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 6:49 PM
 **/

fun log(tag: String, msg: String) = Log.e(tag, msg)

fun log(tag: Int, msg: String) = Log.e(Application.getContext().getString(tag), msg)

@Synchronized
fun Exception.getStack() {
    if (BuildConfig.DEBUG) { printStackTrace() }
    
    printStackTrace(PrintWriter(StringWriter().apply { Application.handler.post { makeToast(toString()) }; close() }).apply { close() })
}

fun makeToast(msg: String, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(Application.getContext(), msg, length).show()

@Suppress("IMPLICIT_CAST_TO_ANY")
fun getTime(time: Int) =
    "${(time / 60).run { if (this > 10) this else "0$this" }}:${(time % 60).run { if (this > 10) this else "0$this" }}"