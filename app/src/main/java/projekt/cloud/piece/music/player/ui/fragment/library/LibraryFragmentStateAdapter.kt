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

        private val childFragments: List<BaseLibraryObjectFragment<*>>
            get() = listOf(ArtistLibraryFragment(), AlbumLibraryFragment())

    }

    constructor(fragment: Fragment): this(
        fragment, fragment.childFragments(childFragments)
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