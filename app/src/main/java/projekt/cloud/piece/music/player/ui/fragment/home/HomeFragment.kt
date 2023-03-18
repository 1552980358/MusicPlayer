package projekt.cloud.piece.music.player.ui.fragment.home

import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.layoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentHomeBinding
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

class HomeFragment: BaseMultiDensityFragment<FragmentHomeBinding, HomeLayoutCompat>() {

    override val viewBindingClass: Class<FragmentHomeBinding>
        get() = FragmentHomeBinding::class.java

    override fun onCreateLayoutCompat(binding: FragmentHomeBinding): HomeLayoutCompat {
        return binding.layoutCompat(requireContext().screenDensity)
    }

}