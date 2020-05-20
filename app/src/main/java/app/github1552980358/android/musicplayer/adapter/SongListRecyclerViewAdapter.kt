package app.github1552980358.android.musicplayer.adapter

import android.app.Service
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.SongList.Companion.SongListInfo

/**
 * [SongListRecyclerViewAdapter]
 * @author  : 1552980328
 * @since   :
 * @date    : 2020/5/20
 * @time    : 11:01
 **/

class SongListRecyclerViewAdapter(arrayList: ArrayList<SongListInfo>):
    RecyclerView.Adapter<SongListRecyclerViewAdapter.ViewHolder>() {

    /**
     *
     **/
    private var data = arrayList

    /**
     *
     **/
    fun updateList(arrayList: ArrayList<SongListInfo>) {
        data = arrayList
        notifyDataSetChanged()
    }

    /**
     *
     **/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            (parent.context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.view_song_list, parent, false)
        )
    }

    /**
     *
     **/
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     *
     **/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.text = data[position].listName
        holder.textViewTitle.ellipsize = TextUtils.TruncateAt.END
        holder.textViewSubtitle.text = data[position].listSize.toString()
    }

    /**
     *
     **/
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)!!
        val textViewSubtitle = view.findViewById<TextView>(R.id.textViewSubtitle)!!
    }

}