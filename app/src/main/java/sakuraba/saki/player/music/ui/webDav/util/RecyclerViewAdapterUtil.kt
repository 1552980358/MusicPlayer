package sakuraba.saki.player.music.ui.webDav.util

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import lib.github1552980358.ktExtension.android.view.createViewHolder
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.ViewHolderUtil.bindHolder

class RecyclerViewAdapterUtil(recyclerView: RecyclerView, private val listener: (WebDavData) -> Unit) {

    val webDavDataList = ArrayList<WebDavData>()

    private class RecyclerViewHolder(view: View): ViewHolder(view) {
        val relativeLayout: RelativeLayout = view.findViewById(R.id.relative_layout)
        val textViewName: AppCompatTextView = view.findViewById(R.id.text_view_name)
        val textViewNo: AppCompatTextView = view.findViewById(R.id.text_view_no)
        val textViewUsernameUrl: AppCompatTextView = view.findViewById(R.id.text_view_username_url)
    }

    private inner class RecyclerAdapter: Adapter<RecyclerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            parent.createViewHolder<RecyclerViewHolder>(R.layout.layout_web_dav)

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) = holder.bindHolder {
            val webDavData = webDavDataList[position]
            textViewNo.text = position.plus(1).toString()
            textViewName.text = webDavData.name
            @Suppress("SetTextI18n")
            textViewUsernameUrl.text = "${webDavData.username} - ${webDavData.url}"
            relativeLayout.setOnClickListener { listener(webDavData) }
        }

        override fun getItemCount() = webDavDataList.size

    }

    private val adapter = RecyclerAdapter()

    init {
        recyclerView.adapter = adapter
    }

    @Suppress("NotifyDataSetChanged")
    fun notifyUpdate() = adapter.notifyDataSetChanged()

}