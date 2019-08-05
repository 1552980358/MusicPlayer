package app.skynight.musicplayer.util

import android.widget.Toast
import app.skynight.musicplayer.MainApplication
import app.skynight.musicplayer.base.InitNotAllowedException

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

fun makeToast(content: Int): Unit = makeToast(MainApplication.getMainApplication().getString(content))
fun makeToast(content: CharSequence): Unit = Toast.makeText(MainApplication.getMainApplication(), content, Toast.LENGTH_SHORT).show()