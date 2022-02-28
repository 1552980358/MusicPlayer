package sakuraba.saki.player.music.ui.playlist.util

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.view.createViewHolder
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.Playlist
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView, private val listener: (Int) -> Unit) {

    val playlistList = arrayListOf<Playlist>()
    val bitmapMap = mutableMapOf<String, Bitmap?>()

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val linearLayout = view
        val imageView: AppCompatImageView = view.findViewById(R.id.image_view)
        val textViewTitle: AppCompatTextView = view.findViewById(R.id.text_view_title)
        val textViewSize: AppCompatTextView = view.findViewById(R.id.text_view_size)
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder =
            parent.createViewHolder(R.layout.layout_playlist)

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            linearLayout.setOnClickListener { listener(position) }
            playlistList[position].apply {
                textViewTitle.text = title
                textViewSize.text = size.toString()
                bitmapMap[title]?.let { imageView.setImageBitmap(it) }
            }
        }

        override fun getItemCount() = playlistList.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyDataSetChanged() = adapter.notifyDataSetChanged()

}