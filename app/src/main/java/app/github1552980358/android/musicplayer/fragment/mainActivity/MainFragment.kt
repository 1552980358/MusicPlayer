package app.github1552980358.android.musicplayer.fragment.mainActivity

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
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.dialog.CreateSongListDialogFragment
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * [MainFragment]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/19
 * @time    : 10:54
 **/

class MainFragment : Fragment() {

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
        recyclerView.adapter = SongListRecyclerViewAdapter(SongList.songListInfoList)

    }

    fun updateList(songList: ArrayList<SongList.Companion.SongListInfo>) {
        (recyclerView.adapter as SongListRecyclerViewAdapter).updateList(songList)
    }

}