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
import app.github1552980358.android.musicplayer.adapter.MainFragmentRecyclerViewAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_main.recyclerView
import kotlinx.android.synthetic.main.fragment_main.swipeRefreshLayout

/**
 * @file    : [MainFragment]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/9
 * @time    : 15:37
 **/

class MainFragment :
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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
    
    /**
     * [onViewCreated]
     * @author 1552980358
     * @since 0.1
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        /*
        listView.apply {
            setOnScrollListener(object : AbsListView.OnScrollListener{
                override fun onScroll(
                    view: AbsListView?,
                    firstVisibleItem: Int,
                    visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    //if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    //   bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            
                override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                }
            })
        
            setOnTouchListener { _, _ ->
                //if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                //    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return@setOnTouchListener true
            }
        
        
            adapter = object : BaseAdapter() {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    return (context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.view_media_list, parent, false).apply {
                        view.findViewById<TextView>(R.id.textViewNo)?.text = position.toString()
                        view.findViewById<TextView>(R.id.textViewTitle)?.text = list[position].title
                        view.findViewById<TextView>(R.id.textViewSubtitle)?.text = list[position].artist
                        Log.e("getView", position.toString() + " " + list[position].title + " " + list[position].artist)
                    }
                }
            
                override fun getItem(position: Int): Any {
                    return list[position]
                }
            
                override fun getItemId(position: Int): Long {
                    return position.toLong()
                }
            
                override fun getCount(): Int {
                    return list.size
                }
            
            }
        }
         */
        
        swipeRefreshLayout.setOnRefreshListener {
            updateList()
        }
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MainFragmentRecyclerViewAdapter(
            (activity as MainActivity).bottomSheetBehavior, swipeRefreshLayout,
            activity as MainActivity
        )
        
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ((activity as MainActivity).bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    (activity as MainActivity).bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
        
    }
    
    /**
     * [updateList]
     * @author 1552980358
     * @since 0.1
     */
    fun updateList() {
        (recyclerView?.adapter as MainFragmentRecyclerViewAdapter?)?.updateList()
    }
    
}