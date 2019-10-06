package app.fokkusu.music.base

import android.util.Log
import app.fokkusu.music.Application

/**
 * @File    : ApplicationUtil
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 6:49 PM
 **/
 
fun log(tag: String, msg: String) = Log.e(tag, msg)
fun log(tag: Int, msg: String) = Log.e(Application.getContext().getString(tag), msg)