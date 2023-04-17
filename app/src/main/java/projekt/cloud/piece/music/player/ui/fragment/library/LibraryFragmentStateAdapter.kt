package projekt.cloud.piece.music.player.ui.fragment.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LibraryFragmentStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val fragments = listOf<Fragment>()

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