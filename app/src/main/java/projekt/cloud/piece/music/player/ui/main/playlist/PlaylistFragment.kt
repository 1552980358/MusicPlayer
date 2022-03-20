package projekt.cloud.piece.music.player.ui.main.playlist

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import projekt.cloud.piece.music.player.databinding.FragmentPlaylistBinding
import projekt.cloud.piece.music.player.ui.audioList.AudioListFragment.Companion.EXTRA_TYPE_ARTIST
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainFragmentDirections
import projekt.cloud.piece.music.player.ui.main.MainViewModel
import projekt.cloud.piece.music.player.ui.main.playlist.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp

class PlaylistFragment: BaseFragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil
    private lateinit var mainViewModel: MainViewModel

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
         _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(parentFragment as MainFragment)[MainViewModel::class.java]
        navController = findNavController()
        if (!mainViewModel.isPlaylistArtLoaded) {
            mainViewModel.defaultPlaylistArt = getDrawable(R.drawable.ic_playlist_default)!!.toBitmap()
        }
        val artMap = mutableMapOf<String, Bitmap>()
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root, artMap, mainViewModel.defaultPlaylistArt) { rootView, playlistItem ->
            navigateToAudioList(rootView, playlistItem)
        }
        io {
            requireContext().loadPlaylist40Dp(artMap)
            recyclerViewAdapterUtil.playlistList = activityViewModel.database.playlist.query()
        }
    }

    private fun navigateToAudioList(rootView: View, item: BaseTitledItem) {
        navController.navigate(
            MainFragmentDirections.actionNavMainToNavAudioList(item, EXTRA_TYPE_ARTIST),
            FragmentNavigatorExtras(rootView to rootView.transitionName)
        )
    }

}