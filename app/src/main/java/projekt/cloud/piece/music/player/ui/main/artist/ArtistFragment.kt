package projekt.cloud.piece.music.player.ui.main.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.ui.main.artist.adapter.RecyclerViewAdapter
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

/**
 * [ArtistFragment]
 * inherit to [BaseMainFragment]
 *
 * Variables:
 * [recyclerView]
 * [recyclerViewAdapter]
 *
 * Methods:
 * [onCreateView]
 * [onViewCreated]
 * [isRecyclerViewBottom]
 **/
class ArtistFragment: BaseMainFragment() {
    
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
            recyclerViewAdapter.artistList = withContext(io) {
                audioRoom.artistDao.queryAll()
            }
        }
    }
    
    override val isRecyclerViewBottom get() =
        recyclerViewAdapter.artistList != null && _recyclerView?.canScrollVertically(1) == false

}