package app.fokkusu.music.view.pulse

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import app.fokkusu.music.R
import kotlin.math.abs
import kotlin.math.hypot

/**
 * @File    : BasePulseView
 * @Author  : 1552980358
 * @Date    : 2019/11/5
 * @TIME    : 19:26
 **/

open class BasePulseView: View {
    protected var dataByte = byteArrayOf()
    protected var floatArray = floatArrayOf()
    protected val paint by lazy { Paint() }
    
    protected var drawing = false
    
    protected val widgetHeight by lazy { resources.getDimensionPixelOffset(R.dimen.pulse_height) }
    protected val widgetWidth by lazy { resources.displayMetrics.widthPixels.toFloat() }
    
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet) {
        paint.color = Color.WHITE
        paint.strokeWidth = resources.getDimension(R.dimen.pulse_thick)
        paint.isAntiAlias = true
    }
    
    /* updateByteArray for fft form */
    @Synchronized
    fun updateFFTArray(byteArray: ByteArray) {
        if (drawing) return
        
        //if (dataByte.isEmpty()) {
        //    dataByte = ByteArray(byteArray.size / 2 + 1)
        //}
    
        //dataByte[0] = abs(byteArray[1].toInt()).toByte()
        //for (i in 2 until byteArray.size step 2) {
        //    dataByte[i / 2] = abs(hypot(byteArray[i].toDouble(), byteArray[i + 1].toDouble()).toByte().toInt()).toByte()
        //}
        
        if (floatArray.isEmpty()) {
            floatArray = FloatArray(byteArray.size / 2 + 1)
        }
        
        floatArray[0] = abs(byteArray[0].toFloat())
        floatArray[byteArray.size / 2] = abs(byteArray[1].toFloat())
        for (i in 1 until byteArray.size / 2) {
            floatArray[i] = hypot(byteArray[2 * i].toDouble(), byteArray[2 * i + 1].toDouble()).toFloat()
        }
        
        postInvalidate()
        drawing = true
    }
    
    /* updateByteArray for wave form */
    @Synchronized
    open fun updateWaveArray(byteArray: ByteArray) {
        if (drawing) return
        
        dataByte = byteArray
        postInvalidate()
    }
    
    @Synchronized
    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawColor(Color.TRANSPARENT)
    }
    
}