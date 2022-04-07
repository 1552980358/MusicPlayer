package projekt.cloud.piece.music.player.ui.dialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeFileDescriptor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import lib.github1552980358.ktExtension.kotlinx.coroutines.ui
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseDialogFragment
import projekt.cloud.piece.music.player.database.item.AudioItem
import projekt.cloud.piece.music.player.databinding.DialogFragmentCropImageBinding

class CropImageDialogFragment: BaseDialogFragment() {

    companion object {
        private const val IMAGE_READ_MODE = "r"
    }

    private var _binding: DialogFragmentCropImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageUri: Uri
    private lateinit var callback: (Bitmap) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_MusicPlayer_CropImageDialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        window?.attributes?.windowAnimations = R.style.Theme_MusicPlayer_AudioListDialogFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_fragment_crop_image, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_save -> {
                    binding.cropImageView.crop?.let { callback(it) }
                    dismiss()
                }
            }
            true
        }
        ui {
            val bitmap = withContext(IO) {
                @Suppress("BlockingMethodInNonBlockingContext")
                requireContext().contentResolver.openFileDescriptor(imageUri, IMAGE_READ_MODE)?.use {
                    decodeFileDescriptor(it.fileDescriptor)
                }
            }
            when (bitmap) {
                null -> dismiss()
                else -> binding.cropImageView.setBitmap(bitmap)
            }
        }
    }

    fun setArgs(imageUri: Uri, callback: (Bitmap) -> Unit) = apply {
        this.imageUri = imageUri
        this.callback = callback
    }

}