package sakuraba.saki.player.music.ui.webDav.addHost

import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogFragmentAddHostBinding
import sakuraba.saki.player.music.ui.webDav.util.WebDavData

class AddHostDialogFragment(private val data: WebDavData? = null, private val listener: (String?, String, String, String) -> Unit): DialogFragment() {

    private companion object {
        const val TAG = "AddHostDialogFragment"
        const val EMPTY_STR = ""
    }

    private var _dialogFragmentAddHostBinding: DialogFragmentAddHostBinding? = null
    private val layout get() = _dialogFragmentAddHostBinding!!

    private val textInputName get() = layout.textInputName.editText!!
    private val textInputUrl get() = layout.textInputUrl.editText!!
    private val textInputUsername get() = layout.textInputUsername.editText!!
    private val textInputPassword get() = layout.textInputPassword.editText!!

    private val alertDialog get() = dialog as AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogFragmentAddHostBinding = DialogFragmentAddHostBinding.inflate(layoutInflater)
        textInputName.doAfterTextChanged { alertDialog.getButton(BUTTON_POSITIVE).isEnabled = checkIsPass }
        textInputUrl.doAfterTextChanged { alertDialog.getButton(BUTTON_POSITIVE).isEnabled = checkIsPass }
        textInputUsername.doAfterTextChanged { alertDialog.getButton(BUTTON_POSITIVE).isEnabled = checkIsPass }
        textInputPassword.doAfterTextChanged { alertDialog.getButton(BUTTON_POSITIVE).isEnabled = checkIsPass }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.web_dav_add)
            .setView(layout.root)
            .setPositiveButton(R.string.web_dav_dialog_add) { _, _ ->
                listener(textInputName.text.toString(), textInputUrl.text.toString(), textInputUsername.text.toString(), textInputPassword.text.toString())
            }.setNegativeButton(R.string.web_dav_dialog_cancel) { _, _ -> }
            .setNeutralButton(R.string.web_dav_dialog_remove) { _, _ ->
                listener(null, EMPTY_STR, EMPTY_STR, EMPTY_STR)
            }.create()
    }

    override fun onStart() {
        super.onStart()
        when (data) {
            null -> alertDialog.getButton(BUTTON_POSITIVE).isEnabled = false
            else -> {
                textInputName.setText(data.name)
                layout.textInputName.isEnabled = false
                textInputUrl.setText(data.url)
                textInputUsername.setText(data.username)
                textInputPassword.setText(data.password)
            }
        }
    }

    private val checkIsPass: Boolean get() =
        !textInputName.text.isNullOrBlank()
            && !textInputUrl.text.isNullOrBlank()
            && !textInputUsername.text.isNullOrBlank()
            && !textInputPassword.text.isNullOrBlank()

    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)

}