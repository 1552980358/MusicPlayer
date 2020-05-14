package app.github1552980358.android.musicplayer.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.adapter.FragmentPagerAdapter
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.base.Constant.Companion.SmallAlbumRound
import app.github1552980358.android.musicplayer.base.SystemUtil
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

class MainActivity : BaseAppCompatActivity(), SystemUtil {
    
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
                Intent(this, AudioActivity::class.java)
                    .putExtra("TITLE", mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                    .putExtra("ARTIST", mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                    .putExtra("ALBUM", mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
                    .putExtra("DURATION", mediaControllerCompat.metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
                    .putExtra("ID", mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)),
                0,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    imageView, "img"
                ).toBundle()
            )
        }
    
        textViewTitle.apply {
            setText(R.string.mainActivity_bottom_sheet_title)
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            
        }
        textViewSubtitle.apply {
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        
        imageView.setImageResource(R.drawable.ic_launcher_foreground)
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
                if (!exists()) {
                    imageView.setImageResource(R.drawable.ic_launcher_foreground)
                    return
                }
    
                inputStream().use { `is` ->
                    imageView.setImageBitmap(BitmapFactory.decodeStream(`is`))
                }
                
            }
        }
    }
    
    override fun onBackPressed() {
        startActivity(
            Intent().apply {
                action = Intent.ACTION_MAIN
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addCategory(Intent.CATEGORY_HOME)
            }
        )
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
            if (!exists()) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
                return
            }
    
            inputStream().use { `is` ->
                imageView.setImageBitmap(BitmapFactory.decodeStream(`is`))
            }
        }
        
    }
    
    /**
     * [onPlaybackStateChanged]
     * @param state [PlaybackStateCompat]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        when (state?.state) {
            PlaybackStateCompat.STATE_BUFFERING, PlaybackStateCompat.STATE_PLAYING -> {
            
            }
            PlaybackStateCompat.STATE_PAUSED -> {
            
            }
        }
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
    
    /**
     * [onConnected]
     * @param [mediaControllerCompat] [MediaControllerCompat]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onConnected(mediaControllerCompat: MediaControllerCompat) {
    
    }
    
}
