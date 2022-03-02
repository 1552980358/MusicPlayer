package sakuraba.saki.player.music.ui.common.addToPlaylist.util

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.view.createViewHolder
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.Playlist
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val playlistList: List<Playlist>,
                              private val bitmapMap: Map<String, Bitmap?>,
                              private val selection: (Playlist) -> Unit,
                              private val alertDialog: AlertDialog,
                              private val playlistImage: Bitmap) {

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val relativeLayout = view as RelativeLayout
        val imageView: ImageView = view.findViewById(R.id.image_view)
        val textViewTitle: AppCompatTextView = view.findViewById(R.id.text_view_title)
        val textViewSubtitle: AppCompatTextView = view.findViewById(R.id.text_view_subtitle)
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            parent.createViewHolder<RecyclerViewHolder>(R.layout.layout_add_to_playlist)

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            playlistList[position].apply {
                textViewTitle.text = title
                textViewSubtitle.text = size.toString()
                imageView.setImageBitmap(bitmapMap[titlePinyin] ?: playlistImage)
                relativeLayout.setOnClickListener {
                    selection(this)
                    alertDialog.dismiss()
                }
            }
        }

        override fun getItemCount() = playlistList.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

}