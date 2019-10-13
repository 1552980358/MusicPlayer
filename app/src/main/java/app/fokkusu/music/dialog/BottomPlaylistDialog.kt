package app.fokkusu.music.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import app.fokkusu.music.base.log
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import app.fokkusu.music.R
import kotlinx.android.synthetic.main.dialog_bottom_playlist.linearLayout_root

/**
 * @File    : BottomPlaylistDialog
 * @Author  : 1552980358
 * @Date    : 13 Oct 2019
 * @TIME    : 7:12 PM
 **/

class BottomPlaylistDialog : BottomSheetDialogFragment() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.dialog_bottom_playlist)
            linearLayout_root.layoutParams.height = resources.displayMetrics.heightPixels / 2
        }
    }
    
    override fun onCancel(dialog: DialogInterface) {
        fragmentManager!!.beginTransaction().remove(this).commit()
        super.onCancel(dialog)
    }
}