package sakuraba.saki.player.music.ui.setting.fragment.util

import android.media.MediaCodecInfo
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.view.inflate
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(private val codecInfoArray: Array<MediaCodecInfo>, recyclerView: RecyclerView, private val listener: (Int) -> Unit) {

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val linearLayout: LinearLayoutCompat = view.findViewById(R.id.linear_layout)
        val textView: AppCompatTextView = view.findViewById(R.id.text_view)
    }

    private inner class RecyclerViewAdapter(): Adapter<RecyclerViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            // parent.createViewHolder<RecyclerViewHolder>(R.layout.layout_codec_recycler_view)
            RecyclerViewHolder(parent.inflate(R.layout.layout_codec_recycler_view))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            textView.text = codecInfoArray[position].name
            linearLayout.setOnClickListener { listener(position) }
        }

        override fun getItemCount() = codecInfoArray.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

}