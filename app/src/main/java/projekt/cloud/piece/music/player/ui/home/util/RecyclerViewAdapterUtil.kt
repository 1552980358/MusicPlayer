package projekt.cloud.piece.music.player.ui.home.util

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.view.createViewHolder
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.util.ViewHolderUtil.bind

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val audioList: List<AudioItem>,
                              private val defaultBitmap: Bitmap,
                              private val audioBitmap: Map<String, Bitmap?>,
                              private val albumBitmapMap: Map<String, Bitmap?>,
                              private val rootClick: (Int) -> Unit,
                              private val optionClick: (Int, View) -> Unit) {

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val root = view
        val imageView: AppCompatImageView = view.findViewById(R.id.image_view)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relative_layout)
        val textViewTitle: AppCompatTextView = view.findViewById(R.id.text_view_title)
        val textViewSubtitle: AppCompatTextView = view.findViewById(R.id.text_view_subtitle)
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            parent.createViewHolder<RecyclerViewHolder>(R.layout.layout_home_recycler_view)

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bind {
            root.setOnClickListener { rootClick(position) }
            root.setOnLongClickListener {
                optionClick(position, relativeLayout)
                return@setOnLongClickListener true
            }
            relativeLayout.setOnClickListener { optionClick(position, relativeLayout) }
            audioList[position].apply {
                textViewTitle.text = title
                @Suppress("SetTextI18n")
                textViewSubtitle.text = "${artistItem.name} - ${albumItem.title}"
                imageView.setImageBitmap(audioBitmap[id] ?: albumBitmapMap[album] ?: defaultBitmap)
            }
            
        }

        override fun getItemCount() = audioList.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyUpdate() = adapter.notifyDataSetChanged()

}