package sakuraba.saki.player.music.ui.webDav.webDavDirectory.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView, private val listener: (Int, DirectoryItem) -> Unit) {

    private lateinit var list: List<DirectoryItem>

    private inner class RecyclerViewHolder(view: View): ViewHolder(view) {
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relative_layout)
        val textView: AppCompatTextView = view.findViewById(R.id.text_view)
        val imageView: ImageView = view.findViewById(R.id.image_view)
    }

    private inner class RecyclerViewAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_web_dav_directory_item, parent, false))

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            val item = list[position]
            relativeLayout.setOnClickListener { listener(position, item) }
            textView.text = item.name
            imageView.setImageResource(
                if (item.isDirectory) R.drawable.ic_folder else if (item.isAudioFile) R.drawable.ic_audio_file else R.drawable.ic_file
            )
        }

        override fun getItemCount() = if (::list.isInitialized) list.size else 0
    }

    private val adapter = RecyclerViewAdapter()

    init {
        recyclerView.adapter = adapter
    }

    fun updateList(list: List<DirectoryItem>) {
        this.list = list
        notifyUpdate()
    }

    fun notifyUpdate() {
        @Suppress("NotifyDataSetChanged")
        adapter.notifyDataSetChanged()
    }

}