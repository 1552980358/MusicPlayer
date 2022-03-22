package projekt.cloud.piece.music.player.ui.main.playlist

import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeFileDescriptor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import lib.github1552980358.ktExtension.androidx.fragment.app.getDrawable
import lib.github1552980358.ktExtension.androidx.fragment.app.show
import lib.github1552980358.ktExtension.kotlinx.coroutines.io
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseFragment
import projekt.cloud.piece.music.player.database.base.BaseTitledItem
import projekt.cloud.piece.music.player.database.item.ColorItem
import projekt.cloud.piece.music.player.database.item.ColorItem.Companion.TYPE_PLAYLIST
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.FragmentPlaylistBinding
import projekt.cloud.piece.music.player.ui.audioList.AudioListFragment.Companion.EXTRA_TYPE_ARTIST
import projekt.cloud.piece.music.player.ui.main.MainFragment
import projekt.cloud.piece.music.player.ui.main.MainFragmentDirections
import projekt.cloud.piece.music.player.ui.main.MainViewModel
import projekt.cloud.piece.music.player.ui.main.playlist.dialogFragment.AddPlaylistDialogFragment
import projekt.cloud.piece.music.player.ui.main.playlist.util.RecyclerViewAdapterUtil
import projekt.cloud.piece.music.player.util.ImageUtil.cutAs40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.loadPlaylist40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writePlaylist40Dp
import projekt.cloud.piece.music.player.util.ImageUtil.writePlaylistRaw

class PlaylistFragment: BaseFragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil
    private lateinit var mainViewModel: MainViewModel

    private lateinit var navController: NavController

    private var addPlaylistDialogFragment: AddPlaylistDialogFragment? = null

    private val playlistArtMap = mutableMapOf<String, Bitmap>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
         _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(parentFragment as MainFragment)[MainViewModel::class.java]
        navController = findNavController()
        if (!mainViewModel.isPlaylistLoaded) {
            mainViewModel.defaultPlaylistArt = getDrawable(R.drawable.ic_playlist_default)!!.toBitmap()
        }
        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(binding.root, playlistArtMap, mainViewModel.defaultPlaylistArt) { rootView, playlistItem ->
            navigateToAudioList(rootView, playlistItem)
        }
        io {
            if (!mainViewModel.isPlaylistLoaded) {
                mainViewModel.playlistList = activityViewModel.database.playlist.query().toMutableList() as ArrayList<PlaylistItem>
            }
            requireContext().loadPlaylist40Dp(playlistArtMap)
            ui { recyclerViewAdapterUtil.playlistList = mainViewModel.playlistList }
            mainViewModel.isPlaylistLoaded = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_playlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_playlist -> {
                addPlaylistDialogFragment = AddPlaylistDialogFragment().apply {
                    setCallback { playlistItem, bitmap ->
                        io {
                            activityViewModel.database.playlist.insert(playlistItem)
                            when {
                                bitmap != null -> {
                                    requireContext().writePlaylistRaw(playlistItem.id, bitmap)
                                    requireContext().writePlaylist40Dp(playlistItem.id, bitmap.cutAs40Dp(requireContext()))
                                    MediaNotificationProcessor(requireContext(), bitmap).apply {
                                        activityViewModel.database.color.insert(
                                            ColorItem(playlistItem.id, TYPE_PLAYLIST, backgroundColor, primaryTextColor, secondaryTextColor)
                                        )
                                    }
                                    playlistArtMap[playlistItem.id] = bitmap
                                }
                                else -> activityViewModel.database.color.insert(ColorItem(playlistItem.id, TYPE_PLAYLIST))
                            }
                            mainViewModel.playlistList.add(playlistItem)
                            ui { recyclerViewAdapterUtil.playlistList = mainViewModel.playlistList }
                        }
                    }
                }
                addPlaylistDialogFragment?.show(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToAudioList(rootView: View, item: BaseTitledItem) {
        navController.navigate(
            MainFragmentDirections.actionNavMainToNavAudioList(item, EXTRA_TYPE_ARTIST),
            FragmentNavigatorExtras(rootView to rootView.transitionName)
        )
    }

}