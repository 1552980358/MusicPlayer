package projekt.cloud.piece.music.player.ui.main.artist

import android.os.Bundle
import android.view.View
import projekt.cloud.piece.music.player.item.Artist
import projekt.cloud.piece.music.player.room.AudioDatabase.Companion.audioDatabase
import projekt.cloud.piece.music.player.ui.main.base.BaseMainRecyclerFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class ArtistFragment: BaseMainRecyclerFragment() {
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewAdapter = RecyclerViewAdapter(recyclerView) {}
        io {
            val artistList = audioDatabase.artistDao.query()
            ui { recyclerViewAdapter.artistList = artistList.toMutableList() as ArrayList<Artist> }
        }
    }

}