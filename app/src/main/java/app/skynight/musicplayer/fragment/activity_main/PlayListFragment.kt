package app.skynight.musicplayer.fragment.activity_main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import app.skynight.musicplayer.view.PlayListContainerView
import app.skynight.musicplayer.view.PlayListView
import app.skynight.musicplayer.R
import app.skynight.musicplayer.activity.MusicListActivity

/**
 * @File    : PlayListFragment
 * @Author  : 1552980358
 * @Date    : 30 Jul 2019
 * @TIME    : 9:28 PM
 **/
class PlayListFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LinearLayout(context!!).apply {
            orientation = LinearLayout.VERTICAL
            addView(PlayListContainerView(context).apply {
                addView(PlayListView(context).apply {
                    setUp(R.drawable.ic_main_full_def, R.string.abc_main_list_full)
                    setOnClickListener {
                        startActivity(Intent(context, MusicListActivity::class.java))
                    }
                })
            }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
    }
}