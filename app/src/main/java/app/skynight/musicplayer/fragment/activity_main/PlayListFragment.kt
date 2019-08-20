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
import app.skynight.musicplayer.activity.FindEncryptedMusicActivity
import app.skynight.musicplayer.activity.MusicListActivity
import app.skynight.musicplayer.util.Player.Companion.EXTRA_LIST
import app.skynight.musicplayer.util.Player.Companion.LIST_ALL
import app.skynight.musicplayer.util.log

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
            log("PlayListFragment", "- onCreateView")
            orientation = LinearLayout.VERTICAL
            addView(PlayListContainerView(context).apply {
                addView(PlayListView(context).apply {
                    setUp(R.drawable.ic_main_full, R.string.abc_main_list_full)
                    setOnClickListener {
                        startActivity(Intent(context, MusicListActivity::class.java)
                            .putExtra(EXTRA_LIST, LIST_ALL)
                        )
                    }
                })
                addView(PlayListView(context).apply {
                    setUp(R.drawable.ic_main_full, "转换qmc0/qmc3/qmcflac/ncm文件")
                    setOnClickListener {
                        startActivity(Intent(context, FindEncryptedMusicActivity::class.java))
                    }
                })
            }, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
    }

    override fun onStart() {
        log("PlayListFragment", "- onStart")
        super.onStart()
    }
    override fun onResume() {
        log("PlayListFragment", "- onResume")
        super.onResume()
    }
}