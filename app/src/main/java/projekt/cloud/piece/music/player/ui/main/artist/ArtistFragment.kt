package projekt.cloud.piece.music.player.ui.main.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.music.player.databinding.FragmentArtistBinding
import projekt.cloud.piece.music.player.item.Artist
import projekt.cloud.piece.music.player.room.AudioDatabase.Companion.audioDatabase
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class ArtistFragment: BaseMainFragment() {
    
    private var _binding: FragmentArtistBinding? = null
    private val binding: FragmentArtistBinding
        get() = _binding!!
    
    private val root: CoordinatorLayout
        get() = binding.root
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        return root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapter = RecyclerViewAdapter(recyclerView) {}
        io {
            val artistList = audioDatabase.artistDao.query()
            ui { recyclerViewAdapter.artistList = artistList.toMutableList() as ArrayList<Artist> }
        }
    }

}