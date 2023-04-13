package projekt.cloud.piece.music.player.base.interfaces

import android.content.Context
import androidx.annotation.ColorInt
import com.google.android.material.elevation.SurfaceColors

interface SurfaceColorsInterface {

    fun requireSurfaceColors(context: Context) {
        if (requireSurface0Color) {
            onSurface0ColorObtained(SurfaceColors.SURFACE_0.getColor(context))
        }
        if (requireSurface1Color) {
            onSurface1ColorObtained(SurfaceColors.SURFACE_1.getColor(context))
        }
        if (requireSurface2Color) {
            onSurface2ColorObtained(SurfaceColors.SURFACE_2.getColor(context))
        }
        if (requireSurface3Color) {
            onSurface3ColorObtained(SurfaceColors.SURFACE_3.getColor(context))
        }
        if (requireSurface4Color) {
            onSurface4ColorObtained(SurfaceColors.SURFACE_4.getColor(context))
        }
        if (requireSurface5Color) {
            onSurface5ColorObtained(SurfaceColors.SURFACE_5.getColor(context))
        }
    }

    val requireSurface0Color: Boolean
        get() = false

    val requireSurface1Color: Boolean
        get() = false

    val requireSurface2Color: Boolean
        get() = false

    val requireSurface3Color: Boolean
        get() = false

    val requireSurface4Color: Boolean
        get() = false

    val requireSurface5Color: Boolean
        get() = false

    fun onSurface0ColorObtained(@ColorInt color: Int) = Unit

    fun onSurface1ColorObtained(@ColorInt color: Int) = Unit

    fun onSurface2ColorObtained(@ColorInt color: Int) = Unit

    fun onSurface3ColorObtained(@ColorInt color: Int) = Unit

    fun onSurface4ColorObtained(@ColorInt color: Int) = Unit

    fun onSurface5ColorObtained(@ColorInt color: Int) = Unit

}