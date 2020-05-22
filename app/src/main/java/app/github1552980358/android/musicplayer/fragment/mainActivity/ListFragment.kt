package app.github1552980358.android.musicplayer.fragment.mainActivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.MainActivity
import app.github1552980358.android.musicplayer.adapter.ListFragmentRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*
import kotlin.concurrent.thread

/**
 * @file    : [ListFragment]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 15:37
 **/

class ListFragment :
    Fragment() {
    
    /**
     * [onCreateView]
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]?
     * @param savedInstanceState [Bundle]?
     * @return [View]
     * @author 1552980358
     * @since 0.1
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }
    
    /**
     * [onViewCreated]
     * @author 1552980358
     * @since 0.1
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        swipeRefreshLayout.setOnRefreshListener {
            updateList()
        }
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ListFragmentRecyclerViewAdapter(
            (activity as MainActivity).bottomSheetBehavior, swipeRefreshLayout,
            activity as MainActivity
        )
        
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isScrolling = false
    
            val listener = object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    if (!isScrolling) {
                        sideLetterView.visibility = View.GONE
                    }
                }
            }
            
            val timer = Timer()
            
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ((activity as MainActivity).bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    (activity as MainActivity).bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
    
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    SCROLL_STATE_DRAGGING -> {
                        isScrolling = true
                        if (sideLetterView.alpha != 1F) {
                            sideLetterView.animate().cancel()
                            sideLetterView.visibility = View.VISIBLE
                            sideLetterView.animate().alpha(1F).setDuration(500L).setListener(null)
                        }
                    }
                    SCROLL_STATE_IDLE -> {
                        isScrolling = false
                        timer.schedule(object : TimerTask() {
                            override fun run() {
                                if (isScrolling) {
                                    return
                                }
                                activity?.runOnUiThread { sideLetterView.animate().alpha(0F).setDuration(1000L).setListener(listener) }
                            }
                        }, 1000)
                    }
                }
            }
        })
        
    }
    
    /**
     * [updateList]
     * @author 1552980358
     * @since 0.1
     */
    fun updateList() {
        (recyclerView?.adapter as ListFragmentRecyclerViewAdapter?)?.updateList()
    }
    
}