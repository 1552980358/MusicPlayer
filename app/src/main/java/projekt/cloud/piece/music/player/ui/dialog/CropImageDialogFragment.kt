package projekt.cloud.piece.music.player.ui.dialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import kotlinx.coroutines.withContext
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseDialogFragment
import projekt.cloud.piece.music.player.databinding.DialogFragmentCropImageBinding
import projekt.cloud.piece.music.player.util.CoroutineUtil.io
import projekt.cloud.piece.music.player.util.CoroutineUtil.ioContext
import projekt.cloud.piece.music.player.util.CoroutineUtil.ui

class CropImageDialogFragment: BaseDialogFragment() {

    private var _binding: DialogFragmentCropImageBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val materialToolbar get() = binding.materialToolbar
    private val cropImageView get() = binding.cropImageView

    private var uri: Uri? = null

    private var onCrop: ((Bitmap?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_CloudPiece_CropImageDialogFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFragmentCropImageBinding.inflate(inflater, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        materialToolbar.setNavigationOnClickListener { dismiss() }
        materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_crop -> {
                    onCrop?.invoke(cropImageView.crop)
                    dismiss()
                }
            }
            true
        }
        ui {
            ioContext {
                uri?.run {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    requireContext().contentResolver.openInputStream(this)
                        .use { BitmapFactory.decodeStream(it) }
                }
            }?.let { cropImageView.setBitmap(it) }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    fun setUri(uri: Uri) = apply {
        this.uri = uri
    }

    fun setOnCrop(onCrop: (Bitmap?) -> Unit) = apply {
        this.onCrop = onCrop
    }

}