package projekt.cloud.piece.music.player.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog

abstract class BaseAlertDialogFragment: BaseDialogFragment() {
    
    protected abstract val dialogTitle: String
    protected abstract val contentView: View
    
    protected abstract val positiveText: String
    protected abstract val onPositiveClick: () -> Unit
    
    protected abstract val negativeText: String
    protected abstract val onNegativeClick: (() -> Unit)?
    
    protected abstract val neutralText: String?
    protected abstract val onNeutralClick: (() -> Unit)?
    
    protected var defaultValue: String? = null
    protected abstract val currentValue: String?
    
    protected abstract val hint: String
    protected var prefix: String? = null
    protected var suffix: String? = null
    
    protected lateinit var alertDialog: AlertDialog
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(dialogTitle)
            .setView(contentView)
            .apply {
                setPositiveButton(positiveText) { _, _ -> onPositiveClick.invoke() }
                setNegativeButton(negativeText) { _, _ -> onNegativeClick?.invoke() }
                neutralText?.let { neutralText ->
                    setNeutralButton(neutralText) { _, _ -> onNeutralClick?.invoke() }
                }
            }.create()
        return alertDialog
    }
    
}