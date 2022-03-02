package sakuraba.saki.player.music.ui.common.addPlaylist

import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import sakuraba.saki.player.music.databinding.DialogFragmentAddPlaylistBinding
import sakuraba.saki.player.music.R

import sakuraba.saki.player.music.util.Playlist

class AddPlaylistDialogFragment(private val listener: (Playlist) -> Unit): DialogFragment() {

    private var _fragmentDialogAddPlaylistBinding: DialogFragmentAddPlaylistBinding? = null
    private val layout get() = _fragmentDialogAddPlaylistBinding!!

    private val alertDialog get() = dialog as AlertDialog

    private val editTextTitle get() = layout.textInputTitle.editText!!
    private val editTextDescription get() = layout.textInputDescription.editText!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _fragmentDialogAddPlaylistBinding = DialogFragmentAddPlaylistBinding.inflate(layoutInflater)
        layout.textInputTitle.editText?.addTextChangedListener {
            alertDialog.getButton(BUTTON_POSITIVE)?.isEnabled = !it.isNullOrBlank()
        }
        ui { alertDialog.getButton(BUTTON_POSITIVE)?.isEnabled = false }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_playlist_title)
            .setPositiveButton(R.string.add_playlist_ok) { _, _ ->
                if (editTextTitle.text.isNullOrBlank()) {
                    return@setPositiveButton
                }
                listener(Playlist(editTextTitle.text.toString(), editTextDescription.text?.toString() ?: ""))
            }
            .setNegativeButton(R.string.add_playlist_cancel) { _, _ -> }
            .setView(layout.root)
            .create()
    }

}