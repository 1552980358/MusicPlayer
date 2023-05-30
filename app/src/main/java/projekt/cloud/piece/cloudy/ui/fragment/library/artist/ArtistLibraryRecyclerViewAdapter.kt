package projekt.cloud.piece.cloudy.ui.fragment.library.artist

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import projekt.cloud.piece.cloudy.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.cloudy.databinding.LibraryArtistRecyclerLayoutBinding
import projekt.cloud.piece.cloudy.storage.audio.view.ArtistView
import projekt.cloud.piece.cloudy.util.ViewBindingInflater

typealias LibraryArtistRecyclerLayoutViewBindingInflater =
    ViewBindingInflater<LibraryArtistRecyclerLayoutBinding>

/**
 * [ArtistLibraryRecyclerViewAdapter]
 * @extends [BaseRecyclerViewAdapter]
 *   @typeParam [LibraryArtistRecyclerLayoutBinding]
 * @param fragment [androidx.fragment.app.Fragment]
 * @param _artistList [List]<[ArtistView]>
 * @param onItemClicked [kotlin.jvm.functions.Function2]<[ArtistView], [Int], [Unit]>
 **/
class ArtistLibraryRecyclerViewAdapter(
    private val fragment: Fragment,
    private var _artistList: List<ArtistView>?,
    private val onItemClicked: (ArtistView, Int) -> Unit
): BaseRecyclerViewAdapter<LibraryArtistRecyclerLayoutBinding>() {

    /**
     * [ArtistLibraryRecyclerViewAdapter.artistList]
     **/
    private val artistList: List<ArtistView>
        get() = _artistList!!

    /**
     * [BaseRecyclerViewAdapter.viewBindingInflater]
     * @type [LibraryArtistRecyclerLayoutViewBindingInflater]
     **/
    override val viewBindingInflater: LibraryArtistRecyclerLayoutViewBindingInflater
        get() = LibraryArtistRecyclerLayoutBinding::inflate

    /**
     * [androidx.recyclerview.widget.RecyclerView.Adapter.getItemCount]
     * @return [Int]
     **/
    override fun getItemCount(): Int {
        return _artistList?.size ?: ITEM_EMPTY
    }

    /**
     * [BaseRecyclerViewAdapter.onBindViewHolder]
     * @param binding [LibraryArtistRecyclerLayoutBinding]
     **/
    override fun onViewBindingCreated(binding: LibraryArtistRecyclerLayoutBinding) {
        binding.onClicked = onItemClicked
    }

    /**
     * [BaseRecyclerViewAdapter.onBindViewHolder]
     * @param context [android.content.Context]
     * @param binding [LibraryArtistRecyclerLayoutBinding]
     * @param position [Int]
     **/
    override fun onBindViewHolder(
        context: Context, binding: LibraryArtistRecyclerLayoutBinding, position: Int
    ) {
        binding.index = position
        bindData(fragment, binding, artistList[position])
    }

    /**
     * [ArtistLibraryRecyclerViewAdapter.bindData]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param binding [LibraryArtistRecyclerLayoutBinding]
     * @param artist [ArtistView]
     **/
    private fun bindData(
        fragment: Fragment, binding: LibraryArtistRecyclerLayoutBinding, artist: ArtistView
    ) {
        binding.artist = artist
        bindArtistImage(fragment, binding.appCompatImageViewLeading, artist)
    }

    /**
     * [ArtistLibraryRecyclerViewAdapter.bindData]
     * @param fragment [androidx.fragment.app.Fragment]
     * @param artist [ArtistView]
     **/
    private fun bindArtistImage(
        fragment: Fragment, leading: AppCompatImageView, artist: ArtistView
    ) {
        // TODO: To be implemented in the future
    }

    /**
     * [ArtistLibraryRecyclerViewAdapter.updateArtistList]
     * @param artistList [List]<[ArtistView]>
     *
     * Update artist list
     **/
    fun updateArtistList(artistList: List<ArtistView>) {
        _artistList = artistList
        @Suppress("NotifyDataSetChanged")
        notifyDataSetChanged()
    }

}