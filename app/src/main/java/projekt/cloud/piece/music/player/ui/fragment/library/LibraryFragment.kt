package projekt.cloud.piece.music.player.ui.fragment.library

import android.os.Bundle
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryBinding
import projekt.cloud.piece.music.player.ui.fragment.library.LibraryLayoutCompat.LibraryLayoutCompatUtil

class LibraryFragment: BaseMultiDensityFragment<FragmentLibraryBinding, LibraryLayoutCompat>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentLibraryBinding>
        get() = FragmentLibraryBinding::inflate

    override val layoutCompatInflater: LayoutCompatInflater<FragmentLibraryBinding, LibraryLayoutCompat>
        get() = LibraryLayoutCompatUtil::inflate

    override fun onSetupLayoutCompat(layoutCompat: LibraryLayoutCompat, savedInstanceState: Bundle?) {
        layoutCompat.setupViewPager2(this)
        layoutCompat.setupTabLayout(resources)
        layoutCompat.setupRootBottomMargin(this)
    }

}