package app.skynight.musicplayer.util

import android.content.Context
import android.graphics.Bitmap
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