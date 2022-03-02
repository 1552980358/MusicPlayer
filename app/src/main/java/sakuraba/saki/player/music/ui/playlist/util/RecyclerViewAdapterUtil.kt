package sakuraba.saki.player.music.ui.playlist.util

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.fragment.FragmentNavigator.Extras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.view.createViewHolder
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.MainActivityInterface
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView,
                              private val activityInterface: MainActivityInterface,
                              private val defaultBitmap: Bitmap,
                              private val listener: (Int, Extras) -> Unit) {

    val playlistList get() = activityInterface.playlistList
    val bitmapMap get() = activityInterface.playlistMap

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
            linearLayout.setOnClickListener {
                listener(position, FragmentNavigatorExtras(imageView to imageView.transitionName))
            }
            playlistList[position].apply {
                imageView.transitionName = titlePinyin
                textViewTitle.text = title
                textViewSize.text = size.toString()
                imageView.setImageBitmap(bitmapMap[titlePinyin] ?: defaultBitmap)
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