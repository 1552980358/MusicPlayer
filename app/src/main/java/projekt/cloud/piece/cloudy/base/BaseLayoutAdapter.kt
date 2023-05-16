package projekt.cloud.piece.cloudy.base

import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.cloudy.util.PixelDensity

typealias LayoutAdapterConstructor<B, A> = (B) -> A
typealias LayoutAdapterBuilder<B, A> = (PixelDensity) -> LayoutAdapterConstructor<B, A>

abstract class BaseLayoutAdapter<B: ViewBinding>(protected val binding: B) {

    companion object LayoutAdapterUtil {

        /**
         * [BaseLayoutAdapter.LayoutAdapterUtil.build]
         * @param pixelDensity [PixelDensity]
         * @param binding [B]
         * @return [A]
         **/
        fun <B: ViewBinding, A: BaseLayoutAdapter<B>> LayoutAdapterBuilder<B, A>.build(
            pixelDensity: PixelDensity, binding: B
        ): A {
            /**
             * [LayoutAdapterBuilder].invoke(pixelDensity) => [LayoutAdapterConstructor]
             * [LayoutAdapterConstructor].invoke(binding) => Child of [BaseLayoutAdapter]
             **/
            return this(pixelDensity)(binding)
        }

    }

}