package app.github1552980358.android.musicplayer.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.widget.TextView
import androidx.core.content.ContextCompat
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_INFO
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListCoverDir
import app.github1552980358.android.musicplayer.base.SongListCover
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.SystemUtil
import app.github1552980358.android.musicplayer.base.TimeExchange
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main_bottomsheet.cardView
import kotlinx.android.synthetic.main.activity_song_list.appBarLayout
import kotlinx.android.synthetic.main.activity_song_list.collapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_song_list.imageViewCover
import kotlinx.android.synthetic.main.activity_song_list.textViewDescription
import kotlinx.android.synthetic.main.activity_song_list.textViewSubtitle
import kotlinx.android.synthetic.main.activity_song_list.textViewTitle
import kotlinx.android.synthetic.main.activity_song_list.toolbar
import java.io.File
import java.io.ObjectInputStream

/**
 * [SongListActivity]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/24
 * @time    : 13:29
 **/

class SongListActivity: BaseAppCompatActivity(), TimeExchange {
    
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    
    var songListCover: SongListCover? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val songListInfo = intent?.getSerializableExtra(INTENT_SONG_LIST_INFO) as SongListInfo
        
        File(getExternalFilesDir(SongListCoverDir), songListInfo.listTitle).apply {
            @Suppress("LABEL_NAME_CLASH")
            if (!exists()) {
                
                window.statusBarColor = Color.WHITE
                return@apply
            }
        
            // Load image and colours
            // 加载图片以及颜色
            inputStream().use { `is` ->
                ObjectInputStream(`is`).use { ois ->
                    songListCover = (ois.readObject() as SongListCover)
                }
            }
            
            window.statusBarColor = songListCover!!.backgroundColour
            
            @SuppressLint("InlinedApi")
            if (songListCover!!.isLight) {
                window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
            
        }
        
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_song_list)
        
        songListInfo.apply {
            toolbar.title = listTitle
    
            textViewTitle.text = listSize.toString()
            textViewSubtitle.text = getDateText(createDate)
            textViewDescription.text = description
    
            if (!hasCoverImage) {
                return@apply
            }
            
            songListCover.apply {
                @Suppress("LABEL_NAME_CLASH")
                this?:return@apply
    
                // Set colours
                // 设置颜色
                imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
                appBarLayout.setBackgroundColor(backgroundColour)
                textViewTitle.setTextColor(primaryTextColour)
                textViewSubtitle.setTextColor(secondaryTextColour)
                textViewDescription.setTextColor(secondaryTextColour)
                collapsingToolbarLayout.setCollapsedTitleTextColor(primaryTextColour)
                collapsingToolbarLayout.setExpandedTitleColor(primaryTextColour)
            }
            
        }
    
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    
        if (songListCover != null) {
            toolbar.navigationIcon?.setTint(songListCover!!.primaryTextColour)
        }
        
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        
        @Suppress("UNCHECKED_CAST")
        bottomSheetBehavior = BottomSheetBehavior.from(cardView) as BottomSheetBehavior<View>
        
    }
    
    override fun onResume() {
        super.onResume()
    }
    
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
    
    }
    
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
    
    }
    
    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
    
    }
    
    override fun onConnected(mediaControllerCompat: MediaControllerCompat) {
    
    }
    
}