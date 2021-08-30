package sakuraba.saki.player.music

import android.graphics.Bitmap
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.android.view.getDimensionPixelSize
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import sakuraba.saki.player.music.base.BaseMediaControlActivity
import sakuraba.saki.player.music.databinding.ActivityMainBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.ui.home.HomeFragment.Companion.INTENT_ACTIVITY_FRAGMENT_INTERFACE
import sakuraba.saki.player.music.util.ActivityFragmentInterface
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.Constants.ACTION_REQUEST_STATUS
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS
import sakuraba.saki.player.music.util.Constants.EXTRAS_PROGRESS
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Coroutine.delay1second
import sakuraba.saki.player.music.util.Coroutine.ms_1000_int

class MainActivity: BaseMediaControlActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private var _activityMainMainBinding: ActivityMainBinding? = null
    private val activityMain get() = _activityMainMainBinding!!
    private lateinit var viewModel: MainViewModel
    
    private lateinit var behavior: BottomSheetBehavior<RelativeLayout>
    
    private lateinit var activityFragmentInterface: ActivityFragmentInterface
    
    private var isOnPaused = false
    
    private var _imageView: ImageView? = null
    private val imageView get() = _imageView!!
    private var _imageButton: ImageButton? = null
    private val imageButton get() = _imageButton!!
    private var _textView: TextView? = null
    private val textView get() = _textView!!
    private var _progressBar: ProgressBar? = null
    private val progressBar get() = _progressBar!!
    
    private lateinit var navController: NavController
    
    private var job: Job? = null
    private var isPlaying = false
    
    private var playBackState = STATE_NONE
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        activityFragmentInterface = ActivityFragmentInterface { pos, audioInfo, audioInfoList ->
            mediaControllerCompat.transportControls.playFromMediaId(audioInfo?.audioId, bundle {
                putInt(EXTRAS_AUDIO_INFO_POS, pos)
                putSerializable(EXTRAS_AUDIO_INFO, audioInfo)
                putSerializable(EXTRAS_AUDIO_INFO_LIST, audioInfoList)
            })
            if (audioInfo != null) {
                progressBar.max = audioInfo.audioDuration.toInt()
                textView.text = audioInfo.audioTitle
                CoroutineScope(Dispatchers.IO).launch {
                    val bitmap: Bitmap? = tryRun { loadAlbumArt(audioInfo.audioAlbumId) }
                    if (bitmap != null) {
                        launch(Dispatchers.Main) { imageView.setImageBitmap(bitmap) }
                    }
                }
            }
        }
    
        intent?.putExtra(INTENT_ACTIVITY_FRAGMENT_INTERFACE, activityFragmentInterface)
        
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        _activityMainMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMain.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home), activityMain.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        activityMain.navView.setupWithNavController(navController)
        
        behavior = BottomSheetBehavior.from(findViewById(R.id.relative_layout_root))
        behavior.isHideable = true
        behavior.state = STATE_HIDDEN
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Log.e(TAG, "BottomSheetBehavior.BottomSheetCallback.onStateChanged $newState")
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.e(TAG, "BottomSheetBehavior.BottomSheetCallback.onSlide $slideOffset")
            }
        })
        
        _textView = findViewById(R.id.text_view)
        
        _imageView = findViewById(R.id.image_view)
        
        _imageButton = findViewById<ImageButton>(R.id.image_button).apply {
            setOnClickListener {
                when (mediaControllerCompat.playbackState.state) {
                    STATE_PLAYING -> mediaControllerCompat.transportControls.pause()
                    STATE_PAUSED -> mediaControllerCompat.transportControls.play()
                    STATE_NONE -> {
                        when (playBackState) {
                            STATE_PLAYING -> mediaControllerCompat.transportControls.pause()
                            STATE_PAUSED -> mediaControllerCompat.transportControls.play()
                            else -> Unit
                        }
                    }
                    else -> Unit
                }
            }
        }
        
        _progressBar = findViewById(R.id.progress_bar)
        viewModel.progress.observe(this) { progress ->
            progressBar.progress = progress
        }
        
        viewModel.state.observe(this) { newState ->
            when (newState) {
                STATE_PLAYING -> imageButton.apply {
                    setImageResource(R.drawable.ani_play_to_pause)
                    (drawable as AnimatedVectorDrawable).start()
                    // }
                    viewModel.updateNewState(STATE_PLAYING)
                }
                STATE_PAUSED -> {
                    imageButton.apply {
                        setImageResource(R.drawable.ani_pause_to_play)
                        (drawable as AnimatedVectorDrawable).start()
                    }
                }
            }
        }
    }
    
    override fun onMediaBrowserConnected() {
        Log.e(TAG, "MediaBrowserCompat.ConnectionCallback.onConnected")
        if (mediaBrowserCompat.isConnected) {
            registerMediaController()
            
            if (isOnPaused) {
                isOnPaused = false
                if (mediaBrowserCompat.isConnected) {
                    if (behavior.state == STATE_EXPANDED) {
                        mediaBrowserCompat.sendCustomAction(ACTION_REQUEST_STATUS, null, object : MediaBrowserCompat.CustomActionCallback() {
                            override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                                Log.e(TAG, "onResult ${resultData == null}")
                                resultData ?: return
                                val audioInfo = (resultData.getSerializable(EXTRAS_AUDIO_INFO) as AudioInfo?) ?: return
                                progressBar.max = audioInfo.audioDuration.toInt()
                                viewModel.updateProgress(resultData.getInt(EXTRAS_PROGRESS))
                                // playBackState = resultData.getInt(EXTRAS_STATUS)
                                viewModel.updateNewState(resultData.getInt(EXTRAS_STATUS))
                                when (viewModel.stateValue) {
                                    STATE_PLAYING -> {
                                        isPlaying = true
                                        job = getProgressSyncJob(progressBar.progress)
                                        imageButton.setImageResource(R.drawable.ic_pause)
                                    }
                                    STATE_PAUSED -> imageButton.setImageResource(R.drawable.ic_play)
                                }
                                textView.text = audioInfo.audioTitle
                                CoroutineScope(Dispatchers.IO).launch {
                                    var bitmap: Bitmap? = null
                                    tryOnly { bitmap = loadAlbumArt(audioInfo.audioAlbumId) }
                                    if (bitmap != null) {
                                        launch(Dispatchers.Main) { imageView.setImageBitmap(bitmap) }
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }
    
    override fun onMediaBrowserConnectionSuspended() {
        Log.e(TAG, "MediaBrowserCompat.ConnectionCallback.onConnectionSuspended")
    }
    
    override fun onMediaBrowserConnectionFailed() {
        Log.e(TAG, "MediaBrowserCompat.ConnectionCallback.onConnectionFailed")
    }
    
    override fun onMediaControllerPlaybackStateChanged(state: PlaybackStateCompat?) {
        Log.e(TAG, "MediaControllerCompat.Callback.onPlaybackStateChanged ${state?.state}")
        when (state?.state) {
            STATE_BUFFERING -> Unit
            STATE_PLAYING -> {
                isPlaying = false
                job?.cancel()
                isPlaying = true
                val progress = state.position.toInt()
                viewModel.updateProgress(progress)
                job = getProgressSyncJob(progress)
                if (behavior.state == STATE_HIDDEN) {
                    behavior.state = STATE_EXPANDED
                    behavior.isHideable = false
                    findViewById<ConstraintLayout>(R.id.constraint_layout).apply {
                        layoutParams = (layoutParams as CoordinatorLayout.LayoutParams).apply {
                            setMargins(0, 0, 0, getDimensionPixelSize(R.dimen.home_bottom_sheet_height))
                        }
                    }
                }
                // imageButton.apply {
                //     setImageResource(R.drawable.ani_play_to_pause)
                //     (drawable as AnimatedVectorDrawable).start()
                // }
                viewModel.updateNewState(STATE_PLAYING)
            }
            STATE_PAUSED -> {
                // imageButton.apply {
                //     setImageResource(R.drawable.ani_pause_to_play)
                //     (drawable as AnimatedVectorDrawable).start()
                // }
                viewModel.updateNewState(STATE_PAUSED)
                isPlaying = false
                job?.cancel()
            }
            else -> Unit
        }
    }
    
    override fun onMediaControllerMetadataChanged(metadata: MediaMetadataCompat?) {
    
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    
    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    
    override fun onResume() {
        Log.e(TAG, "onResume")
        super.onResume()
    }
    
    private fun getProgressSyncJob(progress: Int) = CoroutineScope(Dispatchers.IO).launch {
        var currentProgress = delayForCorrection(progress)
        while (isPlaying) {
            launch(Dispatchers.Main) { viewModel.updateProgress(currentProgress) }
            delay1second()
            currentProgress += ms_1000_int
        }
    }
    
    private suspend fun delayForCorrection(progress: Int): Int {
        val diff = progress % 1000L
        if (diff != 0L) {
            delay(diff)
        }
        return progress + diff.toInt()
    }
    
    override fun onPause() {
        Log.e(TAG, "onPause")
        isOnPaused = true
        super.onPause()
    }
    
    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        _activityMainMainBinding = null
    }
}