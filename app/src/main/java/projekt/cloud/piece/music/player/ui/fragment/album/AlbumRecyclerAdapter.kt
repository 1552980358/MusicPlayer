package projekt.cloud.piece.music.player.ui.fragment.album

import android.view.LayoutInflater
import android.view.ViewGroup
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.music.player.databinding.AlbumRecyclerLayoutBinding
import projekt.cloud.piece.music.player.storage.runtime.entity.AudioMetadataEntity
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr

class AlbumRecyclerAdapter(
    private val audioList: List<AudioMetadataEntity>,
    private val onItemClick: (String) -> Unit
): BaseRecyclerViewAdapter() {

    private inner class ViewHolder(
        parent: ViewGroup
    ): BaseBindingViewHolder<AlbumRecyclerLayoutBinding>(
        AlbumRecyclerLayoutBinding::inflate, parent
    ) {

        init {
            binding.onItemClick = onItemClick
        }

        fun bind(pos: String, audioMetadata: AudioMetadataEntity) {
            binding.id = audioMetadata.id
            binding.pos = pos
            binding.title = audioMetadata.title
            binding.artist = audioMetadata.artistName
            binding.durationStr = audioMetadata.duration.durationStr
        }

    }

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.tryTo<ViewHolder>()
            ?.bind(position.inc().toString(), audioList[position])
    }

    override fun getItemCount() = audioList.size

}