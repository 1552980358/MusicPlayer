package sakuraba.saki.player.music.ui.audioDetail.base

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceFragmentCompat
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.shortSnack
import lib.github1552980358.ktExtension.androidx.fragment.app.findActivityViewById
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.CoroutineUtil
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.Lyric
import sakuraba.saki.player.music.util.LyricUtil.writeLyric
import sakuraba.saki.player.music.util.PreferenceUtil.preference

abstract class BaseLyricFetchDialogFragment(private val audioId: String, private val parentFragment: PreferenceFragmentCompat): DialogFragment() {

    @StringRes
    abstract fun getDialogTitle(): Int

    @StringRes
    abstract fun failedMessage(): Int

    abstract fun getLyric(text: String): Lyric

    abstract fun getLaunchTag(): String

    private var _editText: EditText? = null
    private val editText get() = _editText!!

    private var coordinatorLayout: CoordinatorLayout? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coordinatorLayout = findActivityViewById(R.id.coordinator_layout)
        return AlertDialog.Builder(requireContext())
                .apply {
                    setTitle(getDialogTitle())
                    _editText = EditText(requireContext())
                    setView(editText)
                    setPositiveButton(R.string.audio_detail_lyric_fetch_dialog_ok) { _, _ ->
                        CoroutineUtil.io {
                            val text = editText.text.toString()
                            val lyric = getLyric(text)
                            if (lyric.size == 0) {
                                ui {
                                    coordinatorLayout?.shortSnack(getString(failedMessage()) + text)
                                }
                                return@io
                            }
                            ui {
                                coordinatorLayout?.shortSnack(R.string.audio_detail_lyric_import_succeed)
                                parentFragment.preference(R.string.audio_detail_lyric_view_key)?.isEnabled = true
                                parentFragment.preference(R.string.audio_detail_lyric_remove_key)?.isEnabled = true
                            }
                            parentFragment.requireContext().writeLyric(audioId, lyric.lyricList, lyric.timeList)
                        }
                    }
                    setNegativeButton(R.string.audio_detail_lyric_fetch_dialog_cancel) { _, _ -> }
                }.show()
    }

    fun show() = show(parentFragment.parentFragmentManager, getLaunchTag())

}