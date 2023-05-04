package projekt.cloud.piece.music.player.ui.fragment.artist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.music.player.databinding.ArtistRecyclerLayoutBinding
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.util.KotlinUtil.to
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri

class ArtistRecyclerAdapter(
    private val fragment: Fragment,
    private val audioList: List<AudioMetadataEntity>,
    private val onItemClick: (String) -> Unit
): BaseRecyclerViewAdapter() {

    private inner class ViewHolder(
        parent: ViewGroup
    ): BaseBindingViewHolder<ArtistRecyclerLayoutBinding>(
        ArtistRecyclerLayoutBinding::inflate, parent
    ) {

        init {
            binding.onItemClick = onItemClick
        }

        private val cover: ShapeableImageView
            get() = binding.shapeableImageViewCover

        fun bind(fragment: Fragment, audioMetadata: AudioMetadataEntity) {
            bindData(audioMetadata)
            requireAlbumCover(fragment, audioMetadata.album)
        }

        private fun bindData(audioMetadata: AudioMetadataEntity) {
            binding.id = audioMetadata.id
            binding.title = audioMetadata.title
            binding.durationStr = audioMetadata.duration.durationStr
        }

        private fun requireAlbumCover(fragment: Fragment, albumId: String) {
            Glide.with(fragment)
                .load(albumId.albumArtUri)
                .into(cover)
        }

    }

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.to<ViewHolder>()
            .bind(fragment, audioList[position])
    }

    override fun getItemCount() = audioList.size

}