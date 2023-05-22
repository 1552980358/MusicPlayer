package projekt.cloud.piece.cloudy.ui.fragment.home

import android.os.Bundle
import androidx.media3.common.Player
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.databinding.FragmentHomeBinding
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.ui.fragment.home.HomeViewModel.HomeViewModelUtil.homeViewModel
import projekt.cloud.piece.cloudy.util.MediaControllerHelper
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class HomeFragment: BaseMultiLayoutFragment<FragmentHomeBinding, HomeLayoutAdapter>(), Player.Listener {

    /**
     * [BaseFragment.viewBindingInflater]
     * @type [ViewBindingInflater]
     **/
    override val viewBindingInflater: ViewBindingInflater<FragmentHomeBinding>
        get() = FragmentHomeBinding::inflate

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [LayoutAdapterBuilder]
     **/
    override val layoutAdapterBuilder: LayoutAdapterBuilder<FragmentHomeBinding, HomeLayoutAdapter>
        get() = HomeLayoutAdapter.builder

    /**
     * [HomeFragment.viewModel]
     * @type [HomeViewModel]
     **/
    private val viewModel by homeViewModel()

    /**
     * [HomeFragment.mediaControllerHelper]
     * @type [MediaControllerHelper]
     **/
    private val mediaControllerHelper = MediaControllerHelper()

    /**
     * [androidx.fragment.app.Fragment.onCreate]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaControllerHelper.setupWithLifecycleOwner(this, this)
    }

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [HomeLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: HomeLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupRecyclerView(this, ::onRecyclerViewItemClicked)
        viewModel.requireMetadataList(this, ::onCompleteRequireMetadataList)
    }

    /**
     * [HomeFragment.onCompleteRequireMetadataList]
     * @param metadataList [List]<[MetadataView]>
     *
     * Triggered when [metadataList] is obtained in non-null instance
     * by [HomeViewModel.requireMetadataList]
     **/
    private fun onCompleteRequireMetadataList(metadataList: List<MetadataView>) {
        requireLayoutAdapter { layoutAdapter ->
            layoutAdapter.updateMetadataList(metadataList)
        }
    }

    /**
     * [HomeFragment.onRecyclerViewItemClicked]
     * @param pos [Int]
     *
     * Triggered when [R.id.recycler_view] list content items are clicked
     **/
    private fun onRecyclerViewItemClicked(pos: Int) {
        mediaControllerHelper.requireMediaController { mediaController ->
            viewModel.playAudioAtPos(
                this, mediaController, pos
            )
        }
    }

}