package app.github1552980358.android.musicplayer.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.Constant.Companion.AudioDataDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListFile
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.SongListInfo.Companion.songListInfoList
import app.github1552980358.android.musicplayer.fragment.mainActivity.MainFragment
import kotlinx.android.synthetic.main.dialog_create_song_list.editTextTitle
import kotlinx.android.synthetic.main.dialog_create_song_list.textViewCount
import lib.github1552980358.ktExtension.jvm.io.writeObject
import java.io.File

/**
 * [CreateSongListDialogFragment]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/20
 * @time    : 9:13
 **/

class CreateSongListDialogFragment : DialogFragment() {

    companion object {
    
        /**
         * [TAG]
         * @author 1552980358
         * @since 0.1
         **/
        const val TAG = "AddSongListDialogFragment"
        
    }
    
    /**
     * [contentText]
     * @author 1552980358
     * @since 0.1
     **/
    private var contentText = "" as String?
    
    /**
     * [assignFragment]
     * @author 1552980358
     * @since 0.1
     **/
    private var assignFragment: Fragment? = null
    
    /**
     * [onCreateDialog]
     * @param savedInstanceState [Bundle]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.createSongListDialog_title)
            .setView(R.layout.dialog_create_song_list)
            .setPositiveButton(R.string.createSongListDialog_submit) { _, _ ->
                songListInfoList.add(
                    SongListInfo().apply {
                        listTitle = contentText?:throw Exception()
                        createDate = System.currentTimeMillis()
                    }
                )
                File(requireContext().getExternalFilesDir(AudioDataDir), SongListFile).apply {
                    if (exists())
                        delete()
                    createNewFile()
    
                    writeObject(songListInfoList)
                    /**
                     * outputStream().os { os ->
                     *     ObjectOutputStream(os).os { oos ->
                     *         oos.writeObject(songListInfoList)
                     *     }
                     * }
                     **/
                }
                File(requireContext().getExternalFilesDir(SongListDir), contentText?:throw Exception()).apply {
                    if (parentFile!!.exists()) {
                        parentFile!!.mkdirs()
                    }
    
                    createNewFile()
    
                    writeObject(SongList().apply { listName = contentText ?: throw Exception() })
                    /**
                     * outputStream().os { os ->
                     *     ObjectOutputStream(os).os { oos ->
                     *         oos.writeObject(SongList().apply { listName = contentText ?: throw Exception() })
                     *     }
                     * }
                     **/
    
                }
                
                if (parentFragment is AddToSongListDialogFragment) {
                    (parentFragment as AddToSongListDialogFragment).updateList(songListInfoList)
                } else {
                    (parentFragment as MainFragment?)?.updateList(songListInfoList)
                }
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
    
    /**
     * [setFragment]
     * @param fragment [Fragment]
     * @author 1552980358
     * @since 0.1
     **/
    fun setFragment(fragment: Fragment) {
        assignFragment = fragment
    }
    
    /**
     * [showNow]
     * @param manager [FragmentManager]
     * @author 1552980358
     * @since 0.1
     **/
    fun showNow(manager: FragmentManager) {
        super.showNow(manager, TAG)
    }

}
