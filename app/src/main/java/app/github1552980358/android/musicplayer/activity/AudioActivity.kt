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
import android.view.Gravity
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import app.github1552980358.android.musicplayer.R
import app.github1552980358.android.musicplayer.base.BaseAppCompatActivity
import app.github1552980358.android.musicplayer.base.Colour
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumColourFile
import app.github1552980358.android.musicplayer.base.Constant.Companion.AlbumNormal
import app.github1552980358.android.musicplayer.base.Constant.Companion.BackgroundThread
import kotlinx.android.synthetic.main.activity_audio.checkBoxPlayPause
import kotlinx.android.synthetic.main.activity_audio.imageButtonLast
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

class AudioActivity : BaseAppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        
        window.decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE)
        
        window.navigationBarColor = TRANSPARENT
        window.statusBarColor = TRANSPARENT
        
        super.onCreate(savedInstanceState)
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
        File(getExternalFilesDir(AlbumNormal), intent.getStringExtra("ID")!!).apply {
            if (!exists())
                return@apply
            imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream()))
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
                        updateLayoutColours(backgroundColour, titleColour, subtitleColour)
                    }
                }
            }
        }
        
        checkBoxPlayPause.setOnClickListener {
            mediaControllerCompat.transportControls.apply {
                if (checkBoxPlayPause.isChecked) play() else pause()
            }
        }
        
        imageButtonLast.setOnClickListener {
            mediaControllerCompat.transportControls.skipToPrevious()
        }
        
        imageButtonNext.setOnClickListener {
            mediaControllerCompat.transportControls.skipToNext()
        }
    }
    
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        textViewTitle.text = metadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        textViewSubtitle1.text = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)
        textViewSubtitle2.text = metadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        seekBar.max = metadata?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)!!.toInt() / 1000
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
                        updateLayoutColours(backgroundColour, titleColour, subtitleColour)
                    }
                }
            }
        }
    }
    
    private fun setUpSeekbar() {
        LabourForce.onDuty.sendWork2Labour(
            BackgroundThread,
            LabourWorkBuilder
                .getBuilder()
                .setWorkContent(object : LabourWorkBuilder.Companion.WorkContent {
                    override fun workContent(workProduct: MutableMap<String, Any?>?, handler: Handler?) {
                        runOnUiThread {
                            while (mediaControllerCompat.playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                                runOnUiThread {
                                    seekBar.progress = mediaControllerCompat.playbackState.position.toInt() / 1000
                                }
                                try {
                                    Thread.sleep(500)
                                } catch (e: Exception) {
                                    //e.printStackTrace()
                                }
                            }
                        }
                    }
                })
        )
    }
    
    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        when (state?.state) {
            PlaybackStateCompat.STATE_BUFFERING -> {
                checkBoxPlayPause.isChecked = true
                seekBar.progress = 0
            }
            PlaybackStateCompat.STATE_PLAYING -> {
                checkBoxPlayPause.isChecked = true
                setUpSeekbar()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
        
            }
        }
    }
    
    override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
    
    }
    
    /**
     * [onBackPressed]
     * @author 1552980358
     * @since 0.1
     **/
    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
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
        
            }
        }
        
    }
    
    private fun updateLayoutColours(
        background: Int = -16524603,
        titleColour: Int = -13172557,
        subtitleColour: Int = -10354450
    ) {
        linearLayoutRoot.background.setTint(background)
        
        imageButtonLast.background.setTint(titleColour)
        imageButtonNext.background.setTint(titleColour)
        checkBoxPlayPause.background.setTint(titleColour)
        
        seekBar.thumb.setTint(titleColour)
        seekBar.progressDrawable.setTint(titleColour)
        seekBar.indeterminateDrawable.setTint(subtitleColour)
        
        textViewPassed.setTextColor(titleColour)
        textViewFull.setTextColor(titleColour)
        textViewDivider.setTextColor(titleColour)
        textViewTitle.setTextColor(titleColour)
        textViewSubtitle1.setTextColor(subtitleColour)
        textViewSubtitle2.setTextColor(subtitleColour)
    }
    
}