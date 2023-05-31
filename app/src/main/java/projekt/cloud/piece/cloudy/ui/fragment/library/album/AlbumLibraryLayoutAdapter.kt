package projekt.cloud.piece.cloudy.ui.fragment.library.album

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.cloudy.R
import projekt.cloud.piece.cloudy.base.BaseLayoutAdapter
import projekt.cloud.piece.cloudy.base.LayoutAdapterBuilder
import projekt.cloud.piece.cloudy.base.LayoutAdapterConstructor
import projekt.cloud.piece.cloudy.databinding.LibraryChildFragmentBinding
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView
import projekt.cloud.piece.cloudy.util.CastUtil.cast
import projekt.cloud.piece.cloudy.util.PixelDensity
import projekt.cloud.piece.cloudy.util.PixelDensity.COMPAT

typealias AlbumLibraryLayoutAdapterBuilder =
    LayoutAdapterBuilder<LibraryChildFragmentBinding, AlbumLibraryLayoutAdapter>

private typealias AlbumLibraryLayoutAdapterConstructor =
    LayoutAdapterConstructor<LibraryChildFragmentBinding, AlbumLibraryLayoutAdapter>

/**
 * [AlbumLibraryLayoutAdapter]
 * @abstractExtends [BaseLayoutAdapter]
 *   @typeParam [LibraryChildFragmentBinding]
 * @param binding [LibraryChildFragmentBinding]
 *
 * @impl [AlbumLibraryLayoutAdapter.CompatImpl], [AlbumLibraryLayoutAdapter.LargeScreenImpl]
 **/
abstract class AlbumLibraryLayoutAdapter(
    binding: LibraryChildFragmentBinding
): BaseLayoutAdapter<LibraryChildFragmentBinding>(binding) {

    companion object {

        /**
         * [AlbumLibraryLayoutAdapter.builder]
         * @type [AlbumLibraryLayoutAdapterBuilder]
         **/
        val builder: AlbumLibraryLayoutAdapterBuilder
            get() = ::buildAdapter

        /**
         * [AlbumLibraryLayoutAdapter.builder]
         * @param pixelDensity [PixelDensity]
         * @return [AlbumLibraryLayoutAdapterConstructor]
         **/
        private fun buildAdapter(pixelDensity: PixelDensity): AlbumLibraryLayoutAdapterConstructor {
            return when (pixelDensity) {
                COMPAT -> ::CompatImpl
                else -> ::LargeScreenImpl
            }
        }

    }

    /**
     * [AlbumLibraryLayoutAdapter.recyclerView]
     * @type [androidx.recyclerview.widget.RecyclerView]
     * @layout [R.layout.library_child_fragment]
     * @id [R.id.recycler_view]
     **/
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    /**
     * [AlbumLibraryLayoutAdapter.setupRecyclerView]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param albumList [List]<[AlbumView]>
     * @param onItemClicked [kotlin.jvm.functions.Function2]<[AlbumView], [Int], [Unit]>
     **/
    fun setupRecyclerView(
        fragment: Fragment,
        albumList: List<AlbumView>?,
        onItemClicked: (AlbumView, Int) -> Unit
    ) {
        recyclerView.adapter = AlbumLibraryRecyclerViewAdapter(fragment, albumList, onItemClicked)
    }

    /**
     * [AlbumLibraryLayoutAdapter.updateRecyclerViewAlbumList]
     * @param albumList [List]<[AlbumView]>
     **/
    fun updateRecyclerViewAlbumList(albumList: List<AlbumView>) {
        recyclerView.adapter
            .cast<AlbumLibraryRecyclerViewAdapter>()
            .updateAlbumList(albumList)
    }

    private class CompatImpl(binding: LibraryChildFragmentBinding): AlbumLibraryLayoutAdapter(binding)

    private class LargeScreenImpl(binding: LibraryChildFragmentBinding): AlbumLibraryLayoutAdapter(binding)

}