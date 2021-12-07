package sakuraba.saki.player.music.ui.audioDetail.lyricView.dialogFragment

import android.app.Dialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputEditText
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.databinding.DialogFragmentLyricEditBinding
import sakuraba.saki.player.music.util.Lyric
import sakuraba.saki.player.music.util.LyricUtil.timeMin
import sakuraba.saki.player.music.util.LyricUtil.timeMs
import sakuraba.saki.player.music.util.LyricUtil.timeSec

class LyricEditDialogFragment(private val pos: Int,
                              private val lyric: Lyric,
                              private val fm: FragmentManager,
                              private val listener: (action: Int, pos: Int, time: Long?, lyric: String?) -> Unit
): DialogFragment() {

    companion object {
        val CREATE_LYRIC = -1
        private const val TAG = "LyricEditDialogFragment"

        const val NO_CHANGE = -1
        const val CREATE = 0
        const val MODIFY = 1
        const val REMOVE = 2
    }

    private var _dialogFragmentLyricEditBinding: DialogFragmentLyricEditBinding? = null
    private val dialogFragmentLyricEdit get() = _dialogFragmentLyricEditBinding!!
    private lateinit var editTextMin: TextInputEditText
    private lateinit var editTextSec: EditText
    private lateinit var editTextMs: EditText
    private lateinit var editTextLyric: EditText
    private lateinit var positiveButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialogFragmentLyricEditBinding = DialogFragmentLyricEditBinding.inflate(layoutInflater)
        editTextMin = dialogFragmentLyricEdit.textInputLyric.editText as TextInputEditText
        editTextSec = dialogFragmentLyricEdit.textInputSec.editText as TextInputEditText
        editTextMs = dialogFragmentLyricEdit.textInputMs.editText as TextInputEditText
        editTextLyric = dialogFragmentLyricEdit.textInputLyric.editText as TextInputEditText
        return AlertDialog.Builder(requireContext())
                .apply {
                    when (pos) {
                        CREATE_LYRIC -> {
                            setTitle(R.string.lyric_edit_dialog_add)
                            dialogFragmentLyricEdit.textInputMin.isEnabled = true
                            dialogFragmentLyricEdit.textInputSec.isEnabled = true
                            dialogFragmentLyricEdit.textInputMs.isEnabled = true
                            editTextMin.doAfterTextChanged { updatePositiveButton() }
                            editTextSec.doAfterTextChanged { updatePositiveButton() }
                            editTextMs.doAfterTextChanged { updatePositiveButton() }
                        }
                        else -> {
                            setTitle(R.string.lyric_edit_dialog_modify)
                            val time = lyric.timeList[pos]
                            editTextMin.setText(time.timeMin.toString())
                            editTextSec.setText(time.timeSec.toString())
                            editTextMs.setText(time.timeMs.toString())
                            editTextLyric.setText(lyric.lyricList[pos])
                            setNeutralButton(R.string.lyric_edit_dialog_remove) { _, _, ->
                                listener(REMOVE, pos, null, null)
                            }
                        }
                    }
                    editTextLyric.doAfterTextChanged { updatePositiveButton() }
                    setView(dialogFragmentLyricEdit.root)
                }.setPositiveButton(android.R.string.ok) { _, _ ->
                    val text = dialogFragmentLyricEdit.textInputLyric.editText?.text!!.toString()
                    listener(textState(text), pos, timeLong, text)
                }.setNegativeButton(android.R.string.cancel) { _, _ -> }
                .create()
    }

    override fun onStart() {
        super.onStart()
        positiveButton = (requireDialog() as AlertDialog).getButton(BUTTON_POSITIVE)
        if (pos == CREATE_LYRIC) {
            positiveButton.isEnabled = false
        }
    }

    private fun textState(text: String) = if (pos == CREATE_LYRIC) CREATE else if (lyric.lyricList[pos] == text) NO_CHANGE else MODIFY

    private val timeLong get() = (dialogFragmentLyricEdit.textInputMin.editText?.text!!.toString().toInt() * 60 +
                dialogFragmentLyricEdit.textInputSec.editText?.text!!.toString().toInt()) * 1000L +
                dialogFragmentLyricEdit.textInputMs.editText?.text!!.toString().toInt()

    private fun updatePositiveButton() {
        positiveButton.isEnabled =
                !editTextMin.text.isNullOrBlank() && !editTextSec.text.isNullOrBlank()
                        && !editTextMs.text.isNullOrBlank() && !editTextLyric.text.isNullOrBlank()
    }

    fun show() = show(fm, TAG)

}