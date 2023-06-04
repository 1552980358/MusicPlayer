package projekt.cloud.piece.cloudy.ui.fragment.library

import android.content.res.Resources
import android.view.ViewGroup.MarginLayoutParams
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.card.MaterialCardView
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.FragmentLibraryBinding
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT
import projekt.cloud.piece.cloudy.util.PixelDensity.EXPANDED
import projekt.cloud.piece.cloudy.util.PixelDensity.MEDIUM
import projekt.cloud.piece.cloudy.util.WindowInsetUtil.applySystemBarsInsets

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
 * @abstractImpl [LibraryLayoutAdapter.LargeScreenCommonImpl]
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
     * [LibraryLayoutAdapter.setupWindowInsets]
     *
     * @impl [LargeScreenCommonImpl.setupWindowInsets]
     **/
    open fun setupWindowInsets() = Unit

    /**
     * [LibraryLayoutAdapter.setupSlidingPane]
     * @param resources [android.content.res.Resources]
     *
     * @impl [W600dpImpl.setupSlidingPane]
     **/
    open fun setupSlidingPane(resources: Resources) = Unit

    /**
     * [LibraryLayoutAdapter.CompatImpl]
     * @extends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private class CompatImpl(binding: FragmentLibraryBinding): LibraryLayoutAdapter(binding)

    /**
     * [LibraryLayoutAdapter.LargeScreenCommonImpl]
     * @abstractExtends [LibraryLayoutAdapter]
     * @param binding [FragmentLibraryBinding]
     **/
    private abstract class LargeScreenCommonImpl(
        binding: FragmentLibraryBinding
    ): LibraryLayoutAdapter(binding) {

        /**
         * [LargeScreenCommonImpl.layoutRoot]
         * @type [androidx.constraintlayout.widget.ConstraintLayout]
         * @layout [R.layout.fragment_library]
         * @id [R.id.constraint_layout_root]
         **/
        private val layoutRoot: ConstraintLayout
            get() = binding.constraintLayoutRoot

        /**
         * [LibraryLayoutAdapter.setupWindowInsets]
         **/
        override fun setupWindowInsets() {
            layoutRoot.applySystemBarsInsets()
        }

    }

    /**
     * [LibraryLayoutAdapter.W600dpImpl]
     * @extends [LargeScreenCommonImpl]
     * @param binding [FragmentLibraryBinding]
     **/
    private class W600dpImpl(binding: FragmentLibraryBinding): LargeScreenCommonImpl(binding) {

        /**
         * [W600dpImpl.slidingPane]
         * @type [androidx.slidingpanelayout.widget.SlidingPaneLayout]
         * @layout [R.layout.fragment_library]
         * @id [R.id.sliding_pane_layout]
         **/
        private val slidingPane: SlidingPaneLayout
            get() = binding.slidingPaneLayout!!

        /**
         * [W600dpImpl.detailCard]
         * @type [com.google.android.material.card.MaterialCardView]
         * @layout [R.layout.fragment_library]
         * @id [R.id.material_card_view_detail]
         **/
        private val detailCard: MaterialCardView
            get() = binding.materialCardViewDetail!!

        /**
         * [LibraryLayoutAdapter.setupSlidingPane]
         * @param resources [android.content.res.Resources]
         **/
        override fun setupSlidingPane(resources: Resources) {
            slidingPane.doOnLayout {
                if (!slidingPane.isSlideable) {
                    // Set spacing
                    detailCard.updateLayoutParams<MarginLayoutParams> {
                        marginStart = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_24)
                    }
                }
            }
        }

    }

    /**
     * [LibraryLayoutAdapter.W1240dpImpl]
     * @extends [LargeScreenCommonImpl]
     * @param binding [FragmentLibraryBinding]
     **/
    private class W1240dpImpl(binding: FragmentLibraryBinding): LargeScreenCommonImpl(binding)

}