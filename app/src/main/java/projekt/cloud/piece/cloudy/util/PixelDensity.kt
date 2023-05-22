package projekt.cloud.piece.cloudy.util

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.requireContext

enum class PixelDensity {
    COMPAT,
    MEDIUM,
    EXPANDED;

    companion object PixelDensityUtil {

        /**
         * [android.content.Context.pixelDensity]
         * @type [PixelDensity]
         **/
        val Context.pixelDensity: PixelDensity
            get() = resources.pixelDensity

        /**
         * [android.content.res.Resources.pixelDensity]
         * @type [PixelDensity]
         **/
        val Resources.pixelDensity
            get() = when {
                getBoolean(R.bool.pixel_density_medium) -> MEDIUM
                getBoolean(R.bool.pixel_density_expanded) -> EXPANDED
                else -> COMPAT
            }

        /**
         * [PixelDensity.pixelDensity]
         * @return [LifecycleOwnerProperty]<[PixelDensity]>
         **/
        fun pixelDensity(): LifecycleOwnerProperty<PixelDensity> {
            return PixelDensityProperty()
        }

        /**
         * [PixelDensity.PixelDensityProperty]
         * @parent [LifecycleOwnerProperty]
         **/
        private class PixelDensityProperty: LifecycleOwnerProperty<PixelDensity>() {

            /**
             * [LifecycleOwnerProperty.syncCreateValue]
             * @param thisRef [androidx.lifecycle.LifecycleOwner]
             * @return [PixelDensity]
             **/
            override fun syncCreateValue(thisRef: LifecycleOwner): PixelDensity {
                return thisRef.requireContext()
                    .pixelDensity
            }

        }

    }

}