package sakuraba.saki.player.music.ui.setting.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import lib.github1552980358.ktExtension.android.content.commit
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogTextInputBinding
import sakuraba.saki.player.music.util.SettingUtil.defaultSharedPreference

class TextInputDialogFragment(@StringRes private val title: Int, private val key: String, defaultValue: Any? = null): DialogFragment() {
    
    private companion object {
        const val TAG = "TextInputDialogFragment"
    }
    
    private var _dialogTextInputBinding: DialogTextInputBinding? = null
    private val dialogTextInput get() = _dialogTextInputBinding!!
    
    private val defaultValueStr = defaultValue?.toString()
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogTextInputBinding = DialogTextInputBinding.inflate(layoutInflater)
        dialogTextInput.editText.setText(defaultSharedPreference.getString(key, null) ?: defaultValueStr)
        dialogTextInput.editText.addTextChangedListener {
            if (it.isNullOrEmpty() && defaultValueStr != null) {
                dialogTextInput.editText.setText(defaultValueStr)
            }
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogTextInput.root)
            .setPositiveButton(R.string.dialog_okay) { _, _ ->
                val text = dialogTextInput.editText.text
                if (!text.isNullOrEmpty()) {
                    defaultSharedPreference.commit(key, text.toString())
                }
            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ -> }
            .setNeutralButton(R.string.dialog_default) { _, _ ->
                if (defaultValueStr != null) {
                    defaultSharedPreference.commit(key, defaultValueStr)
                }
            }
            .create()
    }
    
    fun show(fragmentManager: FragmentManager) = show(fragmentManager, TAG)
    
}