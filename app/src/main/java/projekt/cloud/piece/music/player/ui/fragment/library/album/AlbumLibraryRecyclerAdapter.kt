package projekt.cloud.piece.music.player.ui.fragment.library.album

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.music.player.databinding.AlbumLibraryRecyclerLayoutBinding
import projekt.cloud.piece.music.player.storage.runtime.databaseView.AlbumView
import projekt.cloud.piece.music.player.util.KotlinUtil.to
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri

class AlbumLibraryRecyclerAdapter(
    private val albumList: List<AlbumView>,
    private val fragment: Fragment,
    private val onItemClick: (String, Int, View) -> Unit
): BaseRecyclerViewAdapter() {

    private class ViewHolder(
        parent: ViewGroup,
        onItemClick: (String, Int, View) -> Unit
    ): BaseBindingViewHolder<AlbumLibraryRecyclerLayoutBinding>(
        AlbumLibraryRecyclerLayoutBinding::inflate, parent
    ) {

        init {
            binding.onItemClick = onItemClick
        }

        private val art: ShapeableImageView
            get() = binding.shapeableImageViewImageArt

        fun bindData(fragment: Fragment, album: AlbumView, pos: Int) {
            bindData(album, pos)
            setCoverImage(fragment, album.id.albumArtUri)
        }

        private fun bindData(album: AlbumView, pos: Int) {
            binding.id = album.id
            binding.pos = pos
            binding.title = album.title
            binding.duration = album.duration.durationStr
        }

        private fun setCoverImage(fragment: Fragment, uri: Uri) {
            Glide.with(fragment)
                .load(uri)
                .into(art)
        }

    }

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
        return ViewHolder(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.to<ViewHolder>()
            .bindData(fragment, albumList[position], position)
    }

    override fun getItemCount() = albumList.size

}