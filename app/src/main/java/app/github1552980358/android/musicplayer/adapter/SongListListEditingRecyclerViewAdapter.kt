package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.AudioData
import lib.github1552980358.ktExtension.android.java.moveAndShift

/**
 * [SongListListEditingRecyclerViewAdapter]
 * @author  : 1552980328
 * @since   :
 * @date    : 2020/6/7
 * @time    : 17:14
 **/

class SongListListEditingRecyclerViewAdapter:
    Adapter<SongListListEditingRecyclerViewAdapter.ViewHolder>() {
    
    private var list = arrayListOf<AudioData>()
    private var isRandom = false
    
    init {
    
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (parent.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_song_list_list_editing, parent, false)
        )
    }
    
    override fun getItemCount(): Int {
        return list.size
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text = list[position].title
        holder.textViewSubtitle.text = list[position].artist
    }
    
    fun setList(arrayList: ArrayList<AudioData>, isRandom: Boolean) {
        this.list = arrayList
        this.isRandom = isRandom
    }
    
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewSubtitle: TextView = view.findViewById(R.id.textViewSubtitle)
    }
    
    class Callback(): ItemTouchHelper.Callback() {
        
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }
        
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            (recyclerView.adapter as SongListListEditingRecyclerViewAdapter?)
                ?.apply {
                    list.moveAndShift(viewHolder.adapterPosition, target.adapterPosition)
                    notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                }
            return true
        }
        
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    }
    
}