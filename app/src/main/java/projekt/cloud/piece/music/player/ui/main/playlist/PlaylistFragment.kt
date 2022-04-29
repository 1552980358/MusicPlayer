package projekt.cloud.piece.music.player.ui.main.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.withContext
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.audio.extension.PlaylistWithAudio
import projekt.cloud.piece.music.player.database.audio.item.ColorItem
import projekt.cloud.piece.music.player.database.audio.item.PlaylistItem
import projekt.cloud.piece.music.player.ui.main.base.BaseMainFragment
import projekt.cloud.piece.music.player.ui.dialog.CreatePlaylistDialogFragment
import projekt.cloud.piece.music.player.ui.main.playlist.adapter.RecyclerViewAdapter
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui
import projekt.cloud.piece.music.player.util.DialogFragmentUtil.showNow
import projekt.cloud.piece.music.player.util.ImageUtil.savePlaylistArt

class PlaylistFragment: BaseMainFragment() {

    private var _recyclerView: RecyclerView? = null
    private val recyclerView get() = _recyclerView!!

    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewAdapter = RecyclerViewAdapter(recyclerView)
        setHasOptionsMenu(true)
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ui {
            recyclerViewAdapter.playlistList = withContext(io) {
                audioRoom.playlist
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_playlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_create -> CreatePlaylistDialogFragment()
                .setOnCreate { title, description, bitmap ->
                    val playlistItem = PlaylistItem(title, description)
                    io {
                        audioRoom.playlistDao.insert(playlistItem)
                        bitmap?.let {
                            requireContext().savePlaylistArt(playlistItem.id, it)
                            MediaNotificationProcessor(requireContext(), it).run {
                                audioRoom.colorDao.insert(
                                    ColorItem(backgroundColor, primaryTextColor, secondaryTextColor, playlist = playlistItem.id)
                                )
                            }
                        }
                        with(recyclerViewAdapter) {
                            playlistList?.add(PlaylistWithAudio(playlistItem))
                            ui { notifyUpdate() }
                        }
                    }
                }
                .showNow(this)
        }
        return super.onOptionsItemSelected(item)
    }

}