package app.fokkusu.music.view.pulse

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

/**
 * @File    : CylinderPulseView
 * @Author  : 1552980358
 * @Date    : 2019/11/5
 * @TIME    : 19:49
 **/

class WavePulseView : BasePulseView {
    
    private lateinit var points: FloatArray
    
    private val waveHeight by lazy { widgetHeight / 2F }
    private val waveWidth by lazy { widgetWidth / (dataByte.size - 1) }
    
    constructor(context: Context): this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet)
    
    /* onDraw */
    @SuppressLint("DrawAllocation")
    @Synchronized
    override fun onDraw(canvas: Canvas?) {
        canvas?:return
    
        super.onDraw(canvas)
        
        if (dataByte.isEmpty()) {
            drawing = false
            return
        }
        
        if (!::points.isInitialized) {
            points = FloatArray(dataByte.size * 4)
        }
        
        /* Draw wave */
        for (i in 0 until dataByte.lastIndex) {
            points[i * 4] = waveWidth * i
            points[i * 4 + 1] = waveHeight / 2 + (dataByte[i] + 128).toByte() * waveHeight / 128
            points[i * 4 + 2] = waveWidth * (i + 1)
            points[i * 4 + 3] = waveHeight / 2 + (dataByte[i + 1] + 128).toByte() * waveHeight / 128
        }
        
        canvas.drawLines(points, paint)
        
        drawing = false
    }
}