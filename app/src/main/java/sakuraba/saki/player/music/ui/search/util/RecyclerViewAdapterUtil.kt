package sakuraba.saki.player.music.ui.search.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView, private val listener: (audioInfo: AudioInfo) -> Unit) {

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relative_layout)
        val textViewNum: TextView = view.findViewById(R.id.text_view_num)
        val textViewTitle: TextView = view.findViewById(R.id.text_view_title)
        val textViewSummary: TextView = view.findViewById(R.id.text_view_summary)
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        val list = arrayListOf<AudioInfo>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_search_recycler_view, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            relativeLayout.setOnClickListener { listener(list[position]) }
            textViewNum.text = position.plus(1).toString()
            textViewTitle.text = list[position].audioTitle
            @Suppress("SetTextI18n")
            textViewSummary.text = "${list[position].audioArtist} - ${list[position].audioAlbum}"
        }

        override fun getItemCount() = list.size

    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

    val audioInfoList get() = adapter.list

    @Suppress("NotifyDataSetChanged")
    fun notifyDataSetChanged() = adapter.notifyDataSetChanged()

}