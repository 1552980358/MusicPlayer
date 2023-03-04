package projekt.cloud.piece.music.player.ui.fragment.mainHost

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.music.player.base.BaseLayoutCompat.BaseLayoutCompatUtil.layoutCompat
import projekt.cloud.piece.music.player.base.BaseMultiDensityFragment
import projekt.cloud.piece.music.player.databinding.FragmentMainHostBinding
import projekt.cloud.piece.music.player.util.ScreenDensity.ScreenDensityUtil.screenDensity

private typealias BaseMainHostFragment =
        BaseMultiDensityFragment<FragmentMainHostBinding, MainHostLayoutCompat>

class MainHostFragment: BaseMainHostFragment() {

    override val viewBindingClass: Class<FragmentMainHostBinding>
        get() = FragmentMainHostBinding::class.java

    override fun onCreateLayoutCompat(binding: FragmentMainHostBinding): MainHostLayoutCompat {
        return binding.layoutCompat(requireContext().screenDensity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutCompat.setupNavigation(
            binding.fragmentContainerView
                .getFragment<NavHostFragment>()
                .navController
        )
    }

}