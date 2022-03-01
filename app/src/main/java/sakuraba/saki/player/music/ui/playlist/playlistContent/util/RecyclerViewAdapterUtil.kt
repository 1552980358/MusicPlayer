package sakuraba.saki.player.music.ui.playlist.playlistContent.util

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.android.view.createViewHolder
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.MainActivityInterface
import sakuraba.saki.player.music.util.Playlist
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              playlist: Playlist,
                              private val activityInterface: MainActivityInterface,
                              private val listener: (Int) -> Unit) {

    private val audioInfoList = playlist.audioInfoList

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val relativeLayout = view as RelativeLayout
        val imageView: AppCompatImageView = view.findViewById(R.id.image_view)
        val textViewTitle: AppCompatTextView = view.findViewById(R.id.text_view_title)
        val textViewSubtitle: AppCompatTextView = view.findViewById(R.id.text_view_subtitle)
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            parent.createViewHolder<RecyclerViewHolder>(R.layout.layout_playlist_content)

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            relativeLayout.setOnClickListener { listener(position) }

            audioInfoList[position].apply {
                imageView.setImageBitmap(
                    activityInterface.audioBitmapMap[audioId]
                        ?: activityInterface.bitmapMap[audioAlbumId]
                        ?: ContextCompat.getDrawable(relativeLayout.context, R.drawable.ic_music)!!.toBitmap()
                )
                textViewTitle.text = audioTitle
                @Suppress("SetTextI18n")
                textViewSubtitle.text = "$audioArtist - $audioAlbum"
            }

        }

        override fun getItemCount() = audioInfoList.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyDataSetChanged() = adapter.notifyDataSetChanged()

}