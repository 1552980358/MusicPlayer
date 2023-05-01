package projekt.cloud.piece.music.player.ui.fragment.library.artist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import projekt.cloud.piece.music.player.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.music.player.databinding.ArtistLibraryRecyclerLayoutBinding
import projekt.cloud.piece.music.player.storage.runtime.databaseView.ArtistView
import projekt.cloud.piece.music.player.util.KotlinUtil.to
import projekt.cloud.piece.music.player.util.KotlinUtil.tryTo
import projekt.cloud.piece.music.player.util.TimeUtil.durationStr

class ArtistLibraryRecyclerAdapter(
    private val artistList: List<ArtistView>,
    private val fragment: Fragment,
    private val onItemClick: (String, Int, View) -> Unit
): BaseRecyclerViewAdapter() {

    private val recyclerViewPool = RecycledViewPool()

    private class ViewHolder(
        parent: ViewGroup,
        fragment: Fragment,
        recyclerViewPool: RecycledViewPool,
        onItemClick: (String, Int, View) -> Unit
    ): BaseBindingViewHolder<ArtistLibraryRecyclerLayoutBinding>(
        ArtistLibraryRecyclerLayoutBinding::inflate, parent
    ) {

        private val recyclerView: RecyclerView
            get() = binding.recyclerView

        init {
            binding.onRootClick = onItemClick
            recyclerView.layoutManager.tryTo<LinearLayoutManager>()?.let { linearLayoutManager ->
                linearLayoutManager.orientation = HORIZONTAL
                linearLayoutManager.recycleChildrenOnDetach = true
            }
            recyclerView.adapter = ArtistLibraryChildRecyclerAdapter(fragment)
            recyclerView.setRecycledViewPool(recyclerViewPool)
        }

        fun bindData(id: String, pos: Int, title: String, duration: Long) {
            binding.id = id
            binding.pos = pos
            binding.title = title
            binding.duration = duration.durationStr
        }

        fun updateList(albumList: List<String>) {
            recyclerView.adapter.tryTo<ArtistLibraryChildRecyclerAdapter>()
                ?.updateAlbumList(albumList)
        }

    }

    override fun onCreateViewHolder(
        layoutInflater: LayoutInflater, parent: ViewGroup
    ): BaseViewHolder = ViewHolder(parent, fragment, recyclerViewPool, onItemClick)

    override fun getItemCount() = artistList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        artistList[position].let { artist ->
            holder.to<ViewHolder>().let { viewHolder ->
                viewHolder.bindData(artist.id, position, artist.title, artist.duration)
                viewHolder.updateList(artist.albums)
            }
        }
    }

}