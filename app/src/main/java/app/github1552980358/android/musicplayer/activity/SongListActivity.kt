package app.github1552980358.android.musicplayer.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.adapter.SongListContentRecyclerViewAdapter
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.base.Constant
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumRoundDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.DEFAULT_VALUE_INT
import app.github1552980358.android.musicplayer.base.Constant.Companion.DEFAULT_VALUE_STR
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ALBUM
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ARTIST
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_DURATION
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_ID
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_PRESENT
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_AUDIO_TITLE
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_COVER
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_INFO
import app.github1552980358.android.musicplayer.base.Constant.Companion.INTENT_SONG_LIST_POS
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListCoverDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListDir
import app.github1552980358.android.musicplayer.base.Constant.Companion.SongListFile
import app.github1552980358.android.musicplayer.base.SongList
import app.github1552980358.android.musicplayer.base.SongListCover
import app.github1552980358.android.musicplayer.base.SongListInfo
import app.github1552980358.android.musicplayer.base.SongListInfo.Companion.songListInfoList
import app.github1552980358.android.musicplayer.base.TimeExchange
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main_bottomsheet.cardView
import kotlinx.android.synthetic.main.activity_song_list.appBarLayout
import kotlinx.android.synthetic.main.activity_song_list.collapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_song_list.imageViewCover
import kotlinx.android.synthetic.main.activity_song_list.recyclerView
import kotlinx.android.synthetic.main.activity_song_list.textViewDescription
import kotlinx.android.synthetic.main.activity_song_list.textViewSubtitle
import kotlinx.android.synthetic.main.activity_song_list.textViewTitle
import kotlinx.android.synthetic.main.activity_song_list.toolbar
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.checkBoxPlay
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.imageView
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.linearLayoutBottom
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.textViewSubtitle_bottom_sheet
import kotlinx.android.synthetic.main.activity_song_list_bottomsheet.textViewTitle_bottom_sheet
import lib.github1552980358.ktExtension.jvm.io.readObjectAs
import lib.github1552980358.ktExtension.jvm.io.writeObject
import java.io.File

/**
 * [SongListActivity]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/24
 * @time    : 13:29
 **/

class SongListActivity: BaseAppCompatActivity(), TimeExchange {
    
    /**
     * [songListInfo]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var songListInfo: SongListInfo
    
    /**
     * [bottomSheetBehavior]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    
    /**
     * [songListCover]
     * @author 1552980358
     * @since 0.1
     **/
    private var songListCover: SongListCover? = null
    
    /**
     * [songList]
     * @author 1552980358
     * @since 0.1
     **/
    private lateinit var songList: SongList
    
    /**
     * [position]
     * @author 1552980358
     * @since 0.1
     **/
    private var position = DEFAULT_VALUE_INT
    
    /**
     * [listTitle]
     * @author 1552980358
     * @since 0.1
     **/
    private var listTitle = DEFAULT_VALUE_STR
    
    /**
     * [onCreate]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("SongListActivity", "onCreate")
        
        songListInfo = intent?.getSerializableExtra(INTENT_SONG_LIST_INFO) as SongListInfo
        
        File(getExternalFilesDir(SongListCoverDir), songListInfo.listTitle).apply {
            @Suppress("LABEL_NAME_CLASH")
            if (!exists()) {
                window.statusBarColor = Color.WHITE
                window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                return@apply
            }
    
            // Load image and colours
            // 加载图片以及颜色
            songListCover = readObjectAs()!!
            /**
             * inputStream().use { `is` ->
             *     ObjectInputStream(`is`).use { ois ->
             *         songListCover = (ois.readObject() as SongListCover)
             *     }
             * }
             **/
    
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
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK)
                collapsingToolbarLayout.setExpandedTitleColor(Color.BLACK)
                toolbar.overflowIcon?.setTint(Color.BLACK)
                return@apply
            }
            
            songListCover.apply {
                @Suppress("LABEL_NAME_CLASH")
                this ?: return@apply
                
                // Set colours
                // 设置颜色
                imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
                appBarLayout.setBackgroundColor(backgroundColour)
                textViewTitle.setTextColor(primaryTextColour)
                textViewSubtitle.setTextColor(secondaryTextColour)
                textViewDescription.setTextColor(secondaryTextColour)
                collapsingToolbarLayout.setCollapsedTitleTextColor(primaryTextColour)
                collapsingToolbarLayout.setExpandedTitleColor(primaryTextColour)
                toolbar.overflowIcon?.setTint(primaryTextColour)
            }
            
        }
        
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        
        toolbar.navigationIcon?.setTint(if (songListCover != null) songListCover!!.primaryTextColour else Color.BLACK)
        
        toolbar.setNavigationOnClickListener { onBackPressed() }
        
        @Suppress("UNCHECKED_CAST")
        bottomSheetBehavior = BottomSheetBehavior.from(cardView) as BottomSheetBehavior<View>
        
        listTitle = songListInfo.listTitle
        
        readSongList()
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SongListContentRecyclerViewAdapter(
            this,
            songListInfo.listTitle,
            if (songListInfo.customSort) songList.audioListCustom else songList.audioList
        )
        
        checkBoxPlay.setOnClickListener {
            mediaControllerCompat.transportControls.apply {
                if (checkBoxPlay.isChecked) play() else pause()
            }
        }
        
        linearLayoutBottom.setOnClickListener {
            if (mediaControllerCompat.playbackState.state == PlaybackStateCompat.STATE_NONE) {
                return@setOnClickListener
            }
            startActivityForResult(
                Intent(this, AudioActivity::class.java)
                    .putExtra(
                        INTENT_AUDIO_TITLE,
                        mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                    )
                    .putExtra(
                        INTENT_AUDIO_ARTIST,
                        mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
                    )
                    .putExtra(
                        INTENT_AUDIO_ALBUM,
                        mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
                    )
                    .putExtra(
                        INTENT_AUDIO_DURATION,
                        mediaControllerCompat.metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                    )
                    .putExtra(
                        INTENT_AUDIO_ID,
                        mediaControllerCompat.metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                    ),
                0,
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    imageView, "imageView"
                ).toBundle()
            )
        }
        
        position = intent?.getIntExtra(INTENT_SONG_LIST_POS, DEFAULT_VALUE_INT) ?: DEFAULT_VALUE_INT
        
        if (!intent.getBooleanExtra(INTENT_AUDIO_PRESENT, false)) {
            textViewTitle_bottom_sheet.setText(R.string.songListActivity_bottom_sheet_title)
            textViewSubtitle_bottom_sheet.visibility = View.GONE
            return
        }
    
        textViewTitle_bottom_sheet.text = intent.getStringExtra(INTENT_AUDIO_TITLE)
        textViewSubtitle_bottom_sheet.text = intent.getStringExtra(INTENT_AUDIO_ARTIST)
    
    }
    
    /**
     * [onResume]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onResume() {
        super.onResume()
        Log.e("SongListActivity", "onResume")
    }
    
    /**
     * [readSongList]
     * @author 1552980358
     * @since 0.1
     **/
    private fun readSongList() {
        File(getExternalFilesDir(SongListDir), listTitle).apply {
            if (!exists()) {
                finish()
                return
            }
            
            songList = readObjectAs()!!
            
            /**
             * inputStream().use { `is` ->
             *    ObjectInputStream(`is`).use { ois ->
             *        songList = ois.readObject() as SongList
             *    }
             *
             * }
             **/
        }
    }
    
    /**
     * [onMetadataChanged]
     * @param metadata [MediaBrowserCompat]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        if (textViewSubtitle_bottom_sheet.visibility == View.GONE) {
            textViewSubtitle_bottom_sheet.visibility = View.VISIBLE
        }
        textViewTitle_bottom_sheet.text = metadata!!.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        textViewSubtitle_bottom_sheet.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        
        File(
            getExternalFilesDir(AlbumRoundDir),
            metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        ).apply {
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
     * [onMetadataChanged]
     * @param state [PlaybackStateCompat]?
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
     * [onMetadataChanged]
     * @param parentId [String]
     * @param children [MutableList]<[MediaBrowserCompat.MediaItem]>
     * @author 1552980358
     * @since 0.1
     **/
    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
        //
    }
    
    /**
     * [onCreateOptionsMenu]
     * @param menu [Menu]?
     * @return [Boolean]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_song_list_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    /**
     * [onPrepareOptionsMenu]
     * @param menu [Menu]?
     * @return [Boolean]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_song_list_sort)
            ?.setTitle(
                if (songListInfo.customSort) R.string.songListActivity_menu_change_sort_custom
                else R.string.songListActivity_menu_change_sort_name
            )
        return super.onPrepareOptionsMenu(menu)
    }
    
    /**
     * [onOptionsItemSelected]
     * @param item [MenuItem]
     * @return [Boolean]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_song_list_sort -> {
                songListInfo.customSort = !songListInfo.customSort
                File(getExternalFilesDir(Constant.AudioDataDir), SongListFile).writeObject(songListInfoList)
                (recyclerView.adapter as SongListContentRecyclerViewAdapter).updateSongList(
                    if (songListInfo.customSort) songList.audioListCustom else songList.audioList
                )
            }
            R.id.menu_song_list_edit_info -> {
                startActivityForResult(
                    Intent(this, SongListEditingActivity::class.java)
                        .putExtra(INTENT_SONG_LIST_INFO, songListInfo)
                        .putExtra(INTENT_SONG_LIST_POS, position), 0
                )
            }
            R.id.menu_song_list_edit_sequence -> {
                startActivityForResult(
                    Intent(this, SongListListEditingActivity::class.java)
                        .putExtra(INTENT_SONG_LIST_INFO, songListInfo)
                        .putExtra(INTENT_SONG_LIST_POS, position),
                    1
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    /**
     * [onActivityResult]
     * @param requestCode [Int]
     * @param resultCode [Int]
     * @param data [Intent]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                    return
                }
                songListCover = (data?.getSerializableExtra(INTENT_SONG_LIST_COVER) as SongListCover?)?.apply {
                    imageViewCover.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.size))
                    appBarLayout.setBackgroundColor(backgroundColour)
                    textViewTitle.setTextColor(primaryTextColour)
                    textViewSubtitle.setTextColor(secondaryTextColour)
                    textViewDescription.setTextColor(secondaryTextColour)
                    collapsingToolbarLayout.setCollapsedTitleTextColor(primaryTextColour)
                    collapsingToolbarLayout.setExpandedTitleColor(primaryTextColour)
                    toolbar.overflowIcon?.setTint(primaryTextColour)
                }
        
                songListInfo = (data?.getSerializableExtra(INTENT_SONG_LIST_INFO) as SongListInfo).apply {
                    textViewTitle_bottom_sheet.text = listTitle
                }
            }
            1 -> {
                readSongList()
                (recyclerView.adapter as SongListContentRecyclerViewAdapter).updateSongList(
                    if (songListInfo.customSort) songList.audioListCustom else songList.audioList
                )
            }
        }
        
    }
    
    /**
     * [onConnected]
     * @param mediaControllerCompat [MediaControllerCompat]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onConnected(mediaControllerCompat: MediaControllerCompat) {
        when (mediaControllerCompat.playbackState.state) {
    
            PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.STATE_BUFFERING -> {
                checkBoxPlay.isChecked = true
            }
    
            PlaybackStateCompat.STATE_PAUSED -> {
                checkBoxPlay.isChecked = false
            }
            
        }
        
    }
    
}