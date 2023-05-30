package projekt.cloud.piece.cloudy.ui.fragment.library.artist

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.LibraryChildFragmentBinding
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView
import projekt.cloud.piece.cloudy.util.CastUtil.cast
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT

typealias ArtistLibraryLayoutAdapterBuilder =
    LayoutAdapterBuilder<LibraryChildFragmentBinding, ArtistLibraryLayoutAdapter>

private typealias ArtistLibraryLayoutAdapterConstructor =
    LayoutAdapterConstructor<LibraryChildFragmentBinding, ArtistLibraryLayoutAdapter>

/**
 * [ArtistLibraryLayoutAdapter]
 * @abstractExtends [BaseLayoutAdapter]
 *   @typeParam [LibraryChildFragmentBinding]
 * @param binding [LibraryChildFragmentBinding]
 *
 * @impl [ArtistLibraryLayoutAdapter.CompatImpl], [ArtistLibraryLayoutAdapter.LargeScreenImpl]
 **/
abstract class ArtistLibraryLayoutAdapter(
    binding: LibraryChildFragmentBinding
): BaseLayoutAdapter<LibraryChildFragmentBinding>(binding) {

    companion object {

        /**
         * [ArtistLibraryLayoutAdapter.builder]
         * @type [ArtistLibraryLayoutAdapterBuilder]
         **/
        val builder: ArtistLibraryLayoutAdapterBuilder
            get() = ::builder

        /**
         * [ArtistLibraryLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @return [ArtistLibraryLayoutAdapterConstructor]
         **/
        private fun builder(pixelDensity: PixelDensity): ArtistLibraryLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                else -> ::LargeScreenImpl
            }
        }

    }

    /**
     * [ArtistLibraryLayoutAdapter.recyclerView]
     * @type [androidx.recyclerview.widget.RecyclerView]
     * @layout [R.layout.library_child_fragment]
     * @id [R.id.recycler_view]
     **/
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    /**
     * [ArtistLibraryLayoutAdapter.setupRecyclerViewAdapter]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param artistList [List]<[ArtistView]>
     * @param onItemClicked [kotlin.jvm.functions.Function2]<[ArtistView], [Int], [Unit]>
     *
     * Create adapter for [R.id.recycler_view]
     **/
    fun setupRecyclerViewAdapter(
        fragment: Fragment,
        artistList: List<ArtistView>?,
        onItemClicked: (ArtistView, Int) -> Unit
    ) {
        recyclerView.adapter = ArtistLibraryRecyclerViewAdapter(
            fragment, artistList, onItemClicked
        )
    }

    /**
     * [ArtistLibraryLayoutAdapter.updateRecyclerViewArtistList]
     * @param artistList [List]<[ArtistView]>
     *
     * Update list to [ArtistLibraryRecyclerViewAdapter] of [R.id.recycler_view]
     **/
    fun updateRecyclerViewArtistList(artistList: List<ArtistView>) {
        recyclerView.adapter
            .cast<ArtistLibraryRecyclerViewAdapter>()
            .updateArtistList(artistList)
    }

    /**
     * [ArtistLibraryLayoutAdapter.CompatImpl]
     * @extends [ArtistLibraryLayoutAdapter]
     * @param [binding]
     **/
    private class CompatImpl(binding: LibraryChildFragmentBinding): ArtistLibraryLayoutAdapter(binding)

    /**
     * [ArtistLibraryLayoutAdapter.LargeScreenImpl]
     * @extends [ArtistLibraryLayoutAdapter]
     * @param [binding]
     **/
    private class LargeScreenImpl(binding: LibraryChildFragmentBinding): ArtistLibraryLayoutAdapter(binding)

}