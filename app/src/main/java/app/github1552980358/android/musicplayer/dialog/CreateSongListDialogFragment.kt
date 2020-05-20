package app.github1552980358.android.musicplayer.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListFile
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.base.SongList.Companion.SongListInfo
import app.github1552980358.android.musicplayer.base.SongList.Companion.songListInfoList
import app.github1552980358.android.musicplayer.fragment.mainActivity.MainFragment
import kotlinx.android.synthetic.main.dialog_create_song_list.*
import java.io.File
import java.io.ObjectOutputStream

/**
 * [CreateSongListDialogFragment]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/20
 * @time    : 9:13
 **/

class CreateSongListDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "AddSongListDialogFragment"
    }

    var contentText = "" as String?

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.createSongListDialog_title)
            .setView(R.layout.dialog_create_song_list)
            .setPositiveButton(R.string.createSongListDialog_submit) { _, _ ->
                songListInfoList.add(
                    SongListInfo().apply { listName = contentText?:throw Exception() }
                )
                File(requireContext().getExternalFilesDir(AudioDataDir), SongListFile).apply {
                    if (exists())
                        delete()
                    createNewFile()

                    outputStream().use { os ->
                        ObjectOutputStream(os).use { oos ->
                            oos.writeObject(songListInfoList)
                            oos.flush()
                        }
                        os.flush()
                    }
                }
                File(requireContext().getExternalFilesDir(SongListDir), contentText?:throw Exception()).apply {
                    if (parentFile!!.exists()) {
                        parentFile!!.mkdirs()
                    }

                    createNewFile()

                    outputStream().use { os ->
                        ObjectOutputStream(os).use { oos ->
                            oos.writeObject(SongList().apply { listName = contentText?:throw Exception() })
                            oos.flush()
                        }
                        os.flush()
                    }
                }
                (parentFragment as MainFragment?)?.updateList(songListInfoList)
            }
            .setNegativeButton(R.string.createSongListDialog_cancel) { _, _ ->
                dismiss()
            }
            .show().apply {
                getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                editTextTitle.addTextChangedListener { content ->
                    getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !content.isNullOrEmpty()
                    textViewCount.text = (content?.length?:0).toString()
                    contentText = content.toString()
                }
            }
    }

    fun showNow(manager: FragmentManager) {
        super.showNow(manager, TAG)
    }

}
