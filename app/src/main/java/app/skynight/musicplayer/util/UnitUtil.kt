package app.skynight.musicplayer.util

import android.content.res.Resources
import android.util.TypedValue
import app.skynight.musicplayer.base.InitNotAllowedException

/**
 * @FILE:   UnitUtil
 * @AUTHOR: 1552980358
 * @DATE:   19 Jul 2019
 * @TIME:   8:06 PM
 **/
 
class UnitUtil private constructor(){
    init {
        throw InitNotAllowedException(TAG)
    }
    companion object {
        const val TAG = "UnitUtil"
        fun getPx(dp: Int): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dp.toFloat(), Resources.getSystem().displayMetrics).toInt()
        }
        fun getPx(sp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    sp, Resources.getSystem().displayMetrics)
        }

        fun getTime(duration: Int): String {
            return "${duration / 60}:${duration % 60}"
        }
    }
}