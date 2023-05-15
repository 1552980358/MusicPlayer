package projekt.cloud.piece.cloudy.util

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import java.lang.IllegalArgumentException
import projekt.cloud.piece.cloudy.R

enum class PixelDensity {
    COMPAT,
    MEDIUM,
    EXPANDED;

    companion object PixelDensityUtil {

        val Context.pixelDensity: PixelDensity
            get() = resources.pixelDensity

        val Resources.pixelDensity
            get() = when {
                getBoolean(R.bool.pixel_density_medium) -> MEDIUM
                getBoolean(R.bool.pixel_density_expanded) -> EXPANDED
                else -> COMPAT
            }

        fun pixelDensity(): LifecycleOwnerProperty<PixelDensity> {
            return PixelDensityProperty()
        }

        private class PixelDensityProperty: LifecycleOwnerProperty<PixelDensity>() {

            override fun syncCreateValue(thisRef: LifecycleOwner): PixelDensity {
                return getContext(thisRef).pixelDensity
            }

            private fun getContext(thisRef: LifecycleOwner): Context {
                return when (thisRef) {
                    is Activity, is Service -> { thisRef as Context }
                    is Fragment -> { thisRef.requireContext() }
                    else -> throw IllegalArgumentException("Unknown $thisRef: Host class should be the subclass of android.content.Context")
                }
            }

        }

    }

}