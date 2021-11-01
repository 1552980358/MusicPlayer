package sakuraba.saki.player.music

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent.ACTION_MAIN
import android.content.Intent.CATEGORY_HOME
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.android.view.getDimensionPixelSize
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.jvm.util.copy
import sakuraba.saki.player.music.base.BaseMediaControlActivity
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.database.AudioDatabaseHelper.Companion.TABLE_ALBUM
import sakuraba.saki.player.music.database.AudioDatabaseHelper.Companion.TABLE_AUDIO
import sakuraba.saki.player.music.databinding.ActivityMainBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.MainFragmentData
import sakuraba.saki.player.music.util.ActivityFragmentInterface
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.Constants.ACTION_REQUEST_STATUS
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS
import sakuraba.saki.player.music.util.Constants.EXTRAS_PROGRESS
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Constants.TRANSITION_IMAGE_VIEW
import sakuraba.saki.player.music.util.Constants.TRANSITION_TEXT_VIEW
import sakuraba.saki.player.music.util.Coroutine.delay1second
import sakuraba.saki.player.music.util.Coroutine.ms_1000_int
import sakuraba.saki.player.music.util.MediaAlbum
import sakuraba.saki.player.music.widget.PlayProgressBar

class MainActivity: BaseMediaControlActivity() {
    
    companion object {
        const val TAG = "MainActivity"
        const val INTENT_ACTIVITY_FRAGMENT_INTERFACE = "ActivityFragmentInterface"
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
    private var _playProgressBar: PlayProgressBar? = null
    private val playProgressBar get() = _playProgressBar!!
    
    private var bottomSheetClickLock = true

    private var mainFragmentData = MainFragmentData()
    
    private val fragmentLifecycleCallbacks =  object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            Log.e(f::class.java.simpleName, "onFragmentAttached")
        }
        override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            if (f is BaseMainFragment) {
                f.mainFragmentData = mainFragmentData
            }
        }
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            Log.e(f::class.java.simpleName, "onFragmentCreated")
        }
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            Log.e(f::class.java.simpleName, "onFragmentViewCreated")
        }
        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentStarted")
        }
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentResumed")
        }
        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentPaused")
        }
        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentStopped")
        }
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentViewDestroyed")
        }
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentDestroyed")
        }
        override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
            Log.e(f::class.java.simpleName, "onFragmentDetached")
        }
    }
    
    private lateinit var navController: NavController
    
    private var job: Job? = null
    private var isPlaying = false
    
    // private var playBackState = STATE_NONE
    
    private lateinit var audioInfo: AudioInfo

    private lateinit var requestPermission: ActivityResultLauncher<String>

    private lateinit var audioDatabaseHelper: AudioDatabaseHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        
        activityFragmentInterface = ActivityFragmentInterface { pos, audioInfo, audioInfoList ->
            mediaControllerCompat.transportControls.playFromMediaId(audioInfo?.audioId, bundle {
                putInt(EXTRAS_AUDIO_INFO_POS, pos)
                putSerializable(EXTRAS_AUDIO_INFO, audioInfo)
                putSerializable(EXTRAS_AUDIO_INFO_LIST, audioInfoList)
            })
            if (audioInfo != null) {
                this.audioInfo = audioInfo
                playProgressBar.max = audioInfo.audioDuration
                textView.text = audioInfo.audioTitle
                CoroutineScope(Dispatchers.IO).launch {
                    val bitmap = tryRun { loadAlbumArt(audioInfo.audioAlbumId) } ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
                    launch(Dispatchers.Main) { imageView.setImageBitmap(bitmap) }
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
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_album), activityMain.drawerLayout)
        activityMain.root.findViewById<Toolbar>(R.id.toolbar)?.setupWithNavController(navController, appBarConfiguration)
        activityMain.navView.setupWithNavController(navController)
        
        behavior = BottomSheetBehavior.from(findViewById(R.id.relative_layout_root))
        behavior.peekHeight = 0
        behavior.state = STATE_COLLAPSED
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
                        when (viewModel.stateValue) {
                            STATE_PLAYING -> mediaControllerCompat.transportControls.pause()
                            STATE_PAUSED -> mediaControllerCompat.transportControls.play()
                            else -> Unit
                        }
                    }
                    else -> Unit
                }
            }
        }
    
        _playProgressBar = findViewById(R.id.play_progress_bar)
        viewModel.progress.observe(this) { progress ->
            playProgressBar.progress = progress
        }
        
        findViewById<RelativeLayout>(R.id.relative_layout).setOnClickListener {
            if (bottomSheetClickLock) {
                bottomSheetClickLock = false
                startActivity(
                    intent(this, PlayActivity::class.java) { putExtra(EXTRAS_AUDIO_INFO, audioInfo) },
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair(imageView, TRANSITION_IMAGE_VIEW), Pair(textView, TRANSITION_TEXT_VIEW)).toBundle()
                )
            }
        }
        
        viewModel.state.observe(this) { newState ->
            imageButton.apply {
                setImageResource(if (newState == STATE_PLAYING) R.drawable.ani_play_to_pause else R.drawable.ani_pause_to_play)
                (drawable as AnimatedVectorDrawable).start()
            }
        }

        audioDatabaseHelper = AudioDatabaseHelper(this)

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> CoroutineScope(Dispatchers.IO).launch { initLaunchProcess() }
                else -> Unit
            }
        }

        if (ActivityCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermission.launch(READ_EXTERNAL_STORAGE)
            return
        }
        CoroutineScope(Dispatchers.IO).launch { launchProcess() }
    }

    private fun initLaunchProcess() {
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            MediaStore.Audio.AudioColumns.IS_MUSIC
        )?.apply {
            var id: String?
            var title: String?
            var artist: String?
            var album: String?
            var albumId: Long?
            var duration: Long?
            var size: Long?
            while (moveToNext()) {
                id = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                title = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                @Suppress("InlinedApi")
                artist = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                @Suppress("InlinedApi")
                album = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                albumId = getLongOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                @Suppress("InlinedApi")
                duration = getLongOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.DURATION))
                size = getLongOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.SIZE))
                if (id != null && title != null && artist != null && album != null && albumId != null && duration != null && size != null) {
                    mainFragmentData.audioInfoFullList.add(
                        AudioInfo(
                            id,
                            title,
                            artist,
                            album,
                            albumId,
                            duration,
                            size
                        )
                    )
                }
            }
            close()
        }

        if (mainFragmentData.audioInfoFullList.isNotEmpty()) {
            mainFragmentData.audioInfoFullList.forEach { Log.e("TAG", it.audioTitle) }
            mainFragmentData.audioInfoFullList.sortBy { it.audioId }
            audioDatabaseHelper.insertAudio(TABLE_AUDIO, mainFragmentData.audioInfoFullList)
            val mediaAlbumList = arrayListOf<MediaAlbum>()
            mainFragmentData.audioInfoFullList.forEach { audioInfo ->
                when (val index =
                    mediaAlbumList.indexOfFirst { mediaAlbum -> mediaAlbum.albumId == audioInfo.audioAlbumId }) {
                    -1 -> mediaAlbumList.add(
                        MediaAlbum(
                            audioInfo.audioAlbumId,
                            audioInfo.audioAlbum,
                            audioInfo.audioAlbumPinyin
                        )
                    )
                    else -> mediaAlbumList[index].numberOfAudio++
                }
            }
            mediaAlbumList.sortBy { mediaAlbum -> mediaAlbum.albumId }
            audioDatabaseHelper.insertMediaAlbum(TABLE_ALBUM, mediaAlbumList)

            launchProcess()
        }
    }

    private fun launchProcess() {
        mainFragmentData.audioInfoList = mainFragmentData.audioInfoFullList.run {
            clear()
            audioDatabaseHelper.queryAllAudio(this)
            sortBy { it.audioTitlePinyin }
            copy()
        }

        CoroutineScope(Dispatchers.Main).launch { mainFragmentData.onLoadStageChange() }

        mainFragmentData.bitmapMap.apply {
            clear()

            mainFragmentData.audioInfoFullList.forEach { audioInfo ->
                tryOnly {
                    this[audioInfo.audioAlbumId] = loadAlbumArt(audioInfo.audioAlbumId)
                }
            }
        }
        CoroutineScope(Dispatchers.Main).launch { mainFragmentData.onCompleteLoading() }
        mainFragmentData.refreshCompleted = true

        mainFragmentData.albumList
        audioDatabaseHelper.queryMediaAlbum(mainFragmentData.albumList)
    }
    
    override fun onMediaBrowserConnected() {
        Log.e(TAG, "MediaBrowserCompat.ConnectionCallback.onConnected")
        if (mediaBrowserCompat.isConnected) {
            registerMediaController()
    
            if (behavior.state == STATE_EXPANDED) {
                @Suppress("DuplicatedCode")
                mediaBrowserCompat.sendCustomAction(ACTION_REQUEST_STATUS, null, object : MediaBrowserCompat.CustomActionCallback() {
                    override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                        Log.e(TAG, "onResult ${resultData == null}")
                        resultData ?: return
                        audioInfo = (resultData.getSerializable(EXTRAS_AUDIO_INFO) as AudioInfo?) ?: return
                        val progress = resultData.getLong(EXTRAS_PROGRESS)
                        playProgressBar.max = audioInfo.audioDuration
                        viewModel.updateProgress(progress)
                        // playBackState = resultData.getInt(EXTRAS_STATUS)
                        viewModel.updateNewState(resultData.getInt(EXTRAS_STATUS))
                        when (viewModel.stateValue) {
                            STATE_PLAYING -> {
                                isPlaying = true
                                job = getProgressSyncJob(progress)
                                imageButton.setImageResource(R.drawable.ic_pause)
                            }
                            STATE_PAUSED -> imageButton.setImageResource(R.drawable.ic_play)
                        }
                        textView.text = audioInfo.audioTitle
                        CoroutineScope(Dispatchers.IO).launch {
                            val bitmap = tryRun { loadAlbumArt(audioInfo.audioAlbumId) } ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
                            launch(Dispatchers.Main) { imageView.setImageBitmap(bitmap) }
                        }
                    }
                })
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
        state ?: return
        when (state.state) {
            STATE_PLAYING -> {
                job?.cancel()
                if (!isPlaying) {
                    isPlaying = true
                }
                isPlaying = true
                job = getProgressSyncJob(state.position)
                if (behavior.state == STATE_COLLAPSED) {
                    behavior.state = STATE_EXPANDED
                    behavior.isDraggable = false
                    findViewById<ConstraintLayout>(R.id.constraint_layout).apply {
                        layoutParams = (layoutParams as CoordinatorLayout.LayoutParams).apply {
                            setMargins(0, 0, 0, getDimensionPixelSize(R.dimen.home_bottom_sheet_height))
                        }
                    }
                }
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
        viewModel.updateNewState(state.state)
    }
    
    override fun onMediaControllerMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = tryRun { loadAlbumArt(metadata.getString(METADATA_KEY_ALBUM_ART_URI)) } ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
            launch(Dispatchers.Main) { imageView.setImageBitmap(bitmap) }
        }
        textView.text = metadata.getString(METADATA_KEY_TITLE)
        playProgressBar.max = metadata.getLong(METADATA_KEY_DURATION)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true
    }
    
    override fun onSupportNavigateUp(): Boolean = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    
    override fun getParentID() = TAG
    
    override fun onResume() {
        super.onResume()
        bottomSheetClickLock = true
        if (behavior.state == STATE_EXPANDED && mediaBrowserCompat.isConnected) {
            @Suppress("DuplicatedCode")
            mediaBrowserCompat.sendCustomAction(ACTION_REQUEST_STATUS, null, object : MediaBrowserCompat.CustomActionCallback() {
                override fun onResult(action: String?, extras: Bundle?, resultData: Bundle?) {
                    Log.e(TAG, "onResult ${resultData == null}")
                    resultData ?: return
                    audioInfo = (resultData.getSerializable(EXTRAS_AUDIO_INFO) as AudioInfo?) ?: return
                    val progress = resultData.getLong(EXTRAS_PROGRESS)
                    playProgressBar.max = audioInfo.audioDuration
                    viewModel.updateProgress(progress)
                    // playBackState = resultData.getInt(EXTRAS_STATUS)
                    viewModel.updateNewState(resultData.getInt(EXTRAS_STATUS))
                    when (viewModel.stateValue) {
                        STATE_PLAYING -> {
                            isPlaying = true
                            job = getProgressSyncJob(progress)
                            imageButton.setImageResource(R.drawable.ic_pause)
                        }
                        STATE_PAUSED -> imageButton.setImageResource(R.drawable.ic_play)
                    }
                    textView.text = audioInfo.audioTitle
                    CoroutineScope(Dispatchers.IO).launch {
                        val bitmap = tryRun { loadAlbumArt(audioInfo.audioAlbumId) } ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
                        launch(Dispatchers.Main) { imageView.setImageBitmap(bitmap) }
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
        val diff = progress % 1000L
        if (diff != 0L) {
            delay(diff)
        }
        return progress + diff
    }
    
    override fun onPause() {
        isOnPaused = true
        isPlaying = false
        job?.cancel()
        super.onPause()
    }
    
    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        super.onDestroy()
        _activityMainMainBinding = null
    }
    
    override fun onBackPressed() {
        when (findNavController(R.id.nav_host_fragment).currentDestination?.id) {
            R.id.nav_home, R.id.nav_album -> startActivity(intent(ACTION_MAIN) { flags = FLAG_ACTIVITY_NEW_TASK; addCategory(CATEGORY_HOME) })
            else -> super.onBackPressed()
        }
    }
    
}