package projekt.cloud.piece.cloudy.util

import android.view.View
import com.google.android.material.elevation.SurfaceColors

object SurfaceColorUtil {

    fun View.setSurface2BackgroundColor() {
        setBackgroundColor(SurfaceColors.SURFACE_2.getColor(context))
    }

}