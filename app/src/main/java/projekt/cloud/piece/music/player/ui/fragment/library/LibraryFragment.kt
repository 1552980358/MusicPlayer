package projekt.cloud.piece.music.player.ui.fragment.library

import android.os.Bundle
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.base.LayoutCompatInflater
import projekt.cloud.piece.music.player.base.ViewBindingInflater
import projekt.cloud.piece.music.player.databinding.FragmentLibraryBinding
import projekt.cloud.piece.music.player.ui.fragment.library.LibraryLayoutCompat.LibraryLayoutCompatUtil
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo

private typealias BaseLibraryFragment = BaseMultiDensityFragment<FragmentLibraryBinding, LibraryLayoutCompat>

class LibraryFragment: BaseLibraryFragment(), LibraryFragmentInterface {

    override val viewBindingInflater: ViewBindingInflater<FragmentLibraryBinding>
        get() = FragmentLibraryBinding::inflate

    override val layoutCompatInflater: LayoutCompatInflater<FragmentLibraryBinding, LibraryLayoutCompat>
        get() = LibraryLayoutCompatUtil::inflate

    override fun onSetupLayoutCompat(layoutCompat: LibraryLayoutCompat, savedInstanceState: Bundle?) {
        layoutCompat.setupViewPager2(this)
        layoutCompat.setupTabLayout(resources)
        layoutCompat.setupReturnAnimation(this)
        layoutCompat.setupSlidingPane(this)
    }

    override fun navigateToArtist(id: String) {
        layoutCompat.tryTo<LibraryFragmentInterface> { libraryFragmentInterface ->
            libraryFragmentInterface.navigateToArtist(id)
        }
    }

    override fun navigateToAlbum(id: String) {
        layoutCompat.tryTo<LibraryFragmentInterface> { libraryFragmentInterface ->
            libraryFragmentInterface.navigateToAlbum(id)
        }
    }

}