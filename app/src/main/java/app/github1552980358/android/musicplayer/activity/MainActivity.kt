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
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumRoundDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ALBUM
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ARTIST
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_DURATION
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ID
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_TITLE
import app.github1552980358.android.musicplayer.base.SystemUtil
import app.github1552980358.android.musicplayer.fragment.mainActivity.ListFragment
import app.github1552980358.android.musicplayer.fragment.mainActivity.MainFragment
import app.github1552980358.android.musicplayer.fragment.mainActivity.SettingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_main.viewPager
import kotlinx.android.synthetic.main.activity_main_bottomsheet.cardView
import kotlinx.android.synthetic.main.activity_main_bottomsheet.checkBoxPlay
import kotlinx.android.synthetic.main.activity_main_bottomsheet.imageButtonLast
import kotlinx.android.synthetic.main.activity_main_bottomsheet.imageButtonNext
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
     * [listFragment] <[ListFragment]>
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var listFragment: ListFragment
    
    /**
     * [bottomSheetBehavior]
     * @author 1552980358
     * @since 0.1
     **/
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    
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
        bottomSheetBehavior = BottomSheetBehavior.from(cardView) as BottomSheetBehavior<View>
        
        // UI
        viewPager.apply {
            adapter = FragmentPagerAdapter(
                supportFragmentManager,
                arrayListOf(ListFragment().apply { listFragment = this }, MainFragment(), SettingFragment())
            )
            currentItem = 1
            setOnScrollChangeListener { _, _, _, _, _ ->
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        
        tabLayout.apply {
            setupWithViewPager(viewPager)
            getTabAt(0)?.setIcon(R.drawable.ic_tab_music)
            getTabAt(1)?.setIcon(R.drawable.ic_tab_list)
            getTabAt(2)?.setIcon(R.drawable.ic_tab_setting)
        }
    
        linearLayoutBottom.setOnClickListener {
            if (mediaControllerCompat.playbackState.state == PlaybackStateCompat.STATE_NONE) {
                return@setOnClickListener
            }
    
            startActivityForResult(
                Intent(this, AudioActivity::class.java)
                    .putExtra(INTENT_AUDIO_TITLE, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE))
                    .putExtra(INTENT_AUDIO_ARTIST, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST))
                    .putExtra(INTENT_AUDIO_ALBUM, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM))
                    .putExtra(INTENT_AUDIO_DURATION, mediaControllerCompat.metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
                    .putExtra(INTENT_AUDIO_ID, mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)),
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
        
        checkBoxPlay.setOnClickListener {
            mediaControllerCompat.transportControls.apply {
                if (checkBoxPlay.isChecked) play() else pause()
            }
        }
        
        imageButtonLast.setOnClickListener {
            mediaControllerCompat.transportControls.skipToPrevious()
        }
        
        imageButtonNext.setOnClickListener {
            mediaControllerCompat.transportControls.skipToNext()
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
     * @author 1552980358
     * @since 0.1
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (data?.getStringExtra("AudioActivity") != "Complete") {
            return
        }
        
        mediaControllerCompat.metadata.apply {
            textViewTitle.text = getString(MediaMetadataCompat.METADATA_KEY_TITLE)
            textViewSubtitle.text = getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
    
            File(getExternalFilesDir(AlbumRoundDir), getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).apply {
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
    
    /**
     * [onBackPressed]
     * @author 1552980358
     * @since 0.1
     **/
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
        
        File(getExternalFilesDir(AlbumRoundDir), metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)).apply {
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
                checkBoxPlay.isChecked = true
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                checkBoxPlay.isChecked = false
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
        listFragment.updateList()
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
