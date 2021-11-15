package sakuraba.saki.player.music

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Instrumentation
import android.bluetooth.BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED
import android.content.Intent.ACTION_HEADSET_PLUG
import android.graphics.Color.BLACK
import android.graphics.Color.TRANSPARENT
import android.graphics.Color.WHITE
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_SETTLING
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.android.content.broadcastReceiver
import lib.github1552980358.ktExtension.android.content.getStatusBarHeight
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.android.content.register
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import mkaflowski.mediastylepalette.MediaNotificationProcessor
import sakuraba.saki.player.music.base.BaseMediaControlActivity
import sakuraba.saki.player.music.databinding.ActivityPlayBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.ui.play.util.RecyclerViewAdapter
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.Constants
import sakuraba.saki.player.music.util.Constants.ACTION_EXTRA
import sakuraba.saki.player.music.util.Constants.ACTION_REQUEST_AUDIO_LIST
import sakuraba.saki.player.music.util.Constants.ACTION_UPDATE_PLAY_MODE
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_PLAY_MODE
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_LIST
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_RANDOM
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_SINGLE
import sakuraba.saki.player.music.util.Constants.PLAY_MODE_SINGLE_CYCLE
import sakuraba.saki.player.music.util.CoroutineUtil.delay1second
import sakuraba.saki.player.music.util.CoroutineUtil.ms_1000_int
import sakuraba.saki.player.music.util.SystemUtil.pixelHeight
import sakuraba.saki.player.music.ui.play.util.DividerItemDecoration
import sakuraba.saki.player.music.util.AudioUtil
import sakuraba.saki.player.music.util.AudioUtil.getOutputDevice
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.LifeStateConstant.ON_BACK_PRESSED
import sakuraba.saki.player.music.util.SystemUtil.navigationBarHeight

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
    
    @Volatile
    private var activityBackgroundColor = TRANSPARENT
    @Volatile
    private var seekbarBackgroundColor = TRANSPARENT
    @Volatile
    private var progressColor = TRANSPARENT
    
    private lateinit var playModeListCycle: Drawable
    private val playModeSingleCycle by lazy { ContextCompat.getDrawable(this, R.drawable.ic_single_cycle) }
    private val playModeRandom by lazy { ContextCompat.getDrawable(this, R.drawable.ic_random) }
    private val playModeSingle by lazy { ContextCompat.getDrawable(this, R.drawable.ic_single) }
    
    private var _recyclerView: RecyclerView? = null
    private val recyclerView get() = _recyclerView!!
    private var _imageViewDevice: ImageView? = null
    private val imageViewDevice get() = _imageViewDevice!!

    private lateinit var audioInfo: AudioInfo

    private lateinit var audioManager: AudioManager

    private val broadcastReceiver = broadcastReceiver { _, intent, _ ->
        when (intent?.action) {
            ACTION_HEADSET_PLUG, ACTION_CONNECTION_STATE_CHANGED -> updateAudioDeviceIcon()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = TRANSPARENT
        
        super.onCreate(savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        _activityPlayBinding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(activityPlay.root)
        
        setSupportActionBar(activityPlay.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activityPlay.toolbar.setNavigationOnClickListener { onBackPressed() }
        
        activityPlay.imageView.layoutParams = activityPlay.imageView.layoutParams.apply { height = resources.displayMetrics.widthPixels }
        activityPlay.lyricLayout.apply {
            layoutParams = layoutParams.apply { height = resources.displayMetrics.widthPixels }
        }
        
        _textViewTitle = findViewById(R.id.text_view_title)
        _textViewSummary = findViewById(R.id.text_view_summary)
    
        playModeListCycle = activityPlay.imageButtonPlayMode.drawable
        viewModel.updatePlayMode(PLAY_MODE_LIST)

        io {
            val audioInfo = intent?.getSerializableExtra(EXTRAS_AUDIO_INFO) as AudioInfo? ?: return@io
            ui {
                textViewTitle.text = audioInfo.audioTitle
                @Suppress("SetTextI18n")
                textViewSummary.text = "${audioInfo.audioArtist} - ${audioInfo.audioAlbum}"
            }
            val bitmap = tryRun { loadAlbumArt(audioInfo.audioAlbumId) } ?: ContextCompat.getDrawable(this@PlayActivity, R.drawable.ic_music)?.toBitmap()
            ui { activityPlay.imageView.setImageBitmap(bitmap) }
            MediaNotificationProcessor(this@PlayActivity, bitmap).getColorUpdated(true)
            activityPlay.lyricLayout.updateBitmap(bitmap)
        }
    
        findViewById<CardView>(R.id.card_view).apply {
            layoutParams = layoutParams.apply {
                height = pixelHeight - getStatusBarHeight()
            }
            setContentPadding(0, 0, 0, navigationBarHeight)
        }
        
        behavior = BottomSheetBehavior.from(findViewById(R.id.card_view))
        behavior.peekHeight =
            pixelHeight - resources.displayMetrics.widthPixels -
                resources.getDimensionPixelSize(R.dimen.play_controller_height) -
                resources.getDimensionPixelSize(R.dimen.play_controller_seekbar_height)
        behavior.isHideable = false
        behavior.state = STATE_COLLAPSED
        
        viewModel.progress.observe(this) { newProgress ->
            if (!activityPlay.playSeekBar.isUserTouched) {
                activityPlay.playSeekBar.progress = newProgress
            }
            activityPlay.lyricLayout.updatePosition(newProgress)
        }
        viewModel.duration.observe(this) { newDuration ->
            activityPlay.playSeekBar.max = newDuration
            activityPlay.durationViewDuration.duration = newDuration
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
            activityPlay.durationViewProgress.duration = progress
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
        
        activityPlay.imageView.setOnClickListener {
            when (activityPlay.relativeLayoutToolbarRoot.visibility) {
                GONE -> {
                    activityPlay.relativeLayoutToolbarRoot.apply {
                        animate()
                            .alpha(1F)
                            .setDuration(500)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(p0: Animator?) {
                                    alpha = 0F
                                    visibility = VISIBLE
                                }
                            })
                            .start()
                    }
                }
                VISIBLE -> {
                    activityPlay.relativeLayoutToolbarRoot.animate()
                        .alpha(0F)
                        .setDuration(500)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                activityPlay.relativeLayoutToolbarRoot.visibility = GONE
                            }
                        })
                        .start()
                }
                INVISIBLE -> activityPlay.relativeLayoutToolbarRoot.visibility = GONE
            }
        }
        
        activityPlay.imageButtonPlayMode.setOnClickListener {
            mediaBrowserCompat.sendCustomAction(
                ACTION_UPDATE_PLAY_MODE,
                bundle {
                    putInt(EXTRAS_PLAY_MODE,
                        when (viewModel.playModeValue) {
                            PLAY_MODE_LIST -> PLAY_MODE_SINGLE_CYCLE
                            PLAY_MODE_SINGLE_CYCLE -> PLAY_MODE_RANDOM
                            PLAY_MODE_RANDOM -> PLAY_MODE_SINGLE
                            PLAY_MODE_SINGLE -> PLAY_MODE_LIST
                            else -> PLAY_MODE_LIST
                        }
                    ) },
                object : MediaBrowserCompat.CustomActionCallback() {
                    override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                        resultData ?: return
                        @Suppress("UNCHECKED_CAST")
                        (recyclerView.adapter as RecyclerViewAdapter).resetAudioAudioList(resultData.getSerializable(EXTRAS_AUDIO_INFO_LIST) as ArrayList<AudioInfo>)
                    }
                }
            )
        }
        
        viewModel.playMode.observe(this) { newPlayMode ->
            activityPlay.imageButtonPlayMode.setImageDrawable(
                when (newPlayMode) {
                    PLAY_MODE_LIST -> playModeListCycle
                    PLAY_MODE_SINGLE_CYCLE -> playModeSingleCycle
                    PLAY_MODE_RANDOM -> playModeRandom
                    PLAY_MODE_SINGLE -> playModeSingle
                    else -> playModeListCycle
                }
            )
            activityPlay.imageButtonPlayMode.drawable.setTint(if (viewModel.isLightBackground.value == true) BLACK else WHITE)
        }

        activityPlay.imageButtonLyric.setOnClickListener {
            activityPlay.imageButtonLyric.setImageResource(
                if (activityPlay.lyricLayout.updateVisibility()) R.drawable.ic_lyric_enabled else R.drawable.ic_lyric
            )
            activityPlay.imageButtonLyric.drawable?.setTint(if (viewModel.isLightBackground.value == true) BLACK else WHITE)
        }
        _recyclerView = findViewById(R.id.recycler_view)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this, VERTICAL, false)
        recyclerView.adapter = RecyclerViewAdapter(recyclerView) { pos ->
            mediaControllerCompat.transportControls.skipToQueueItem(pos.toLong())
        }
        recyclerView.addItemDecoration(DividerItemDecoration())
        
        findViewById<RelativeLayout>(R.id.relative_layout_audio_info).setOnClickListener {
            if (behavior.state != STATE_EXPANDED) {
                behavior.state = STATE_EXPANDED
            }
        }

        audioManager = (getSystemService(AUDIO_SERVICE) as AudioManager)

        _imageViewDevice = findViewById(R.id.image_view_device)

        updateAudioDeviceIcon()
        broadcastReceiver.register(this, arrayOf(ACTION_HEADSET_PLUG, ACTION_CONNECTION_STATE_CHANGED))
    }

    private fun updateAudioDeviceIcon() = imageViewDevice.run {
        when (audioManager.getOutputDevice) {
            AudioUtil.AudioDevice.HEADSET, AudioUtil.AudioDevice.HEADPHONE
                , AudioUtil.AudioDevice.USB_DEVICE -> { setImageResource(R.drawable.ic_headset) }
            AudioUtil.AudioDevice.BLUETOOTH_A2DP -> { setImageResource(R.drawable.ic_bluetooth) }
            else -> setImageResource(R.drawable.ic_speaker)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_play, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_details -> {
                startActivity(intent(this, AudioDetailActivity::class.java) {
                    putExtra(EXTRAS_DATA, audioInfo)
                    this@PlayActivity::class.java.simpleName.apply {
                        putExtra(this, this)
                    }
                })
                overridePendingTransition(R.anim.translate_up_enter, R.anim.translate_up_exit)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onMediaBrowserConnected() {
        if (mediaBrowserCompat.isConnected) {
            registerMediaController()
    
            io {
                mediaBrowserCompat.sendCustomAction(Constants.ACTION_REQUEST_STATUS, null, object : MediaBrowserCompat.CustomActionCallback() {
                    override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                        resultData?:return
                        isPlaying = false
                        job?.cancel()
                        audioInfo = (resultData.getSerializable(EXTRAS_AUDIO_INFO) as AudioInfo?) ?: return
                        activityPlay.lyricLayout.updateLyric(audioInfo.audioId)
                        viewModel.updateDuration(audioInfo.audioDuration)
                        viewModel.updateProgress(resultData.getLong(Constants.EXTRAS_PROGRESS))
                        viewModel.updateState(resultData.getInt(EXTRAS_STATUS))
                        viewModel.updatePlayMode(resultData.getInt(EXTRAS_PLAY_MODE))
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
    }
    
    @Suppress("DuplicatedCode")
    private fun getProgressSyncJob(progress: Long) = io {
        val currentProgress = delayForCorrection(progress)
        ui { viewModel.updateProgress(currentProgress) }
        while (isPlaying) {
            delay1second()
            ui { viewModel.updateProgress(viewModel.progressValue + ms_1000_int) }
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
                job?.cancel()
                if (!isPlaying) {
                    isPlaying = true
                }
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
        viewModel.updatePlayMode(state.extras?.getInt(EXTRAS_PLAY_MODE, PLAY_MODE_LIST) ?: PLAY_MODE_LIST)
    }
    
    override fun onMediaControllerMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata ?: return
        io {
            val bitmap = tryRun { loadAlbumArt(metadata.getString(METADATA_KEY_ALBUM_ART_URI)) }
                ?: ContextCompat.getDrawable(this@PlayActivity, R.drawable.ic_music)?.toBitmap()
            ui { activityPlay.imageView.setImageBitmap(bitmap) }
            MediaNotificationProcessor(this@PlayActivity, bitmap).getColorUpdated(false)
            activityPlay.lyricLayout.updateBitmap(bitmap)
        }
        viewModel.updateDuration(metadata.getLong(METADATA_KEY_DURATION))

        ValueAnimator.ofArgb(BLACK, WHITE).apply {
            duration = 400
            addUpdateListener {
                textViewTitle.setTextColor(animatedValue as Int)
                textViewSummary.setTextColor(animatedValue as Int)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    textViewTitle.text = metadata.getString(METADATA_KEY_TITLE)
                    textViewSummary.text = metadata.getString(METADATA_KEY_ARTIST)
                    ValueAnimator.ofArgb(WHITE, BLACK).apply {
                        duration = 400
                        addUpdateListener {
                            textViewTitle.setTextColor(animatedValue as Int)
                            textViewSummary.setTextColor(animatedValue as Int)
                        }
                        start()
                    }
                }
            })
            start()
        }
        mediaBrowserCompat.sendCustomAction(ACTION_REQUEST_AUDIO_LIST, null, object : MediaBrowserCompat.CustomActionCallback() {
            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                resultData ?: return
                @Suppress("UNCHECKED_CAST")
                val list = resultData.getSerializable(ACTION_EXTRA) as MutableList<AudioInfo>? ?: return
                (recyclerView.adapter as RecyclerViewAdapter).updateAudioInfoList(list)
                audioInfo = list.last()
                activityPlay.lyricLayout.updateLyric(audioInfo.audioId)
            }
        })
    }
    
    override fun onSubscriptionChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
        (recyclerView.adapter as RecyclerViewAdapter).updateMediaItemList(children)
    }
    
    private fun MediaNotificationProcessor.getColorUpdated(isInit: Boolean) {
        if (activityBackgroundColor != backgroundColor) {
            ValueAnimator.ofArgb(activityBackgroundColor, backgroundColor).apply {
                duration = 500
                addUpdateListener {
                    activityPlay.root.setBackgroundColor(animatedValue as Int)
                }
                ui { start() }
            }
            activityBackgroundColor = backgroundColor
        }
        if (seekbarBackgroundColor != primaryTextColor) {
            ValueAnimator.ofArgb(seekbarBackgroundColor, primaryTextColor).apply {
                duration = 500
                addUpdateListener {
                    activityPlay.playSeekBar.setProgressColor(animatedValue as Int)
                    activityPlay.durationViewProgress.updateTextColor(animatedValue as Int)
                    activityPlay.lyricLayout.updatePrimaryColor(animatedValue as Int)
                }
                ui { start() }
            }
            seekbarBackgroundColor = primaryTextColor
        }
        if (progressColor != secondaryTextColor) {
            ValueAnimator.ofArgb(progressColor, secondaryTextColor).apply {
                duration = 500
                addUpdateListener {
                    activityPlay.playSeekBar.setRemainColor(animatedValue as Int)
                    activityPlay.durationViewDuration.updateTextColor(animatedValue as Int)
                    activityPlay.lyricLayout.updateSecondaryColor(animatedValue as Int)
                }
                ui { start() }
            }
            progressColor = secondaryTextColor
        }
        ui { viewModel.setIsLightBackground(isLight) }
        if (isInit) {
            ui {
                updateControlButtonColor(if (isLight) BLACK else WHITE)
                viewModel.isLightBackground.observe(this@PlayActivity) { isLight ->
                    if (isLight) { ValueAnimator.ofArgb(WHITE, BLACK) } else { ValueAnimator.ofArgb(BLACK, WHITE) }.apply {
                        duration = 500
                        addUpdateListener {
                            updateControlButtonColor(animatedValue as Int)
                        }
                    }.start()
                }
            }
        }
    }

    override fun onBackPressed() {
        Log.e(TAG, ON_BACK_PRESSED)
        when (behavior.state) {
            STATE_EXPANDED, STATE_HALF_EXPANDED, STATE_DRAGGING, STATE_SETTLING -> behavior.state = STATE_COLLAPSED
            else -> finishAfterTransition()
        }
    }
    
    private fun updateControlButtonColor(@ColorInt newColor: Int) {
        activityPlay.imageButtonNext.drawable.setTint(newColor)
        activityPlay.imageButtonPrev.drawable.setTint(newColor)
        activityPlay.imageButtonPlayMode.drawable.setTint(newColor)
        activityPlay.imageButtonLyric.drawable.setTint(newColor)
    }
    
    override fun getParentID() = TAG
    
    override fun onPause() {
        isPlaying = false
        job?.cancel()
        super.onPause()
    }

    override fun onStop() {
        /**
         * Solution token from
         * https://stackoverflow.com/a/60506947/11685230
         **/
        Instrumentation().callActivityOnSaveInstanceState(this, Bundle())
        super.onStop()
    }
    
    override fun onResume() {
        super.onResume()
    
        if (mediaBrowserCompat.isConnected) {
            mediaBrowserCompat.sendCustomAction(Constants.ACTION_REQUEST_STATUS, null, object : MediaBrowserCompat.CustomActionCallback() {
                override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                    Log.e(TAG, "onResult ${resultData == null}")
                    resultData?:return
                    isPlaying = false
                    job?.cancel()
                    audioInfo = (resultData.getSerializable(EXTRAS_AUDIO_INFO) as AudioInfo?) ?: return
                    activityPlay.lyricLayout.updateLyric(audioInfo.audioId)
                    viewModel.updateDuration(audioInfo.audioDuration)
                    viewModel.updateProgress(resultData.getLong(Constants.EXTRAS_PROGRESS))
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
    
    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        activityPlay.lyricLayout.unregisterBroadcastReceiver()
        super.onDestroy()
        _activityPlayBinding = null
    }
    
}