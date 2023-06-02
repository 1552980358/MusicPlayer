package projekt.cloud.piece.cloudy.ui.fragment.library

import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentLibraryBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.PixelDensity.EXPANDED
import projekt.cloud.piece.cloudy.util.PixelDensity.MEDIUM

typealias LibraryLayoutAdapterBuilder =
    LayoutAdapterBuilder<FragmentLibraryBinding, LibraryLayoutAdapter>
private typealias LibraryLayoutAdapterConstructor =
    LayoutAdapterConstructor<FragmentLibraryBinding, LibraryLayoutAdapter>

/**
 * [LibraryLayoutAdapter]
 * @abstractExtends [BaseLayoutAdapter]
 *   @typeParam [FragmentLibraryBinding]
 * @param binding [FragmentLibraryBinding]
 *
 * @impl [LibraryLayoutAdapter.CompatImpl], [LibraryLayoutAdapter.W600dpImpl], [LibraryLayoutAdapter.W1240dpImpl]
 **/
abstract class LibraryLayoutAdapter(
    binding: FragmentLibraryBinding
): BaseLayoutAdapter<FragmentLibraryBinding>(binding) {

    companion object {

        /**
         * [LibraryLayoutAdapter.builder]
         * @type [LibraryLayoutAdapterBuilder]
         **/
        val builder: LibraryLayoutAdapterBuilder
            get() = ::builder

        /**
         * [LibraryLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @return [LibraryLayoutAdapterConstructor]
         **/
        private fun builder(pixelDensity: PixelDensity): LibraryLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                MEDIUM -> ::W600dpImpl
                EXPANDED -> ::W1240dpImpl
            }
        }

    }

    /**
     * [LibraryLayoutAdapter.CompatImpl]
     * @extends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private class CompatImpl(binding: FragmentLibraryBinding): LibraryLayoutAdapter(binding)

    /**
     * [LibraryLayoutAdapter.W600dpImpl]
     * @extends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private class W600dpImpl(binding: FragmentLibraryBinding): LibraryLayoutAdapter(binding)

    /**
     * [LibraryLayoutAdapter.W1240dpImpl]
     * @extends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private class W1240dpImpl(binding: FragmentLibraryBinding): LibraryLayoutAdapter(binding)

}