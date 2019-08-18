package app.skynight.musicplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

/**
 * @File    : ImgUtil
 * @Author  : 1552980358
 * @Date    : 13 Aug 2019
 * @TIME    : 9:36 PM
 **/

fun Drawable.setColorFilter(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        @Suppress("DEPRECATION")
        this.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}

fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
    val output = Bitmap.createBitmap(bitmap)
    try {
        RenderScript.create(context).apply {
            val allocationIn =Allocation.createFromBitmap(this, bitmap)
            val allocationOut = Allocation.createFromBitmap(this, output)
            ScriptIntrinsicBlur.create(this, Element.U8_4(this)).apply {
                setRadius(radius)
                setInput(allocationIn)
                forEach(allocationOut)
            }
            allocationIn.destroy()
            allocationOut.apply {
                copyTo(output)
                destroy()
            }
            destroy()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return output
}

fun makeScaledBitmap(bitmap: Bitmap, scale: Int): Bitmap {
    return Bitmap.createScaledBitmap(bitmap, scale, scale, true)
}