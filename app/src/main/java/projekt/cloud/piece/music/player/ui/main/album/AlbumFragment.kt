package projekt.cloud.piece.music.player.ui.main.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.ui.main.album.adapter.RecyclerViewAdapter
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class AlbumFragment: BaseMainFragment() {
    
    private var _recyclerView: RecyclerView? = null
    private val recyclerView get() = _recyclerView!!
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _recyclerView = RecyclerView(requireContext())
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            recyclerViewAdapter = RecyclerViewAdapter(this)
        }
        return recyclerView
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui {
            recyclerViewAdapter.albumList = withContext(CoroutineUtil.io) {
                audioRoom.albumDao.queryAll()
            }
        }
    }
    
    override val isRecyclerViewBottom get() =
        recyclerViewAdapter.albumList != null && _recyclerView?.canScrollVertically(1) == false
    
}