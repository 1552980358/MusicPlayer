package projekt.cloud.piece.music.player.ui.fragment.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import projekt.cloud.piece.music.player.ui.fragment.library.album.AlbumLibraryFragment
import projekt.cloud.piece.music.player.ui.fragment.library.artist.ArtistLibraryFragment
import projekt.cloud.piece.music.player.ui.fragment.library.base.BaseLibraryObjectFragment
import projekt.cloud.piece.music.player.util.FragmentUtil.childFragments
import projekt.cloud.piece.music.player.util.KotlinUtil.to

class LibraryFragmentStateAdapter private constructor(
    fragment: Fragment,
    private val fragments: List<Fragment>
): FragmentStateAdapter(fragment) {

    private companion object {

        private val libraryFragments: List<BaseLibraryObjectFragment<*>>
            get() = listOf(ArtistLibraryFragment(), AlbumLibraryFragment())

        private fun Fragment.childFragments(): List<BaseLibraryObjectFragment<*>> {
            val fragmentList = childFragments.filterIsInstance<BaseLibraryObjectFragment<*>>()
                // Look at source code, it should be typed ArrayList
                .to<ArrayList<BaseLibraryObjectFragment<*>>>()

            if (fragmentList.isEmpty()) {
                return libraryFragments
            }

            if (fragmentList.size == 1) {
                when (fragmentList.first()) {
                    is ArtistLibraryFragment -> {
                        fragmentList.add(AlbumLibraryFragment())
                    }
                    is AlbumLibraryFragment -> {
                        fragmentList.add(0, ArtistLibraryFragment())
                    }
                }
            }

            // fragmentList.size should > 1
            return fragmentList
        }

    }

    constructor(fragment: Fragment): this(
        fragment, fragment.childFragments()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    operator fun get(position: Int): BaseLibraryObjectFragment<*> {
        return fragments[position].to()
    }

}