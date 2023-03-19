package projekt.cloud.piece.music.player.ui.fragment.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.music.player.databinding.HomeRecyclerLayoutBinding
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.util.CoroutineUtil.main
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr
import projekt.cloud.piece.music.player.util.UriUtil.albumArtUri

object HomeRecyclerViewUtil {

    private class ViewHolder(private val binding: HomeRecyclerLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        private val image: ShapeableImageView
            get() = binding.shapeableImageViewImage

        fun bind(fragment: Fragment, audioMetadata: AudioMetadataEntity) {
            with(binding) {
                title = audioMetadata.title
                artist = audioMetadata.artistName
                album = audioMetadata.albumTitle
                duration = audioMetadata.duration.durationStr
            }
            startObtainAlbumCover(fragment, audioMetadata.album.albumArtUri)
        }

        private fun startObtainAlbumCover(fragment: Fragment, uri: Uri) {
            fragment.lifecycleScope.main {
                Glide.with(fragment)
                    .load(uri)
                    .into(image)
            }
        }

    }

    private class Adapter(
        private val fragment: Fragment, private val audioList: List<AudioMetadataEntity>
    ): RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
            HomeRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(fragment, audioList[position])
        }

        override fun getItemCount() = audioList.size

    }

    fun getRecyclerViewAdapter(
        fragment: Fragment, audioList: List<AudioMetadataEntity>
    ): RecyclerView.Adapter<*> {
        return Adapter(fragment, audioList)
    }

}

