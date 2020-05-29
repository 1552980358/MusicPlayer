package app.github1552980358.android.musicplayer.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.adapter.AddToSongListDialogRecyclerViewAdapter
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.SongListInfo
import kotlinx.android.synthetic.main.dialog_add_to_song_list.recyclerView

/**
 * [AddToSongListDialogFragment]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/27
 * @time    : 21:41
 **/

class AddToSongListDialogFragment: DialogFragment() {
    
    companion object {
    
        /**
         * [TAG]
         * @author 1552980358
         * @since 0.1
         **/
        private const val TAG = "AddToSongListDialogFragment"
        
    }
    
    /**
     * [audioData]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var audioData: AudioData
    
    /**
     * [onCreateDialog]
     * @param savedInstanceState [Bundle]
     * @return [Dialog]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.addToSongListDialog_title)
            setView(R.layout.dialog_add_to_song_list)
        }.show().apply {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = AddToSongListDialogRecyclerViewAdapter(
                this@AddToSongListDialogFragment,
                audioData,
                SongListInfo.songListInfoList
            )
        }
    }
    
    /**
     * [updateList]
     * @param songListInfoList [ArrayList]<[SongListInfo]>
     * @author 1552980358
     * @since 0.1
     **/
    fun updateList(songListInfoList: ArrayList<SongListInfo>) {
        (dialog?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter as AddToSongListDialogRecyclerViewAdapter?)
            ?.updateList(songListInfoList)
    }
    
    /**
     * [showNow]
     * @param fragmentManager [FragmentManager]
     * @param audioData [AudioData]
     * @author 1552980358
     * @since 0.1
     **/
    fun showNow(fragmentManager: FragmentManager, audioData: AudioData) {
        this.audioData = audioData
        super.showNow(fragmentManager, TAG)
    }
    
}
