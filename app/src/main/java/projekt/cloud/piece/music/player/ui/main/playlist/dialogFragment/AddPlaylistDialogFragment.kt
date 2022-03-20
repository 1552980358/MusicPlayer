package projekt.cloud.piece.music.player.ui.main.playlist.dialogFragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.result.ActivityResultLauncher
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.database.item.PlaylistItem
import projekt.cloud.piece.music.player.databinding.DialogFragmentAddPlaylistBinding
import projekt.cloud.piece.music.player.util.ImageUtil.asSquare
import projekt.cloud.piece.music.player.util.ImageUtil.writePlaylistRaw

class AddPlaylistDialogFragment: DialogFragment() {

    private lateinit var callback: (PlaylistItem, Bitmap?) -> Unit
    private lateinit var pickImage: ActivityResultLauncher<String>

    private var _binding: DialogFragmentAddPlaylistBinding? = null
    private val binding get() = _binding!!
    private val toolbar get() = binding.toolbar
    private val imageView get() = binding.imageView
    private val relativeLayout get() = binding.relativeLayout
    private val editTextTitle get() = binding.editTextTitle
    private val editTextDescription get() = binding.editTextDescription

    private var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_MusicPlayer_FullscreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_add_playlist, container, false)
        with(imageView) {
            layoutParams = layoutParams.apply {
                height = resources.displayMetrics.widthPixels / 2
                width = height
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(toolbar) {
            setNavigationOnClickListener { dismiss() }
            setTitle(R.string.playlist_menu_add_playlist)
            menu.getItem(0).isEnabled = false
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_save -> {
                        val playlistItem = PlaylistItem(title = editTextTitle.text.toString(), description = editTextDescription.text?.toString())
                        callback(playlistItem, imageBitmap)
                        dismiss()
                    }
                }
                true
            }
        }
        editTextTitle.doAfterTextChanged { toolbar.menu.getItem(0).isEnabled = !it.isNullOrBlank() }
        relativeLayout.setOnClickListener { pickImage.launch("image/*") }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        window?.attributes?.windowAnimations = R.style.Theme_MusicPlayer_FullscreenDialog_WindowAnimation
    }

    fun setCallback(callback: (PlaylistItem, Bitmap?) -> Unit) {
        this.callback = callback
    }

    fun setPickImage(pickImage: ActivityResultLauncher<String>) {
        this.pickImage = pickImage
    }

    fun setImageBitmap(imageBitmap: Bitmap) {
        this.imageBitmap = imageBitmap.asSquare
        imageView.setImageBitmap(this.imageBitmap)
    }

}