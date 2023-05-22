package projekt.cloud.piece.cloudy.ui.fragment.main_container

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.databinding.FragmentMainContainerBinding
import projekt.cloud.piece.cloudy.ui.fragment.main_container.MainContainerViewModel.MainContainerViewModelUtil.mainContainerViewModel
import projekt.cloud.piece.cloudy.util.MediaControllerHelper
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

private typealias BaseMainContainerFragment =
    BaseMultiLayoutFragment<FragmentMainContainerBinding, MainContainerLayoutAdapter>

class MainContainerFragment: BaseMainContainerFragment(), Player.Listener {

    override val viewBindingInflater: ViewBindingInflater<FragmentMainContainerBinding>
        get() = FragmentMainContainerBinding::inflate

    override val layoutAdapterBuilder: LayoutAdapterBuilder<FragmentMainContainerBinding, MainContainerLayoutAdapter>
        get() = MainContainerLayoutAdapter.builder

    private val viewModel by mainContainerViewModel()

    private val mediaControllerHelper = MediaControllerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaControllerHelper.setupWithLifecycleOwner(this)
    }

    override fun onSetupBinding(binding: FragmentMainContainerBinding, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        super.onSetupBinding(binding, savedInstanceState)
    }

    override fun onSetupLayoutAdapter(layoutAdapter: MainContainerLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupFragmentContainerViewMargins()
        layoutAdapter.setupNavigation()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {

    }

}