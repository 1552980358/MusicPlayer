package sakuraba.saki.player.music.ui.common.input

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Dialog
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogFragmentPlaylistDetailEditingBinding
import sakuraba.saki.player.music.util.BitmapUtil.cutAsCube
import sakuraba.saki.player.music.util.Playlist

class PlaylistDetailEditDialogFragment(private val playlist: Playlist,
                                       private val positiveListener: (Playlist, String, String?, Bitmap?) -> Unit): DialogFragment() {

    companion object {
        private const val MIME_TYPE_IMAGE = "image/*"
        private const val OPEN_IMAGE_MODE = "r"
    }

    private var _dialogFragmentPlaylistDetailEditing: DialogFragmentPlaylistDetailEditingBinding? = null
    private val layout get() = _dialogFragmentPlaylistDetailEditing!!

    private lateinit var alertDialog: AlertDialog

    private var bitmapImage: Bitmap? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogFragmentPlaylistDetailEditing = DialogFragmentPlaylistDetailEditingBinding.inflate(layoutInflater)

        layout.editTextTitle.setText(playlist.title)
        layout.editTextDescription.setText(playlist.description)

        val pickImage = registerForActivityResult(GetContent()) {
            bitmapImage = tryRun { requireContext().contentResolver.openFileDescriptor(it, OPEN_IMAGE_MODE)?.use { BitmapFactory.decodeFileDescriptor(it.fileDescriptor) } }?.cutAsCube
            layout.imageView.setImageBitmap(bitmapImage)
        }

        layout.relativeLayout.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                pickImage.launch(MIME_TYPE_IMAGE)
            }
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.playlist_menu_edit)
            .setView(layout.root)
            .setPositiveButton(R.string.text_input_positive) { _, _ ->
                positiveListener(playlist, layout.editTextTitle.text!!.toString(), layout.editTextDescription.text!!.toString(), bitmapImage)
            }
            .setNegativeButton(R.string.text_input_negative) { _, _ -> }
            .setNeutralButton(R.string.text_input_default) { _, _ -> }
            .create()

        return alertDialog
    }

}