package app.github1552980358.android.musicplayer.fragment.mainActivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.MainActivity
import app.github1552980358.android.musicplayer.adapter.SongListRecyclerViewAdapter
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.SongListInfo.Companion.songListInfoList
import app.github1552980358.android.musicplayer.dialog.CreateSongListDialogFragment
import kotlinx.android.synthetic.main.fragment_main.recyclerView
import kotlinx.android.synthetic.main.fragment_main.relativeLayoutAudio
import kotlinx.android.synthetic.main.fragment_main.relativeLayoutSettings
import kotlinx.android.synthetic.main.fragment_main.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_main.textViewNewList

/**
 * [MainFragment]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/19
 * @time    : 10:54
 **/

class MainFragment : Fragment() {
    
    private var onCreated = false

    /**
     * [onCreateView]
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    /**
     * [onViewCreated]
     * @param view [View]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        relativeLayoutAudio.setOnClickListener {
            (activity as MainActivity).findViewById<ViewPager>(R.id.viewPager).currentItem = 0
        }

        relativeLayoutSettings.setOnClickListener {
            (activity as MainActivity).findViewById<ViewPager>(R.id.viewPager).currentItem = 2
        }

        textViewNewList.setOnClickListener {
            CreateSongListDialogFragment().showNow(activity!!.supportFragmentManager)
        }

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SongListRecyclerViewAdapter(songListInfoList, this)

    }
    
    /**
     * [onActivityResult]
     * @param requestCode [Int]
     * @param resultCode [Int]
     * @param data [Intent]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateList(songListInfoList)
    }
    
    /**
     * [updateList]
     * @param songListInfoList [ArrayList]<[SongListInfo]>
     * @author 1552980358
     * @since 0.1
     **/
    fun updateList(songListInfoList: ArrayList<SongListInfo>) {
        (recyclerView.adapter as SongListRecyclerViewAdapter).updateList(songListInfoList)
    }
    
    /**
     * [onResume]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onResume() {
        super.onResume()
        if (!onCreated) {
            onCreated = true
            return
        }
        (recyclerView.adapter as SongListRecyclerViewAdapter).updateList(songListInfoList)
    }
    
}