package app.github1552980358.android.musicplayer.dialog

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.AudioActivity
import app.github1552980358.android.musicplayer.adapter.PlayHistoryDialogRecyclerViewAdapter
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormalDir
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_play_history.*
import java.io.File

/**
 * [PlayHistoryDialogFragment]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/17
 * @time    : 22:26
 **/

class PlayHistoryDialogFragment: BottomSheetDialogFragment() {

    companion object {
        /**
         * [TAG]
         * @author 1552980358
         * @since 0.1
         */
        const val TAG = "PlayHistoryDialogFragment"
    
        /**
         * [getFragment]
         * @author 1552980358
         * @since 0.1
         */
        val getFragment = PlayHistoryDialogFragment()

    }
    
    /**
     * [onCreateDialog]
     * @param savedInstanceState [Bundle]?
     * @return [Dialog]
     * @author 1552980358
     * @since 0.1
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.dialog_play_history)
            
            
            linearLayoutRoot.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, context.resources.displayMetrics.heightPixels / 2)
            (linearLayoutRoot.parent as FrameLayout).setBackgroundColor(Color.TRANSPARENT)
            
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = PlayHistoryDialogRecyclerViewAdapter(activity as AudioActivity)
            
            textViewTitle.text = (activity as AudioActivity).mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            textViewSubtitle.text = (activity as AudioActivity).mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
            File(context.getExternalFilesDir(AlbumNormalDir), (activity as AudioActivity).mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).apply {
                @Suppress("LABEL_NAME_CLASH")
                if (!exists()) {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                    return@apply
                }
                inputStream().use { `is` ->
                    imageView.setImageBitmap(BitmapFactory.decodeStream(`is`))
                }
            }
        }
    }
    
    /**
     * [showNow]
     * @param manager [FragmentManager]
     * @author 1552980358
     * @since 0.1
     */
    fun showNow(manager: FragmentManager) {
        this.show(manager, TAG)
    }

}