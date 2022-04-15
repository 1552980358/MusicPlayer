package projekt.cloud.piece.music.player.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import projekt.cloud.piece.music.player.R

abstract class BaseAlertDialogFragment: BaseDialogFragment() {

    abstract fun createView(): View
    private val contentView: View get() = createView()

    abstract fun setTitle(): Int
    private val title get() = setTitle()

    private val positiveButtonText get() = setPositiveButtonText()
    open fun setPositiveButtonText() = android.R.string.ok

    private val negativeButtonText get() = setNegativeButtonText()
    open fun setNegativeButtonText() = android.R.string.cancel

    private val neutralButtonText get() = setNeutralButtonText()
    open fun setNeutralButtonText() = R.string.neutral

    abstract fun onPositiveClick(newValue: String?)
    abstract fun setPositiveClickValue(): String?
    private val positiveValue get() = setPositiveClickValue()

    open fun onNegativeClick(originValue: String?) = Unit

    protected var hasDefault = false
    open fun onDefaultClick() = Unit

    protected var initialValue: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setView(contentView)
        .setPositiveButton(positiveButtonText) { _, _ -> onPositiveClick(positiveValue) }
        .setNegativeButton(negativeButtonText) { _, _ -> onNegativeClick(initialValue) }
        .apply {
            if (hasDefault) {
                setNeutralButton(neutralButtonText) { _, _ -> onDefaultClick() }
            }
        }
        .create()

}