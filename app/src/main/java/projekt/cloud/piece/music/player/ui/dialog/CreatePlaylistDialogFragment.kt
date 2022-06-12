package projekt.cloud.piece.music.player.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseActivity
import projekt.cloud.piece.music.player.base.BaseBottomSheetDialogFragment
import projekt.cloud.piece.music.player.databinding.DialogFragmentCreatePlaylistBinding
import projekt.cloud.piece.music.player.util.ActivityUtil.heightPixels
import projekt.cloud.piece.music.player.util.DialogFragmentUtil.showNow

/**
 * [CreatePlaylistDialogFragment]
 * inherit to [BaseBottomSheetDialogFragment]
 *
 * Variable:
 * [binding]
 * [menuItemSave]
 * [bitmap]
 * [onCreate]
 *
 * Methods:
 * [root]
 * [materialToolbar]
 * [textInputEditTextTitle]
 * [textInputEditTextDescription]
 * [relativeLayoutSelectImage]
 * [appCompatImageView]
 * [onCreate]
 * [onCreateView]
 * [onViewCreated]
 * [onCreateDialog]
 * [setOnCreate]
 *
 **/
class CreatePlaylistDialogFragment: BaseBottomSheetDialogFragment() {

    companion object {
        private val MIME_IMAGE = "image/*"
    }

    private var _binding: DialogFragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val materialToolbar get() = binding.materialToolbar
    private val textInputEditTextTitle get() = binding.textInputEditTextTitle
    private val textInputEditTextDescription get() = binding.textInputEditTextDescription
    private val relativeLayoutSelectImage get() = binding.relativeLayoutSelectImage
    private val appCompatImageView get() = binding.appCompatImageView

    private var menuItemSave: MenuItem? = null
    private var bitmap: Bitmap? = null

    private var onCreate: ((String, String?, Bitmap?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_CloudPiece_CreatePlaylistDialogFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pixelHeights = heightPixels
        root.updateLayoutParams {
            height = pixelHeights
        }

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            maxHeight = pixelHeights
            peekHeight = pixelHeights
            state = STATE_EXPANDED
        }

        with(materialToolbar) {
            menuItemSave = menu.getItem(0)
            menuItemSave?.isEnabled = false

            setNavigationOnClickListener { dismissAllowingStateLoss() }

            setOnMenuItemClickListener {
                if (it.itemId == R.id.menu_create) {
                    onCreate?.invoke(
                        textInputEditTextTitle.text!!.toString(),
                        textInputEditTextDescription.text?.toString(),
                        bitmap
                    )
                    dismissAllowingStateLoss()
                }
                true
            }
        }

        textInputEditTextTitle.addTextChangedListener {
            menuItemSave?.isEnabled = !it.isNullOrBlank()
        }

        relativeLayoutSelectImage.setOnClickListener {
           (requireActivity() as? BaseActivity)?.getContent(MIME_IMAGE) { dataUri ->
               dataUri?.let {
                   CropImageDialogFragment()
                       .setUri(it)
                       .setOnCrop { croppedBitmap ->
                           bitmap = croppedBitmap
                           appCompatImageView.setImageBitmap(croppedBitmap)
                       }
                       .showNow(this)
               }
           }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    fun setOnCreate(onCreate: (String, String?, Bitmap?) -> Unit) = apply {
        this.onCreate = onCreate
    }

}