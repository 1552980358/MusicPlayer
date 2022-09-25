package projekt.cloud.piece.music.player.ui.main.album

import android.os.Bundle
import android.view.View
import projekt.cloud.piece.music.player.item.Album
import projekt.cloud.piece.music.player.room.AudioDatabase.Companion.audioDatabase
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class AlbumFragment: BaseMainFragment() {
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewAdapter = RecyclerViewAdapter(recyclerView) {}
        io {
            val albumList = audioDatabase.albumDao.query()
            ui { recyclerViewAdapter.albumList = albumList.toMutableList() as ArrayList<Album> }
        }
    }

}