package app.github1552980358.android.musicplayer.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.adapter.FragmentPagerAdapter
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.base.Constant.Companion.SmallAlbumRound
import app.github1552980358.android.musicplayer.fragment.mainActivity.MainFragment
import app.github1552980358.android.musicplayer.fragment.mainActivity.SettingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.viewPager
import kotlinx.android.synthetic.main.activity_main_bottomsheet.cardView
import kotlinx.android.synthetic.main.activity_main_bottomsheet.imageView
import kotlinx.android.synthetic.main.activity_main_bottomsheet.linearLayoutBottom
import kotlinx.android.synthetic.main.activity_main_bottomsheet.textViewSubtitle
import kotlinx.android.synthetic.main.activity_main_bottomsheet.textViewTitle
import java.io.File

/**
 * @file    : [MainActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/8
 * @time    : 21:22
 **/

class MainActivity : BaseAppCompatActivity() {
    
    /**
     * [mainFragment] <[MainFragment]>
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var mainFragment: MainFragment
    
    /**
     * [onCreate]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("MainActivity", "onCreate")
        setContentView(R.layout.activity_main)
    
        setSupportActionBar(toolbar)
    
        @Suppress("UNCHECKED_CAST")
        val bottomSheetBehavior = BottomSheetBehavior.from(cardView) as BottomSheetBehavior<View>
    
        MainFragment.bottomSheetBehavior = bottomSheetBehavior
    
        // UI
        viewPager.apply {
            adapter = FragmentPagerAdapter(
                supportFragmentManager,
                arrayListOf(MainFragment().apply { mainFragment = this }, SettingFragment())
            )
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
    
        linearLayoutBottom.setOnClickListener {
            if (mediaControllerCompat.playbackState.state == PlaybackStateCompat.STATE_NONE) {
                
                return@setOnClickListener
            }
            
            startActivityForResult(
                Intent(this, AudioActivity::class.java),
                0,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    imageView, "img"
                ).toBundle()
            )
        }
    
        textViewTitle.apply {
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            
        }
        textViewSubtitle.apply {
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
    }
    
    /**
     * [onPause]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onPause() {
        super.onPause()
        Log.e("MainActivity", "onPause")
    }
    
    /**
     * [onResume]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onResume() {
        super.onResume()
        Log.e("MainActivity", "onResume")
    }
    
    /**
     * [onActivityResult]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mediaControllerCompat.metadata.apply {
            textViewTitle.text = getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            textViewSubtitle.text = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    
            File(getExternalFilesDir(SmallAlbumRound), getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).apply {
                if (!exists())
                    return
        
                imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream()))
            }
        }
    }
    
    /**
     * [onMetadataChanged]
     * @param metadata [MediaMetadataCompat]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        textViewTitle.text = metadata!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        textViewSubtitle.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        
        File(getExternalFilesDir(SmallAlbumRound), metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).apply {
            if (!exists())
                return
    
            imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream()))
        }
        
    }
    
    /**
     * [onPlaybackStateChanged]
     * @param state [PlaybackStateCompat]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
    
    }
    
    /**
     * [onChildrenLoaded]
     * @param parentId [String]
     * @param children [MutableList]<[MediaBrowserCompat.MediaItem]>
     * @author 1552980358
     * @since 0.1
     **/
    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
        mainFragment.updateList()
    }
    
}
