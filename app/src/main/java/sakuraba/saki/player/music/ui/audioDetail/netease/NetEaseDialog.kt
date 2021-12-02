package sakuraba.saki.player.music.ui.audioDetail.netease

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceFragmentCompat
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.shortSnack
import lib.github1552980358.ktExtension.androidx.fragment.app.findActivityViewById
import sakuraba.saki.player.music.R
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.LyricUtil.writeLyric
import sakuraba.saki.player.music.util.NetEaseUtil.netEase
import sakuraba.saki.player.music.util.PreferenceUtil.preference

class NetEaseDialog(private val audioId: String, private val parentFragment: PreferenceFragmentCompat): DialogFragment() {

    private companion object {
        const val TAG = "NetEaseDialog"
    }

    private var _editText: EditText? = null
    private val editText get() = _editText!!

    private var coordinatorLayout: CoordinatorLayout? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        coordinatorLayout = findActivityViewById(R.id.coordinator_layout)
        return AlertDialog.Builder(requireContext())
                .apply {
                    setTitle(R.string.audio_detail_netease_dialog)
                    _editText = EditText(requireContext())
                    setView(editText)
                    setPositiveButton(R.string.audio_detail_netease_dialog_ok) { _, _ ->
                        io {
                            val text = editText.text
                            val lyric = text.toString().netEase
                            if (lyric.size == 0) {
                                ui {
                                    coordinatorLayout?.shortSnack(getString(R.string.audio_detail_netease_dialog_failed) + text)
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
                    setNegativeButton(R.string.audio_detail_netease_dialog_cancel) { _, _ -> }
                }.show()
    }

    fun show() = show(parentFragment.parentFragmentManager, TAG)

}