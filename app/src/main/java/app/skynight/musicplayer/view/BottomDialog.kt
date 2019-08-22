package app.skynight.musicplayer.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import app.skynight.musicplayer.R

/**
 * @File    : BottomDialog
 * @Author  : 1552980358
 * @Date    : 21 Aug 2019
 * @TIME    : 9:59 AM
 **/
class BottomDialog: DialogFragment() {
    override fun onStart() {
        super.onStart()
        dialog!!.window!!.apply {
            val wa = attributes.apply {
                gravity = Gravity.BOTTOM
                width = WindowManager.LayoutParams.MATCH_PARENT
            }
            attributes = wa
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context).apply {
            setTitle(R.string.abc_bottomList_header)

            @Suppress("InflateParams")
            setView(LayoutInflater.from(context).inflate(R.layout.dialog_bottom, null, false).apply {

            })

            setPositiveButton("") { p0, p1 ->

            }
        }.create()
    }
}