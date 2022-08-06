package projekt.cloud.piece.music.player.ui.main.home

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.databinding.LayoutRecyclerHomeBinding
import projekt.cloud.piece.music.player.item.AudioMetadata
import projekt.cloud.piece.music.player.util.ArtUtil.SUFFIX_SMALL
import projekt.cloud.piece.music.player.util.ArtUtil.TYPE_ALBUM
import projekt.cloud.piece.music.player.util.ArtUtil.fileOf
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class RecyclerViewAdapter(recyclerView: RecyclerView, private val onClick: (AudioMetadata, ArrayList<AudioMetadata>) -> Unit) {
    
    private inner class RecyclerViewHolder(private val layoutRecyclerHomeBinding: LayoutRecyclerHomeBinding)
        : RecyclerView.ViewHolder(layoutRecyclerHomeBinding.root), View.OnClickListener {
        
        private var job: Job? = null
        
        fun setAudioMetadata(audioMetadata: AudioMetadata) {
            job?.cancel()
            job = io {
                val artFile = layoutRecyclerHomeBinding.root.context.fileOf(TYPE_ALBUM, audioMetadata.album.id, SUFFIX_SMALL)
                when {
                    artFile.exists() -> artFile.inputStream().use {  BitmapFactory.decodeStream(it) }.let { bitmap ->
                        ui { layoutRecyclerHomeBinding.appCompatImageView.setImageBitmap(bitmap) }
                    }
                    else -> ui { layoutRecyclerHomeBinding.appCompatImageView.setImageResource(R.drawable.ic_round_music_note_24) }
                }
                job = null
            }
            layoutRecyclerHomeBinding.audioMetadata = audioMetadata
            layoutRecyclerHomeBinding.root.setOnClickListener(this)
        }
    
        override fun onClick(v: View?) {
            onClick.invoke(layoutRecyclerHomeBinding.audioMetadata!!, audioMetadataList!!.toMutableList() as ArrayList)
        }
        
    }
    
    private inner class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewHolder>() {
    
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(
            LayoutRecyclerHomeBinding.inflate(LayoutInflater.from(parent.context))
        )
    
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            audioMetadataList?.get(position)?.let { holder.setAudioMetadata(it) }
        }
    
        override fun getItemCount() = audioMetadataList?.size ?: 0
        
    }
    
    private val adapter = RecyclerViewAdapter()
    var audioMetadataList: List<AudioMetadata>? = null
        set(value) {
            field = value
            @Suppress("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }
    
    init {
        recyclerView.adapter = adapter
    }
    
}