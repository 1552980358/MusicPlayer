package projekt.cloud.piece.cloudy.util

import android.content.Context
import android.view.View
import com.google.android.material.elevation.SurfaceColors

object SurfaceColorUtil {

    val Context.surfaceColor1: Int
        get() = SurfaceColors.SURFACE_1.getColor(this)

    val Context.surfaceColor2: Int
        get() = SurfaceColors.SURFACE_2.getColor(this)

    val Context.surfaceColor3: Int
        get() = SurfaceColors.SURFACE_3.getColor(this)

    fun View.setSurface1BackgroundColor() {
        setBackgroundColor(context.surfaceColor1)
    }

    fun View.setSurface2BackgroundColor() {
        setBackgroundColor(context.surfaceColor2)
    }

    fun View.setSurface3BackgroundColor() {
        setBackgroundColor(context.surfaceColor3)
    }

}