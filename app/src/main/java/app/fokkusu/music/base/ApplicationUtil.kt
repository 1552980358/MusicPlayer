package app.fokkusu.music.base

import android.util.Log
import android.widget.Toast
import app.fokkusu.music.Application
import app.fokkusu.music.BuildConfig
import java.io.PrintStream
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

@Suppress("SpellCheckingInspection")
@Synchronized
fun Exception.getStack(showLog: Boolean = true, showToast: Boolean = true) {
    try {
        also { exception ->
            StringWriter().also { stringWriter ->
                PrintWriter(stringWriter).apply {
                    exception.printStackTrace(this)
            
                    stringWriter.toString().apply {
                        if (showLog && BuildConfig.DEBUG) {
                            log("FokkusuException", this)
                        }
                
                        if (showToast) {
                            makeToast(this)
                        }
                    }
                    close()
                }
                stringWriter.close()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    System.gc()
}

fun makeToast(msg: String, length: Int = Toast.LENGTH_SHORT) =
    Application.handler.post { Toast.makeText(Application.getContext(), msg, length).show() }

fun makeToast(msg: Int, length: Int = Toast.LENGTH_SHORT) = makeToast(Application.getContext().getString(msg), length)

@Suppress("IMPLICIT_CAST_TO_ANY")
fun getTime(time: Int) =
    "${(time / 60).run { if (this > 9) this else "0$this" }}:${(time % 60).run { if (this > 9) this else "0$this" }}"