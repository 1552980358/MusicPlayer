package projekt.cloud.piece.music.player.ui.fragment.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import projekt.cloud.piece.music.player.ui.fragment.library.album.AlbumLibraryFragment
import projekt.cloud.piece.music.player.ui.fragment.library.artist.ArtistLibraryFragment

class LibraryFragmentStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        ArtistLibraryFragment(),
        AlbumLibraryFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    operator fun get(position: Int): Fragment {
        return fragments[position]
    }

}