package app.skynight.musicplayer.util

import android.util.Log
import android.widget.Toast
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.base.InitNotAllowedException
import java.lang.Exception

/**
 * @File    : BaseUtil
 * @Author  : 1552980358
 * @Date    : 5 Aug 2019
 * @TIME    : 1:19 AM
 **/

fun makeToast(content: Int): Unit = makeToast(MainApplication.getMainApplication().getString(content))
fun makeToast(content: CharSequence): Unit = Toast.makeText(MainApplication.getMainApplication(), content, Toast.LENGTH_SHORT).show()

fun log(tag: String, msg: Any?): Any = try { Log.e(tag, msg.toString()) } catch (e: Exception) { e.printStackTrace() }