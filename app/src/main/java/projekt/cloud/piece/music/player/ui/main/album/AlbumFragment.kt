package projekt.cloud.piece.music.player.ui.main.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.databinding.FragmentAlbumBinding
import projekt.cloud.piece.music.player.ui.main.album.util.RecyclerViewAdapterUtil

class AlbumFragment: BaseFragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root, activityViewModel.albumArtMap, activityViewModel.defaultCoverArt) {

        }

        io {
            val albumList = activityViewModel.database.album.query()
            ui { recyclerViewAdapterUtil.albumList = albumList }
        }
    }

}