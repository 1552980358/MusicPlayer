package projekt.cloud.piece.music.player.ui.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseBottomSheetDialogFragment
import projekt.cloud.piece.music.player.databinding.DialogFragmentCreatePlaylistBinding
import projekt.cloud.piece.music.player.util.ActivityUtil.heightPixels

class CreatePlaylistDialogFragment: BaseBottomSheetDialogFragment() {

    private var _binding: DialogFragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val materialToolbar get() = binding.materialToolbar
    private val textInputEditTextTitle get() = binding.textInputEditTextTitle
    private val textInputEditTextDescription get() = binding.textInputEditTextDescription

    private var menuItemSave: MenuItem? = null

    private var onCreate: ((String, String?) -> Unit)? = null

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
                        textInputEditTextDescription.text?.toString()
                    )
                    dismissAllowingStateLoss()
                }
                true
            }
        }

        textInputEditTextTitle.addTextChangedListener {
            menuItemSave?.isEnabled = !it.isNullOrBlank()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.dialog_fragment_create_playlist, menu)
        menuItemSave = menu.findItem(0)
        menuItemSave?.isEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_create -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    fun setOnCreate(onCreate: (String, String?) -> Unit) = apply {
        this.onCreate = onCreate
    }

}