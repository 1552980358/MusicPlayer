package projekt.cloud.piece.music.player.base

import android.view.View
import android.view.View.GONE

abstract class BaseInputDialogFragment: BaseItemSelectDialogFragment() {
    
    override val contentView: View get() {
        super.contentView
        isTextInputEnabled = true
        textInputLayoutMenu.visibility = GONE
        return root
    }
    
    override val onPositiveClick: () -> Unit get() = {
        positiveClick.invoke(textInputEditText.text?.toString())
    }
    
}