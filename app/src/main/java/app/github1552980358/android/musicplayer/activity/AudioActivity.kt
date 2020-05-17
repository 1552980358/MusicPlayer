package app.github1552980358.android.musicplayer.activity

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.SeekBar
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.base.Colour
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumColourFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormal
import app.github1552980358.android.musicplayer.base.Constant.Companion.BackgroundThread
import app.github1552980358.android.musicplayer.base.SystemUtil
import app.github1552980358.android.musicplayer.base.TimeExchange
import kotlinx.android.synthetic.main.activity_audio.checkBoxPlayPause
import kotlinx.android.synthetic.main.activity_audio.imageButtonCycle
import kotlinx.android.synthetic.main.activity_audio.imageButtonLast
import kotlinx.android.synthetic.main.activity_audio.imageButtonList
import kotlinx.android.synthetic.main.activity_audio.imageButtonNext
import kotlinx.android.synthetic.main.activity_audio.imageView
import kotlinx.android.synthetic.main.activity_audio.linearLayoutRoot
import kotlinx.android.synthetic.main.activity_audio.seekBar
import kotlinx.android.synthetic.main.activity_audio.textViewDivider
import kotlinx.android.synthetic.main.activity_audio.textViewFull
import kotlinx.android.synthetic.main.activity_audio.textViewPassed
import kotlinx.android.synthetic.main.activity_audio.textViewSubtitle1
import kotlinx.android.synthetic.main.activity_audio.textViewSubtitle2
import kotlinx.android.synthetic.main.activity_audio.textViewTitle
import lib.github1552980358.labourforce.LabourForce
import lib.github1552980358.labourforce.labours.work.LabourWorkBuilder
import java.io.File
import java.io.ObjectInputStream

/**
 * @file    : [AudioActivity]
 * @author  : 1552980358
 * @since   : 0.1
 * @date    : 2020/5/11
 * @time    : 16:32
 **/

class AudioActivity : BaseAppCompatActivity(), TimeExchange, SystemUtil {
    
    /**
     * [seekBarTouched]
     * @author 1552980358
     * @since 0.1
     **/
    private var seekBarTouched = false
    
    /**
     * [exit]
     * @author 1552980358
     * @since 0.1
     **/
    private var exit = false
    
    /**
     * [imageButtonCycleColour]
     **/
    private var imageButtonCycleColour = -1
    
    /**
     * [onCreate]
     * @param savedInstanceState [Bundle]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onCreate(savedInstanceState: Bundle?) {
        
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)
        
        window.navigationBarColor = TRANSPARENT
        window.statusBarColor = TRANSPARENT
        
        super.onCreate(savedInstanceState)
        Log.e("AudioActivity", "onCreate")
        setContentView(R.layout.activity_audio)
        
        imageView.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, resources.displayMetrics.widthPixels)
        
        textViewTitle.apply {
            maxLines = 2
            ellipsize = TextUtils.TruncateAt.END
            gravity = Gravity.START
        }
        
        textViewSubtitle1.apply {
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        textViewSubtitle2.apply {
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        
        textViewTitle.text = intent.getStringExtra("TITLE")
        textViewSubtitle1.text = intent.getStringExtra("ALBUM")
        textViewSubtitle2.text = intent.getStringExtra("ARTIST")
        textViewFull.text = getTimeText(intent.getLongExtra("DURATION", 0L))
        seekBar.max = intent.getLongExtra("DURATION", 0L).toInt() / 1000
        
        imageButtonLast.setOnClickListener { mediaControllerCompat.transportControls.skipToPrevious() }
        imageButtonNext.setOnClickListener { mediaControllerCompat.transportControls.skipToNext() }
        imageButtonList.setOnClickListener {  }
        
        File(getExternalFilesDir(AlbumNormal), intent.getStringExtra("ID")!!).apply {
            if (!exists()) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
                return@apply
            }
            inputStream().use { `is` ->
                imageView.setImageBitmap(BitmapFactory.decodeStream(`is`))
            }
            
        }
        
        @Suppress("DuplicatedCode")
        File(getExternalFilesDir(AlbumColourFile), intent.getStringExtra("ID")!!).apply {
            
            if (!exists()) {
                updateLayoutColours()
                return@apply
            }
            
            inputStream().use { fis ->
                ObjectInputStream(fis).use { ois ->
                    (ois.readObject() as Colour).apply {
                        updateLayoutColours(backgroundColour, titleColour, subtitleColour, isLight)
                    }
                }
            }
        }
        
        checkBoxPlayPause.setOnClickListener {
            mediaControllerCompat.transportControls.apply {
                if (checkBoxPlayPause.isChecked) play() else pause()
            }
        }
        
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewPassed.text = getTimeText(progress)
            }
    
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                seekBarTouched = true
            }
    
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mediaControllerCompat.transportControls.seekTo(seekBar!!.progress * 1000L)
                seekBarTouched = false
            }
    
        })
        
    }
    /**
     * [onResume]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onResume() {
        super.onResume()
        Log.e("AudioActivity", "onResume")
    }
    
    /**
     * [onMetadataChanged]
     * @param metadata [MediaMetadataCompat]?
     * @author 1552980358
     * @since 0.1
     **/
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        textViewTitle.text = metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        textViewSubtitle1.text = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
        textViewSubtitle2.text = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        textViewFull.text = getTimeText(metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION))
        seekBar.max = metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)!!.toInt() / 1000
        
        File(
            getExternalFilesDir(AlbumNormal),
            metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        ).apply {
    
            if (!exists()) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
                return@apply
            }
            
            inputStream().use { `is` ->
                imageView.setImageBitmap(BitmapFactory.decodeStream(`is`))
            }
        }
    
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DuplicatedCode")
        File(
            getExternalFilesDir(AlbumColourFile),
            metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
        ).apply {
            
            if (!exists()) {
                updateLayoutColours()
                return@apply
            }
            
            inputStream().use { fis ->
                ObjectInputStream(fis).use { ois ->
                    (ois.readObject() as Colour).apply {
                        updateLayoutColours(backgroundColour, titleColour, subtitleColour, isLight)
                    }
                }
            }
        }
    }
    
    /**
     * [setUpSeekbar]
     * @author 1552980358
     * @since 0.1
     **/
    @Synchronized
    private fun setUpSeekbar() {
        LabourForce.onDuty.sendWork2Labour(
            BackgroundThread,
            LabourWorkBuilder
                .getBuilder()
                .setWorkContent(object : LabourWorkBuilder.Companion.WorkContent {
                    override fun workContent(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                        Log.e("setUpSeekbar", "workContent")
                        do {
                            if (!seekBarTouched) {
                                runOnUiThread {
                                    runOnUiThread {
                                        seekBar.progress = mediaControllerCompat.playbackState.position.toInt() / 1000
                                    }
                                }
                            }
                            try {
                                Thread.sleep(500)
                            } catch (e: Exception) {
                                //e.printStackTrace()
                            }
                        } while (mediaControllerCompat.playbackState.state == PlaybackStateCompat.STATE_PLAYING && !exit)
                        
                    }
                })
        )
    }
    
    /**
     * [onPlaybackStateChanged]
     * @param state [PlaybackStateCompat]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?:return
        when (state.state) {
            PlaybackStateCompat.STATE_BUFFERING -> {
                Log.e("onPlaybackStateChanged", "STATE_BUFFERING")
                checkBoxPlayPause.isChecked = true
                seekBar.progress = 0
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                Log.e("onPlaybackStateChanged", "STATE_PLAYING")
                if (!checkBoxPlayPause.isChecked) {
                    setUpSeekbar()
                }
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                Log.e("onPlaybackStateChanged", "STATE_PAUSED")
                checkBoxPlayPause.isChecked = false
            }
        }
        
        when (mediaControllerCompat.playbackState.customActions.first().name) {
            "SINGLE_CYCLE" -> {
                imageButtonCycle.setBackgroundResource(R.drawable.ic_audio_single)
            }
            "LIST_CYCLE" -> {
                imageButtonCycle.setBackgroundResource(R.drawable.ic_audio_list)
            }
            "RANDOM_ACCESS" -> {
                imageButtonCycle.setBackgroundResource(R.drawable.ic_audio_random)
            }
        }
    
        imageButtonCycle.background.setTint(imageButtonCycleColour)
    }
    
    /**
     * [onChildrenLoaded]
     * @param parentId [String]
     * @param children [MutableList]<[MediaBrowserCompat.MediaItem]>
     **/
    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
        //
    }
    
    /**
     * [onBackPressed]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBackPressed() {
        exit = true
        setResult(Activity.RESULT_OK, intent.putExtra("AudioActivity", "Complete"))
        super.onBackPressed()
    }
    
    /**
     * [onConnected]
     * @param [mediaControllerCompat] [MediaControllerCompat]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onConnected(mediaControllerCompat: MediaControllerCompat) {
        when (mediaControllerCompat.playbackState.state) {
            PlaybackStateCompat.STATE_BUFFERING -> {
                checkBoxPlayPause.isChecked = true
                seekBar.progress = 0
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                checkBoxPlayPause.isChecked = true
                setUpSeekbar()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                checkBoxPlayPause.isChecked = false
                seekBar.progress = mediaControllerCompat.playbackState.position.toInt() / 1000
            }
        }
        
        when (mediaControllerCompat.playbackState.customActions.first().name) {
            "SINGLE_CYCLE" -> {
                imageButtonCycle.setBackgroundResource(R.drawable.ic_audio_single)
            }
            "LIST_CYCLE" -> {
                imageButtonCycle.setBackgroundResource(R.drawable.ic_audio_list)
            }
           "RANDOM_ACCESS" -> {
                imageButtonCycle.setBackgroundResource(R.drawable.ic_audio_random)
            }
        }
    
        imageButtonCycle.background.setTint(imageButtonCycleColour)
    
    }
    
    /**
     * [updateLayoutColours]
     * @param background [Int]<-16524603>
     * @param titleColour [Int]<-13172557>
     * @param subtitleColour [Int]<-10354450>
     * @param isLight [Boolean]<true>
     **/
    @Synchronized
    private fun updateLayoutColours(
        background: Int = -1,
        titleColour: Int = -13172557,
        subtitleColour: Int = -10354450,
        isLight: Boolean = true
    ) {
        linearLayoutRoot.background.setTint(background)
        
        imageButtonLast.background.setTint(titleColour)
        imageButtonNext.background.setTint(titleColour)
        imageButtonList.background.setTint(titleColour)
        imageButtonCycle.background.setTint(titleColour)
        checkBoxPlayPause.background.setTint(titleColour)
        imageButtonCycleColour = titleColour
        
        seekBar.thumb.setTint(titleColour)
        seekBar.progressDrawable.setTint(titleColour)
        seekBar.indeterminateDrawable.setTint(subtitleColour)
        
        textViewPassed.setTextColor(titleColour)
        textViewFull.setTextColor(titleColour)
        textViewDivider.setTextColor(titleColour)
        textViewTitle.setTextColor(titleColour)
        textViewSubtitle1.setTextColor(subtitleColour)
        textViewSubtitle2.setTextColor(subtitleColour)
    
        @Suppress("SpellCheckingInspection")
        window.decorView.systemUiVisibility =
            if (isLight) {
                // On MIUI12, posting [SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR]
                // will cause white navigation bar background
                // MIUI12真机测试, 传 [SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR]
                // 会导致导航栏白色背景
                if (isMiUi12()) {
                    (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                } else {
                    (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                        or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }
            } else {
                (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)
            }
    }
    
}