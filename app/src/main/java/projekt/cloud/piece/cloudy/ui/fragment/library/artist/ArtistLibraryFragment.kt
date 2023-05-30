package projekt.cloud.piece.cloudy.ui.fragment.library.artist

import android.os.Bundle
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView
import projekt.cloud.piece.cloudy.ui.fragment.library.BaseLibraryChildFragment
import projekt.cloud.piece.cloudy.ui.fragment.library.artist.ArtistLibraryViewModel.ArtistLibraryViewModelUtil.artistLibraryViewModel

/**
 * [ArtistLibraryFragment]
 * @extends [BaseLibraryChildFragment]
 *   @typeParam [ArtistLibraryLayoutAdapter]
 **/
class ArtistLibraryFragment: BaseLibraryChildFragment<ArtistLibraryLayoutAdapter>() {

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [ArtistLibraryLayoutAdapterBuilder]
     **/
    override val layoutAdapterBuilder: ArtistLibraryLayoutAdapterBuilder
        get() = ArtistLibraryLayoutAdapter.builder

    /**
     * [ArtistLibraryFragment.viewModel]
     * @type [ArtistLibraryViewModel]
     **/
    private val viewModel by artistLibraryViewModel()

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [ArtistLibraryLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: ArtistLibraryLayoutAdapter, savedInstanceState: Bundle?) {
        viewModel.requireArtistList(
            this, ::setupRecyclerViewAdapter, ::onRequestedArtistList
        )
    }

    /**
     * [ArtistLibraryFragment.setupRecyclerViewAdapter]
     * @param artistList [List]<[ArtistView]>
     *
     * Called for create adapter for [R.id.recycler_view]
     **/
    private fun setupRecyclerViewAdapter(artistList: List<ArtistView>?) {
        layoutAdapter safely { layoutAdapter ->
            layoutAdapter.setupRecyclerViewAdapter(
                this, artistList, ::onRecyclerViewItemClicked
            )
        }
    }

    /**
     * [ArtistLibraryFragment.onRecyclerViewItemClicked]
     * @param artist [ArtistView]
     * @param index [Int]
     *
     * Triggered when adapter item in [R.id.recycler_view] clicked
     **/
    private fun onRecyclerViewItemClicked(artist: ArtistView, index: Int) {
        // TODO: Ta be implemented
    }

    /**
     * [ArtistLibraryFragment.onRequestedArtistList]
     * @param artistList [List]<[ArtistView]>
     *
     * Called when [artistList] queried from database
     **/
    private fun onRequestedArtistList(artistList: List<ArtistView>) {
        layoutAdapter safely { layoutAdapter ->
            layoutAdapter.updateRecyclerViewArtistList(artistList)
        }
    }

}