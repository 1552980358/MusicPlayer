package sakuraba.saki.player.music.ui.album.albumList.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView.Adapter
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView, listener: (pos: Int) -> Unit) {
    
    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relative_layout)
        val textViewNumber: TextView = view.findViewById(R.id.text_view_num)
        val textViewTitle: TextView = view.findViewById(R.id.text_view_title)
        val textViewSummary: TextView = view.findViewById(R.id.text_view_summary)
    }
    
    private class RecyclerViewAdapter(val arrayList: ArrayList<AudioInfo>, private val listener: (pos: Int) -> Unit): Adapter<RecyclerViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_album_list_recycler_view, parent, false))
        
        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            relativeLayout.setOnClickListener { listener(position) }
            textViewTitle.text = arrayList[position].audioTitle
            @Suppress("SetTextI18n")
            textViewSummary.text = "${arrayList[position].audioArtist} - ${arrayList[position].audioAlbum}"
            textViewNumber.text = (position + 1).toString()
        }
    
        override fun getItemCount() = arrayList.size

    }
    
    private val recyclerViewAdapter = RecyclerViewAdapter(ArrayList(), listener)
    
    init {
        recyclerView.adapter = recyclerViewAdapter
    }
    
    val audioInfoList get() = recyclerViewAdapter.arrayList
    
    fun notifyDataUpdated() = recyclerViewAdapter.notifyDataSetChanged()
    
}