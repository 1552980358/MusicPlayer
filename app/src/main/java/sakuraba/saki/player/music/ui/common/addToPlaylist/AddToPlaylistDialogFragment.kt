package sakuraba.saki.player.music.ui.common.addToPlaylist

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogFragmentAddToPlaylistBinding
import sakuraba.saki.player.music.ui.common.addToPlaylist.util.RecyclerViewAdapterUtil
import sakuraba.saki.player.music.util.Playlist

class AddToPlaylistDialogFragment(private val playlistList: List<Playlist>,
                                  private val bitmapMap: Map<String, Bitmap?>,
                                  private val selection: (Playlist) -> Unit): DialogFragment() {

    private var _dialogFragmentAddToPlaylistBinding: DialogFragmentAddToPlaylistBinding? = null
    private val layout get() = _dialogFragmentAddToPlaylistBinding!!

    private lateinit var recyclerViewAdapterUtil: RecyclerViewAdapterUtil

    private lateinit var alertDialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogFragmentAddToPlaylistBinding = DialogFragmentAddToPlaylistBinding.inflate(layoutInflater)

        layout.root.apply {
            isEnabled = false
            isRefreshing = true

            ui { isRefreshing = false }
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_to_playlist_title)
            .setView(layout.root)
            .setNegativeButton(R.string.add_to_playlist_cancel) { _, _ -> }
            .create()

        recyclerViewAdapterUtil = RecyclerViewAdapterUtil(layout.recyclerView, playlistList, bitmapMap, selection, alertDialog)

        return alertDialog
    }

}