package projekt.cloud.piece.cloudy.ui.fragment.main_container

import android.os.Bundle
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.databinding.FragmentMainContainerBinding
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class MainContainerFragment: BaseMultiLayoutFragment<FragmentMainContainerBinding, MainContainerLayoutAdapter>() {

    override val viewBindingInflater: ViewBindingInflater<FragmentMainContainerBinding>
        get() = FragmentMainContainerBinding::inflate

    override val layoutAdapterBuilder: LayoutAdapterBuilder<FragmentMainContainerBinding, MainContainerLayoutAdapter>
        get() = MainContainerLayoutAdapter.builder

    override fun onSetupLayoutAdapter(layoutAdapter: MainContainerLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupNavigation()
    }

}