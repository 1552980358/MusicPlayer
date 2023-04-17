package projekt.cloud.piece.music.player.ui.fragment.library.artist

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.music.player.databinding.ArtistLibraryChildRecyclerLayoutBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.KotlinUtil.to
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri

class ArtistLibraryChildRecyclerAdapter(
    private val fragment: Fragment
): BaseRecyclerViewAdapter() {

    private class ViewHolder(
        private val binding: ArtistLibraryChildRecyclerLayoutBinding
    ): BaseViewHolder(binding) {

        private val image: ShapeableImageView
            get() = binding.shapeableImageViewImage

        fun loadImage(fragment: Fragment, id: String) {
            loadImage(fragment, id.albumArtUri)
        }

        fun loadImage(fragment: Fragment, uri: Uri) {
            Glide.with(fragment)
                .load(uri)
                .into(image)
        }

    }

    private var albumList: List<String>? = null

    fun updateAlbumList(albumList: List<String>) {
        this.albumList = albumList
        fragment.lifecycleScope.main {
            @Suppress("NotifyDataSetChanged")
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
        return ViewHolder(createViewBinding(layoutInflater, parent))
    }

    private fun createViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup) =
        ArtistLibraryChildRecyclerLayoutBinding.inflate(layoutInflater, parent, false)

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        albumList?.get(position)?.let { album ->
            holder.to<ViewHolder>()
                .loadImage(fragment, album)
        }
    }

    override fun getItemCount() = albumList?.size ?: 0


}