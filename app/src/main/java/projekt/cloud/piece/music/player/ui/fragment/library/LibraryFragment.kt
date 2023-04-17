package projekt.cloud.piece.music.player.ui.fragment.library

import android.os.Bundle
import kotlin.reflect.KClass
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentLibraryBinding

class LibraryFragment: BaseMultiDensityFragment<FragmentLibraryBinding, LibraryLayoutCompat>() {

    override val viewBindingClass: Class<FragmentLibraryBinding>
        get() = FragmentLibraryBinding::class.java

    override val layoutCompatClass: KClass<LibraryLayoutCompat>
        get() = LibraryLayoutCompat::class

    override fun onSetupLayoutCompat(layoutCompat: LibraryLayoutCompat, savedInstanceState: Bundle?) {
        layoutCompat.setupViewPager2(this)
        layoutCompat.setupTabLayout(resources)
    }

}