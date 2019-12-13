package app.fokkusu.music.view.pulse

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import app.fokkusu.music.R
import kotlin.math.sqrt

/**
 * @File    : CylinderPulseView
 * @Author  : 1552980358
 * @Date    : 2019/11/6
 * @TIME    : 19:20
 **/

class CylinderPulseView : BasePulseView {
    
    private val cylinderHeight by lazy { widgetHeight / sqrt(128 * 128 * 2F) }
    private val diff by lazy { resources.getDimensionPixelOffset(R.dimen.pulse_diff) }
    private val cylinderWidth by lazy { (widgetWidth - diff) / (floatArray.size / 2) }
    
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    
    @Synchronized
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        
        super.onDraw(canvas)
        
        //if (dataByte.isEmpty()) {
        //    drawing = false
        //    return
        //}
        
        //for (i in dataByte.indices) {
        //    canvas.drawRect(
        //         cylinderWidth * i + diff * i,
        //        0F,
        //       cylinderWidth + cylinderWidth * i + diff * i,
        //         dataByte[i].run { if (this == 128.toByte()) this.toInt() else this.toInt().plus(1) } * cylinderHeight,
        //       paint
        //    )
        //}
        
        if (floatArray.isEmpty()) {
            drawing = false
            return
        }
        
        for ((i, j) in floatArray.withIndex()) {
            canvas.drawRect(
                cylinderWidth * i + diff * i,
               0f,
                cylinderWidth + cylinderWidth * i + diff * i,
               j.run { if (this != 0.0F) this else 1F } * cylinderHeight,
                paint
            )
        }
        
        drawing = false
    }
}


