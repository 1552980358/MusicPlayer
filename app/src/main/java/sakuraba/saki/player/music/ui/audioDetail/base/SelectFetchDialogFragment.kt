package sakuraba.saki.player.music.ui.audioDetail.base

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogFragmentSelectFetchBinding

class SelectFetchDialogFragment(private val fm: FragmentManager, private val listener: (Int, String?) -> Unit): DialogFragment() {

    private var _dialogFragmentSelectFetchBinding: DialogFragmentSelectFetchBinding? = null
    private val dialogFragmentSelectFetch get() = _dialogFragmentSelectFetchBinding!!

    companion object {
        const val STORAGE = 0
        const val NET_EASE_MUSIC = 1
        const val QQ_MUSIC = 1

        private const val TAG = "SelectFetchDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogFragmentSelectFetchBinding = DialogFragmentSelectFetchBinding.inflate(layoutInflater)
        dialogFragmentSelectFetch.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            dialogFragmentSelectFetch.editTextLayout.isEnabled = when (checkedId) {
                R.id.radio_button_netease, R.id.radio_button_qq_music -> true
                else -> false
            }
        }
        return AlertDialog.Builder(requireContext())
                .setView(dialogFragmentSelectFetch.root)
                .setTitle(R.string.audio_detail_select_fetch_title)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val selection = when (dialogFragmentSelectFetch.radioGroup.checkedRadioButtonId) {
                        R.id.radio_button_netease -> NET_EASE_MUSIC
                        R.id.radio_button_qq_music -> QQ_MUSIC
                        else -> STORAGE
                    }
                    listener(selection, dialogFragmentSelectFetch.editTextLayout.editText?.text?.toString())
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
    }

    fun show() = show(fm, TAG)

}