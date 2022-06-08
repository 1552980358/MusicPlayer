package projekt.cloud.piece.music.player.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import projekt.cloud.piece.music.player.R
import projekt.cloud.piece.music.player.base.BaseBottomSheetDialogFragment
import projekt.cloud.piece.music.player.databinding.DialogFragmentWebServerBinding
import projekt.cloud.piece.music.player.util.ActivityUtil.heightPixels

class WebServerDialogFragment: BaseBottomSheetDialogFragment() {
    
    private var _binding: DialogFragmentWebServerBinding? = null
    private val binding get() = _binding!!
    private val root get() = binding.root
    private val materialToolbar get() = binding.materialToolbar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_CloudPiece_WebServerDialogFragment)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFragmentWebServerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pixelHeights = heightPixels
        root.updateLayoutParams {
            height = pixelHeights
        }
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            maxHeight = pixelHeights
            peekHeight = pixelHeights
            state = BottomSheetBehavior.STATE_EXPANDED
        }
        with(materialToolbar) {
            setOnMenuItemClickListener {
                dismissAllowingStateLoss()
                true
            }
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?) = super.onCreateDialog(savedInstanceState).apply {
        window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

}