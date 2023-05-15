package projekt.cloud.piece.cloudy.base

import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.cloudy.util.PixelDensity

typealias LayoutAdapterInflater<B, A> = (PixelDensity, B) -> A

abstract class BaseLayoutAdapter<B: ViewBinding>(protected val binding: B) {

    companion object LayoutAdapterUtil {

        /**
         * [BaseLayoutAdapter.LayoutAdapterUtil.inflate]
         * @param pixelDensity [PixelDensity]
         * @param binding [B]
         *
         * Invoke inflater of creating LayoutAdapter
         **/
        fun <B: ViewBinding, A: BaseLayoutAdapter<B>> LayoutAdapterInflater<B, A>.inflate(
            pixelDensity: PixelDensity, binding: B
        ): A = invoke(pixelDensity, binding)

    }

}