package projekt.cloud.piece.cloudy.ui.fragment.library

import android.os.Bundle
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.databinding.FragmentLibraryBinding
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

/**
 * [LibraryFragment]
 * @extends [BaseMultiLayoutFragment]
 *   @typeParam [FragmentLibraryBinding]
 *   @typeParam [LibraryLayoutAdapter]
 **/
class LibraryFragment: BaseMultiLayoutFragment<FragmentLibraryBinding, LibraryLayoutAdapter>() {

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<FragmentLibraryBinding>
        get() = FragmentLibraryBinding::inflate

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @type [LibraryLayoutAdapterBuilder]
     **/
    override val layoutAdapterBuilder: LibraryLayoutAdapterBuilder
        get() = LibraryLayoutAdapter.builder

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [LibraryLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: LibraryLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupViewPager(this)
        layoutAdapter.setupTabs(resources)
        layoutAdapter.setupLayoutColors()
    }

}