package app.skynight.musicplayer.util

import android.content.Context
import android.widget.Toast
import app.skynight.musicplayer.base.InitNotAllowedException
import java.io.BufferedReader
import java.io.InputStream
import java.nio.charset.Charset

/**
 * @File    : BaseUtil
 * @Author  : 1552980358
 * @Date    : 5 Aug 2019
 * @TIME    : 1:19 AM
 **/
class BaseUtil private constructor() {
    companion object {
        const val TAG = "BaseUtil"
    }
    init {
        throw InitNotAllowedException(TAG)
    }
}

fun makeToast(context: Context, content: CharSequence): Unit = Toast.makeText(context, content, Toast.LENGTH_SHORT).show()