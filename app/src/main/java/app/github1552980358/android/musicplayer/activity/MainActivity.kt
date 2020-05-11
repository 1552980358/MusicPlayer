package app.github1552980358.android.musicplayer.activity

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.adapter.FragmentPagerAdapter
import app.github1552980358.android.musicplayer.base.AudioData
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.fragment.mainActivity.MainFragment
import app.github1552980358.android.musicplayer.fragment.mainActivity.SettingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.viewPager
import kotlinx.android.synthetic.main.activity_main_bottomsheet.cardView
import kotlinx.android.synthetic.main.activity_main_bottomsheet.textViewSubtitle
import kotlinx.android.synthetic.main.activity_main_bottomsheet.textViewTitle

/**
 * @file    : [MainActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/8
 * @time    : 21:22
 **/

class MainActivity : BaseAppCompatActivity() {
    
    private lateinit var mainFragment: MainFragment
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("MainActivity", "onCreate")
        setContentView(R.layout.activity_main)
    
        setSupportActionBar(toolbar)
    
        @Suppress("UNCHECKED_CAST")
        val bottomSheetBehavior = BottomSheetBehavior.from(cardView) as BottomSheetBehavior<View>
        
        mainFragment = MainFragment(bottomSheetBehavior)
        
        // UI
        viewPager.apply {
            adapter = FragmentPagerAdapter(supportFragmentManager, arrayListOf(mainFragment, SettingFragment(bottomSheetBehavior)))
            setOnScrollChangeListener { _, _, _, _, _ ->
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        tabLayout.apply {
            setupWithViewPager(viewPager)
            getTabAt(0)!!.setIcon(R.drawable.ic_tab_music)
            getTabAt(1)!!.setIcon(R.drawable.ic_tab_setting)
        }
        
        
    }
    
    /**
     * [onPause]
     **/
    override fun onPause() {
        super.onPause()
        Log.e("MainActivity", "onPause")
    }
    
    /**
     * [onResume]
     **/
    override fun onResume() {
        super.onResume()
        Log.e("MainActivity", "onResume")
    }
    
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        for (i in AudioData.audioData) {
            if (metadata?.description?.mediaId != i.id)
                continue
            
            textViewTitle.text = i.title
            textViewSubtitle.text = i.artist
            break
        }
    }
    
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
    
    }
    
    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
        mainFragment.updateList()
    }
    
}
