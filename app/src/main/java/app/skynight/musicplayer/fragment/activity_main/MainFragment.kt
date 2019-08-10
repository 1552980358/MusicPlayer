package app.skynight.musicplayer.fragment.activity_main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import app.skynight.musicplayer.util.log

/**
 * @File    : MusicFragment
 * @Author  : 1552980358
 * @Date    : 30 Jul 2019
 * @TIME    : 9:21 PM
 **/

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        log("MainFragment", "onCreateView")
        return LinearLayout(context!!).apply {
            orientation = LinearLayout.VERTICAL

            addView(LinearLayout(context).apply {

            }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
    }

    override fun onStart() {
        log("MainFragment", "onStart")
        super.onStart()
    }
    override fun onResume() {
        log("MainFragment", "onResume")
        super.onResume()
    }
}