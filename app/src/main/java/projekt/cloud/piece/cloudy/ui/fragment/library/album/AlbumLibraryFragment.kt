package projekt.cloud.piece.cloudy.ui.fragment.library.album

import android.os.Bundle
import projekt.cloud.piece.cloudy.base.BaseMultiLayoutFragment
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView
import projekt.cloud.piece.cloudy.ui.fragment.library.BaseLibraryChildFragment
import projekt.cloud.piece.cloudy.ui.fragment.library.album.AlbumLibraryViewModel.AlbumLibraryViewModelUtil.albumLibraryViewModel

/**
 * [AlbumLibraryFragment]
 * @extends [BaseLibraryChildFragment]
 *   @typeParam [AlbumLibraryLayoutAdapter]
 **/
class AlbumLibraryFragment: BaseLibraryChildFragment<AlbumLibraryLayoutAdapter>() {

    /**
     * [BaseMultiLayoutFragment.layoutAdapterBuilder]
     * @type [AlbumLibraryLayoutAdapterBuilder]
     **/
    override val layoutAdapterBuilder: AlbumLibraryLayoutAdapterBuilder
        get() = AlbumLibraryLayoutAdapter.builder

    /**
     * [AlbumLibraryFragment.viewModel]
     * @type [AlbumLibraryViewModel]
     **/
    private val viewModel by albumLibraryViewModel()

    /**
     * [BaseMultiLayoutFragment.onSetupLayoutAdapter]
     * @param layoutAdapter [AlbumLibraryLayoutAdapter]
     * @param savedInstanceState [android.os.Bundle]
     **/
    override fun onSetupLayoutAdapter(layoutAdapter: AlbumLibraryLayoutAdapter, savedInstanceState: Bundle?) {
        viewModel.requireAlbumList(
            this, ::onSetupRecyclerViewAdapter, ::onRequestedAlbumList
        )
    }

    /**
     * [AlbumLibraryFragment.onSetupRecyclerViewAdapter]
     * @param albumList [List]<[AlbumView]>
     **/
    private fun onSetupRecyclerViewAdapter(albumList: List<AlbumView>?) {
        layoutAdapter safely { layoutAdapter ->
            layoutAdapter.setupRecyclerView(this, albumList, ::onRecyclerViewItemClicked)
        }
    }

    /**
     * [AlbumLibraryFragment.onRecyclerViewItemClicked]
     * @param album [AlbumView]
     * @param index [Int]
     *
     * Triggered when adapter item in [R.id.recycler_view] clicked
     **/
    private fun onRecyclerViewItemClicked(album: AlbumView, index: Int) {
        // TODO: To be implemented
    }

    /**
     * [AlbumLibraryFragment.onRequestedAlbumList]
     * @param albumList [List]<[AlbumView]>
     *
     * Called when [albumList] queried from database
     **/
    private fun onRequestedAlbumList(albumList: List<AlbumView>) {
        layoutAdapter safely { layoutAdapter ->
            layoutAdapter.updateRecyclerViewAlbumList(albumList)
        }
    }

}