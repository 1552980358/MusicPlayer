package projekt.cloud.piece.cloudy.ui.fragment.library.album

import android.content.Context
import androidx.fragment.app.Fragment
import projekt.cloud.piece.cloudy.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.cloudy.databinding.LibraryAlbumRecyclerLayoutBinding
import projekt.cloud.piece.cloudy.storage.audio.view.AlbumView
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

private typealias LibraryAlbumRecyclerLayoutViewBindingInflater =
    ViewBindingInflater<LibraryAlbumRecyclerLayoutBinding>

/**
 * [AlbumLibraryRecyclerViewAdapter]
 * @extends [BaseRecyclerViewAdapter]
 *   @typeParam [LibraryAlbumRecyclerLayoutBinding]
 * @param fragment [androidx.fragment.app.Fragment]
 * @param _albumList [List]<[AlbumView]>
 * @param onItemClicked [kotlin.jvm.functions.Function2]<[AlbumView], [Int], [Unit]>
 **/
class AlbumLibraryRecyclerViewAdapter(
    private val fragment: Fragment,
    private var _albumList: List<AlbumView>?,
    private val onItemClicked: (AlbumView, Int) -> Unit
): BaseRecyclerViewAdapter<LibraryAlbumRecyclerLayoutBinding>() {

    private val albumList: List<AlbumView>
        get() = _albumList!!

    /**
     * [BaseRecyclerViewAdapter.viewBindingInflater]
     * @type [LibraryAlbumRecyclerLayoutViewBindingInflater]
     **/
    override val viewBindingInflater: LibraryAlbumRecyclerLayoutViewBindingInflater
        get() = LibraryAlbumRecyclerLayoutBinding::inflate

    /**
     * [androidx.recyclerview.widget.RecyclerView.Adapter.getItemCount]
     * @return [Int]
     **/
    override fun getItemCount(): Int {
        return _albumList?.size ?: ITEM_EMPTY
    }

    /**
     * [BaseRecyclerViewAdapter.onViewBindingCreated]
     * @param binding [LibraryAlbumRecyclerLayoutBinding]
     **/
    override fun onViewBindingCreated(binding: LibraryAlbumRecyclerLayoutBinding) {
        binding.onClicked = onItemClicked
    }

    /**
     * [BaseRecyclerViewAdapter.onBindViewHolder]
     * @param context [android.content.Context]
     * @param binding [LibraryAlbumRecyclerLayoutBinding]
     * @param position [Int]
     **/
    override fun onBindViewHolder(
        context: Context, binding: LibraryAlbumRecyclerLayoutBinding, position: Int
    ) {
        binding.index = position
        bindData(fragment, binding, albumList[position])
    }

    /**
     * [AlbumLibraryRecyclerViewAdapter]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param binding [LibraryAlbumRecyclerLayoutBinding]
     * @param album [AlbumView]
     **/
    private fun bindData(
        fragment: Fragment, binding: LibraryAlbumRecyclerLayoutBinding, album: AlbumView
    ) {
        binding.album = album
        bindAlbumImage(fragment, album)
    }

    /**
     * [AlbumLibraryRecyclerViewAdapter]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param album [AlbumView]
     **/
    private fun bindAlbumImage(fragment: Fragment, album: AlbumView) {
        // TODO: To be implemented
    }

    /**
     * [AlbumLibraryRecyclerViewAdapter]
     * @param albumList [List]<[AlbumView]>
     **/
    fun updateAlbumList(albumList: List<AlbumView>) {
        _albumList = albumList
        @Suppress("NotifyDataSetChanged")
        notifyDataSetChanged()
    }

}