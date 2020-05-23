package app.github1552980358.android.musicplayer.fragment.mainActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.activity.MainActivity
import app.github1552980358.android.musicplayer.adapter.ListFragmentRecyclerViewAdapter
import app.github1552980358.android.musicplayer.base.AudioData.Companion.audioDataList
import app.github1552980358.android.musicplayer.base.Constant.Companion.DEFAULT_VALUE_INT
import app.github1552980358.android.musicplayer.view.SideCharView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_list.textViewChar
import kotlinx.android.synthetic.main.fragment_list.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_list.recyclerView
import kotlinx.android.synthetic.main.fragment_list.sideCharView

/**
 * @file    : [ListFragment]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 15:37
 **/

class ListFragment:
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

        val charTable = arrayListOf<Int>()

        swipeRefreshLayout.setOnRefreshListener {
            @Suppress("DuplicatedCode", "DuplicatedCode")
            for (i in 0 .. 26) {
                charTable.add(DEFAULT_VALUE_INT)
            }
            @Suppress("DuplicatedCode")
            for ((i, j) in audioDataList.withIndex()) {
                if (j.titlePinYin.first() in 'A' .. 'Z') {
                    if (charTable[j.titlePinYin.first().toInt() - 64] == DEFAULT_VALUE_INT) {
                        charTable[j.titlePinYin.first().toInt() - 64] = i
                    }
                } else if (charTable.first() == DEFAULT_VALUE_INT) {
                    charTable[0] = i
                }
            }
            updateList()
        }

        @Suppress("DuplicatedCode")
        for (i in 0 .. 26) {
            charTable.add(DEFAULT_VALUE_INT)
        }
        
        @Suppress("DuplicatedCode")
        for ((i, j) in audioDataList.withIndex()) {
            if (j.titlePinYin.first() in 'A' .. 'Z') {
                if (charTable[j.titlePinYin.first().toInt() - 64] == DEFAULT_VALUE_INT) {
                    charTable[j.titlePinYin.first().toInt() - 64] = i
                }
            } else if (charTable.first() == DEFAULT_VALUE_INT) {
                charTable[0] = i
            }
        }

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = ListFragmentRecyclerViewAdapter(
            (activity as MainActivity).bottomSheetBehavior, swipeRefreshLayout,
            activity as MainActivity
        )
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            /**
             * [onScrolled]
             * @param recyclerView [RecyclerView]
             * @param dx [Int]
             * @param dy [Int]
             * @author 1552980358
             * @since 0.1
             **/
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ((activity as MainActivity).bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    (activity as MainActivity).bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                sideCharView.updatePosition(
                    audioDataList[
                        (recyclerView.getChildAt(0).layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
                    ].titlePinYin.first().run { if (this in 'A' .. 'Z') this else '#' }
                )
            }
        })
        
        var temp:Int
        sideCharView.setOnTouchListener(object : SideCharView.Companion.OnTouchEventListener {
    
            /**
             * [onNewCharSelected]
             * @param newChar [Char]
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            override fun onNewCharSelected(newChar: Char): Boolean {
                textViewChar.text = newChar.toString()
                temp = newChar.run { if (newChar in 'A' .. 'Z') this.toInt() - 64 else 0 }
                if (charTable[temp] != DEFAULT_VALUE_INT) {
                    (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(charTable[temp], 0)
                    return true
                }
                sideCharView.updatePosition(newChar)
                return true
            }
    
            /**
             * [onMotionDown]
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            override fun onMotionDown(): Boolean {
                textViewChar.visibility = View.VISIBLE
                return true
            }
    
            /**
             * [onMotionUp]
             * @return [Boolean]
             * @author 1552980358
             * @since 0.1
             **/
            override fun onMotionUp(): Boolean {
                textViewChar.visibility = View.GONE
                return true
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