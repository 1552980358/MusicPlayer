package app.fokkusu.music.fragment.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.fokkusu.music.Application
import app.fokkusu.music.R
import app.fokkusu.music.base.Constants.Companion.APPLICATION_MEDIA_SCAN_COMPLETE
import app.fokkusu.music.service.PlayService
import kotlinx.android.synthetic.main.fragment_music.*

/**
 * @File    : MusicFragment
 * @Author  : 1552980358
 * @Date    : 6 Oct 2019
 * @TIME    : 8:58 AM
 **/

class MusicFragment : Fragment() {
    private var refresh = false
    
    private val broadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Update
                listMusicView.updateMusic(PlayService.musicList)
                
                swipeRefreshLayout.isRefreshing = false
                // Unregister BroadcastReceiver
                context!!.unregisterReceiver(this)
                refresh = false
            }
        }
    }
    
    private val intentFilter by lazy { IntentFilter(APPLICATION_MEDIA_SCAN_COMPLETE) }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        listMusicView.setUpAdapterWithMusicList(PlayService.musicList, 0)
        
        swipeRefreshLayout.setOnRefreshListener {
            if (refresh) {
                return@setOnRefreshListener
            }
            
            // Call for scanning
            Application.onScanMedia()
            // Wait for broadcast
            context!!.registerReceiver(broadcastReceiver, intentFilter)
            
            refresh = true
        }
    }
}