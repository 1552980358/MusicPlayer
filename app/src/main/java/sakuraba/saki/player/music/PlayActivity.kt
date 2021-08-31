package sakuraba.saki.player.music

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.content.getStatusBarHeight
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import sakuraba.saki.player.music.base.BaseMediaControlActivity
import sakuraba.saki.player.music.databinding.ActivityPlayBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.Constants
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Coroutine.delay1second
import sakuraba.saki.player.music.util.Coroutine.ms_1000_int
import sakuraba.saki.player.music.util.SystemUtil.pixelHeight

class PlayActivity: BaseMediaControlActivity() {
    
    companion object {
        private const val TAG = "PlayActivity"
    }
    
    private var _activityPlayBinding: ActivityPlayBinding? = null
    private val activityPlay get() = _activityPlayBinding!!
    private lateinit var viewModel: PlayViewModel
    
    private var _textViewTitle: TextView? = null
    private val textViewTitle get() = _textViewTitle!!
    
    private var _textViewSummary: TextView? = null
    private val textViewSummary get() = _textViewSummary!!
    
    private lateinit var behavior: BottomSheetBehavior<LinearLayout>
    
    private var isPlaying = false
    private var job: Job? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e(TAG, "onCreate")
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        
        super.onCreate(savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        _activityPlayBinding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(activityPlay.root)
        
        activityPlay.imageView.layoutParams = activityPlay.imageView.layoutParams.apply { height = resources.displayMetrics.widthPixels }
        
        _textViewTitle = findViewById(R.id.text_view_title)
        _textViewSummary = findViewById(R.id.text_view_summary)
        
        CoroutineScope(Dispatchers.IO).launch {
            val audioInfo = intent?.getSerializableExtra(EXTRAS_AUDIO_INFO) as AudioInfo? ?: return@launch
            var bitmap = tryRun { loadAlbumArt(audioInfo.audioAlbumId) }
            if (bitmap != null) {
                launch(Dispatchers.Main) { activityPlay.imageView.setImageBitmap(bitmap) }
            }
            launch(Dispatchers.Main) {
                textViewTitle.text = audioInfo.audioTitle
                @Suppress("SetTextI18n")
                textViewSummary.text = "${audioInfo.audioArtist} - ${audioInfo.audioAlbum}"
            }
            MediaNotificationProcessor(this@PlayActivity, bitmap).apply {
                launch(Dispatchers.Main) {
                    ValueAnimator.ofArgb(Color.TRANSPARENT, backgroundColor).apply {
                        duration = 500
                        addUpdateListener {
                            activityPlay.root.setBackgroundColor(animatedValue as Int)
                        }
                        start()
                    }
                }
            }
        }
    
        findViewById<LinearLayout>(R.id.linear_layout).apply {
            layoutParams = layoutParams.apply { height = pixelHeight - getStatusBarHeight() }
        }
        
        behavior = BottomSheetBehavior.from(findViewById(R.id.linear_layout))
        behavior.peekHeight =
            pixelHeight - resources.displayMetrics.widthPixels -
                resources.getDimensionPixelSize(R.dimen.play_controller_height) -
                resources.getDimensionPixelSize(R.dimen.play_controller_seekbar_height)
        behavior.isHideable = false
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        
        viewModel.progress.observe(this) { newProgress ->
            if (!activityPlay.playSeekBar.isUserTouched) {
                activityPlay.playSeekBar.progress = newProgress
            }
        }
        viewModel.state.observe(this) { newState ->
            activityPlay.floatingActionButton.apply {
                when (newState) {
                    STATE_PLAYING -> {
                        setImageResource(R.drawable.ani_play_to_pause)
                        (drawable as AnimatedVectorDrawable).start()
                    }
                    STATE_PAUSED -> {
                        setImageResource(R.drawable.ani_pause_to_play)
                        (drawable as AnimatedVectorDrawable).start()
                    }
                }
            }
        }
        
        activityPlay.playSeekBar.setOnSeekChangeListener { progress, isUser, isReleased ->
            activityPlay.durationView.duration = progress
            if (isUser && isReleased) {
                mediaControllerCompat.transportControls.seekTo(progress)
            }
        }
        
        activityPlay.floatingActionButton.setOnClickListener {
            when (viewModel.stateValue) {
                STATE_PLAYING -> mediaControllerCompat.transportControls.pause()
                STATE_PAUSED -> mediaControllerCompat.transportControls.play()
            }
        }
        
        activityPlay.imageButtonNext.setOnClickListener { mediaControllerCompat.transportControls.skipToNext() }
        activityPlay.imageButtonPrev.setOnClickListener { mediaControllerCompat.transportControls.skipToPrevious() }
    }
    
    override fun onMediaBrowserConnected() {
        if (mediaBrowserCompat.isConnected) {
            registerMediaController()
    
            mediaBrowserCompat.sendCustomAction(Constants.ACTION_REQUEST_STATUS, null, object : MediaBrowserCompat.CustomActionCallback() {
                override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                    resultData?:return
                    isPlaying = false
                    job?.cancel()
                    val audioInfo = (resultData.getSerializable(EXTRAS_AUDIO_INFO) as AudioInfo?) ?: return
                    activityPlay.playSeekBar.max = audioInfo.audioDuration
                    viewModel.updateProgress(resultData.getInt(Constants.EXTRAS_PROGRESS).toLong())
                    viewModel.updateState(resultData.getInt(EXTRAS_STATUS))
                    when (viewModel.stateValue) {
                        STATE_PLAYING -> {
                            activityPlay.floatingActionButton.setImageResource(R.drawable.ic_pause)
                            isPlaying = true
                            job = getProgressSyncJob(activityPlay.playSeekBar.progress)
                        }
                        STATE_PAUSED -> activityPlay.floatingActionButton.setImageResource(R.drawable.ic_play)
                    }
                }
            })
        }
    }
    
    @Suppress("DuplicatedCode")
    private fun getProgressSyncJob(progress: Long) = CoroutineScope(Dispatchers.IO).launch {
        val currentProgress = delayForCorrection(progress)
        launch(Dispatchers.Main) { viewModel.updateProgress(currentProgress) }
        while (isPlaying) {
            delay1second()
            launch(Dispatchers.Main) { viewModel.updateProgress(viewModel.progressValue + ms_1000_int) }
        }
    }
    
    private suspend fun delayForCorrection(progress: Long): Long {
        val diff = progress % 1000
        if (diff != 0L) {
            delay(diff)
        }
        return progress + diff
    }
    
    override fun onMediaControllerPlaybackStateChanged(state: PlaybackStateCompat?) {
        Log.e(TAG, "MediaControllerCompat.Callback.onPlaybackStateChanged ${state?.state}")
        state ?: return
        viewModel.updateState(state.state)
        when (state.state) {
            STATE_PLAYING -> {
                isPlaying = true
                job = getProgressSyncJob(state.position)
            }
            STATE_PAUSED -> {
                isPlaying = false
                job?.cancel()
            }
            STATE_BUFFERING -> {
                isPlaying = false
                job?.cancel()
            }
            else -> Unit
        }
    }
    
    override fun onMediaControllerMetadataChanged(metadata: MediaMetadataCompat?) {
    }
    
    override fun onPause() {
        isPlaying = false
        job?.cancel()
        Log.e(TAG, "onPause")
        super.onPause()
    }
    
    override fun onResume() {
        Log.e(TAG, "onResume")
        super.onResume()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _activityPlayBinding = null
    }
    
}