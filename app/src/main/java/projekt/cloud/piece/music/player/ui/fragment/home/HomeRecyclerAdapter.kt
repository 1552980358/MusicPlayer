package projekt.cloud.piece.music.player.ui.fragment.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.music.player.databinding.HomeRecyclerLayoutBinding
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri

class HomeRecyclerAdapter(
    private val fragment: Fragment,
    private val audioList: List<AudioMetadataEntity>,
    private val onItemClick: (String) -> Unit
): BaseRecyclerViewAdapter() {

    private class ViewHolder(
        parent: ViewGroup,
        onItemClick: (String) -> Unit
    ): BaseBindingViewHolder<HomeRecyclerLayoutBinding>(
        HomeRecyclerLayoutBinding::inflate, parent
    ) {

        init {
            binding.onItemClick = onItemClick
        }

        private val image: ShapeableImageView
            get() = binding.shapeableImageViewImage

        fun bind(fragment: Fragment, audioMetadata: AudioMetadataEntity) {
            setMetadata(audioMetadata)
            startObtainAlbumCover(fragment, audioMetadata.album.albumArtUri)
        }

        private fun setMetadata(audioMetadata: AudioMetadataEntity) {
            binding.id = audioMetadata.id
            binding.title = audioMetadata.title
            binding.artist = audioMetadata.artistName
            binding.album = audioMetadata.albumTitle
            binding.duration = audioMetadata.duration.durationStr
        }

        private fun startObtainAlbumCover(fragment: Fragment, uri: Uri) {
            fragment.lifecycleScope.main {
                Glide.with(fragment)
                    .load(uri)
                    .into(image)
            }
        }

    }

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
         return ViewHolder(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.tryTo<ViewHolder>()
            ?.bind(fragment, audioList[position])
    }

    override fun getItemCount() = audioList.size

}