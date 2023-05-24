package projekt.cloud.piece.cloudy.ui.fragment.main_container

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.databinding.FragmentMainContainerBinding
import projekt.cloud.piece.cloudy.ui.fragment.main_container.MainContainerViewModel.MainContainerViewModelUtil.mainContainerViewModel
import projekt.cloud.piece.cloudy.util.helper.MediaControllerHelper
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

private typealias BaseMainContainerFragment =
    BaseMultiLayoutFragment<FragmentMainContainerBinding, MainContainerLayoutAdapter>

class MainContainerFragment: BaseMainContainerFragment(), Player.Listener {

    /**
     * [BaseFragment.viewBindingInflater]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<FragmentMainContainerBinding>
        get() = FragmentMainContainerBinding::inflate

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [ViewBindingInflater]
     **/
    override val layoutAdapterBuilder: LayoutAdapterBuilder<FragmentMainContainerBinding, MainContainerLayoutAdapter>
        get() = MainContainerLayoutAdapter.builder

    /**
     * [MainContainerFragment.viewModel]
     * @type [MainContainerViewModel]
     **/
    private val viewModel by mainContainerViewModel()

    /**
     * [MainContainerFragment.mediaControllerHelper]
     * @type [MediaControllerHelper]
     **/
    private val mediaControllerHelper = MediaControllerHelper()

    /**
     * [androidx.fragment.app.Fragment.onCreate]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaControllerHelper.setupWithLifecycleOwner(this)
    }

    /**
     * [BaseFragment.setupBinding]
     * @param binding [FragmentMainContainerBinding]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupBinding(binding: FragmentMainContainerBinding, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        super.onSetupBinding(binding, savedInstanceState)
    }

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [MainContainerLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: MainContainerLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupDynamicLayout(resources)
        layoutAdapter.setupMiniPlayer(viewModel)
        layoutAdapter.setupMiniPlayerCoverObserving(this, viewModel)
        layoutAdapter.setupNavigation()
    }

    /**
     * [androidx.media3.common.Player.Listener.onMediaItemTransition]
     * @param mediaItem [androidx.media3.common.MediaItem]
     * @param reason [Int]
     **/
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        mediaItem?.let {
            viewModel.setMetadata(this, mediaItem)
        }
    }

    /**
     * [androidx.media3.common.Player.Listener.onIsPlayingChanged]
     * @param isPlaying [Boolean]
     **/
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            layoutAdapter.safely { layoutAdapter ->
                layoutAdapter.ensureMiniPlayVisible()
            }
        }
    }

}