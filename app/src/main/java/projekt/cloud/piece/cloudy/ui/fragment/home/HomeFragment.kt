package projekt.cloud.piece.cloudy.ui.fragment.home

import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.cloudy.base.BaseFragment
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.databinding.FragmentHomeBinding
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase
import projekt.cloud.piece.cloudy.storage.audio.AudioDatabase.AudioDatabaseUtil.audioDatabase
import projekt.cloud.piece.cloudy.storage.audio.view.MetadataView
import projekt.cloud.piece.cloudy.util.CoroutineUtil.ioBlocking
import projekt.cloud.piece.cloudy.util.LifecycleOwnerUtil.main
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

class HomeFragment: BaseMultiLayoutFragment<FragmentHomeBinding, HomeLayoutAdapter>() {

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
     * [HomeFragment._metadataList]
     * @type [List]<[MetadataView]>
     **/
    private var _metadataList: List<MetadataView>? = null

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [HomeLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: HomeLayoutAdapter, savedInstanceState: Bundle?) {
        layoutAdapter.setupRecyclerView(this, ::onRecyclerViewItemClicked)
        if (!layoutAdapter.updateMetadataList(_metadataList)) {
            main(::setupMetadataList)
        }
    }

    /**
     * [HomeFragment.onRecyclerViewItemClicked]
     * @param id [String]
     *
     * Triggered when [R.id.recycler_view] list content items are clicked
     **/
    private fun onRecyclerViewItemClicked(pos: Int) {
        // TODO: To be implemented for starting playing audio with id
    }

    /**
     * [HomeFragment.setupMetadataList]
     * @param coroutineScope [CoroutineScope]
     *
     * Setup metadata list
     **/
    private suspend fun setupMetadataList(
        @Suppress("UNUSED_PARAMETER")
        coroutineScope: CoroutineScope
    ) {
        updateMetadataList(
            queryMetadata(requireContext().audioDatabase)
        )
    }

    /**
     * [HomeFragment.queryMetadata]
     * @param audioDatabase [AudioDatabase]
     * @return [List]
     *
     * Query metadata list from [AudioDatabase]
     **/
    private suspend fun queryMetadata(audioDatabase: AudioDatabase): List<MetadataView> {
        return ioBlocking {
            audioDatabase.metadata
                .query()
        }
    }

    /**
     * [HomeFragment.updateMetadataList]
     * @param metadataList [List]<[MetadataView]>
     *
     * Update metadata list to [R.id.recycler_view]
     **/
    private fun updateMetadataList(metadataList: List<MetadataView>) {
        _metadataList = metadataList
        requireLayoutAdapter { layoutAdapter ->
            layoutAdapter.updateMetadataList(metadataList)
        }
    }

}