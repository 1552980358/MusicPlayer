package sakuraba.saki.player.music

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.Intent.CATEGORY_HOME
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.android.view.getDimensionPixelSize
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.makeSnack
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import lib.github1552980358.ktExtension.jvm.util.copy
import sakuraba.saki.player.music.BuildConfig.APPLICATION_ID
import sakuraba.saki.player.music.base.BaseMediaControlActivity
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.database.AudioDatabaseHelper.Companion.TABLE_ALBUM
import sakuraba.saki.player.music.database.AudioDatabaseHelper.Companion.TABLE_AUDIO
import sakuraba.saki.player.music.databinding.ActivityMainBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.MainActivityInterface
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.base.BasePreferenceFragmentCompat
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArtRaw
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArts40Dp
import sakuraba.saki.player.music.util.BitmapUtil.loadAudioArt40Dp
import sakuraba.saki.player.music.util.BitmapUtil.writeAlbumArt40Dp
import sakuraba.saki.player.music.util.BitmapUtil.writeAlbumArtRaw
import sakuraba.saki.player.music.util.Constants.ACTION_REQUEST_STATUS
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.Constants.EXTRAS_PROGRESS
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Constants.TRANSITION_IMAGE_VIEW
import sakuraba.saki.player.music.util.Constants.TRANSITION_TEXT_VIEW
import sakuraba.saki.player.music.util.CoroutineUtil.delay1second
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ms_1000_int
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.MediaAlbum
import sakuraba.saki.player.music.util.SettingUtil.getBooleanSetting
import sakuraba.saki.player.music.util.SettingUtil.getIntSettingOrThrow
import sakuraba.saki.player.music.widget.PlayProgressBar

class MainActivity: BaseMediaControlActivity() {
    
    companion object {
        const val TAG = "MainActivity"
    }
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private var _activityMainMainBinding: ActivityMainBinding? = null
    private val activityMain get() = _activityMainMainBinding!!
    private lateinit var viewModel: MainViewModel
    
    private lateinit var behavior: BottomSheetBehavior<RelativeLayout>

    private lateinit var activityInterface: MainActivityInterface
    
    private var isOnPaused = false
    
    private var _imageView: ImageView? = null
    private val imageView get() = _imageView!!
    private var _imageButton: ImageButton? = null
    private val imageButton get() = _imageButton!!
    private var _textView: TextView? = null
    private val textView get() = _textView!!
    private var _playProgressBar: PlayProgressBar? = null
    private val playProgressBar get() = _playProgressBar!!
    private var _coordinatorLayout: CoordinatorLayout? = null
    private val coordinatorLayout get() = _coordinatorLayout!!
    
    private var bottomSheetClickLock = true
    
    private val fragmentLifecycleCallbacks =  object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
            Log.e(f::class.java.simpleName, "onFragmentAttached")
        }
        override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            when (f) {
                is BaseMainFragment -> f.activityInterface = activityInterface
                is BasePreferenceFragmentCompat -> f.activityInterface = activityInterface
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

    private lateinit var snackBar: Snackbar

    private lateinit var mediaStoreObserver: ContentObserver
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        
        activityInterface = MainActivityInterface { pos, audioInfo, audioInfoList ->
            mediaControllerCompat.transportControls.playFromMediaId(audioInfo?.audioId, bundle {
                putInt(EXTRAS_AUDIO_INFO_POS, pos)
                putSerializable(EXTRAS_AUDIO_INFO, audioInfo)
                putSerializable(EXTRAS_AUDIO_INFO_LIST, audioInfoList)
            })
            if (audioInfo != null) {
                this.audioInfo = audioInfo
                playProgressBar.max = audioInfo.audioDuration
                textView.text = audioInfo.audioTitle
                imageView.setImageBitmap(
                    activityInterface.audioBitmapMap[audioInfo.audioId]
                        ?: activityInterface.bitmapMap[audioInfo.audioAlbumId]
                        ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
                )
            }
        }
        
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
                    intent(this, PlayActivity::class.java) {
                        putExtra(EXTRAS_DATA, activityInterface.byteArrayMap[audioInfo.audioAlbumId])
                        putExtra(EXTRAS_AUDIO_INFO, audioInfo)
                    },
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

        _coordinatorLayout = findViewById(R.id.coordinator_layout)

        audioDatabaseHelper = AudioDatabaseHelper(this)

        snackBar = coordinatorLayout.makeSnack(R.string.main_snack_open_setting_text, LENGTH_INDEFINITE)
            .setAction(R.string.main_snack_open_setting_button) {
                startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS, fromParts("package", APPLICATION_ID, null)))
            }

        mediaStoreObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                activityInterface.onContentChange()
                io {
                    audioDatabaseHelper.clearTable(TABLE_AUDIO)
                    audioDatabaseHelper.clearTable(TABLE_AUDIO)
                    activityInterface.clearLists()
                    initLaunchProcess()
                }
            }
        }

        activityInterface.setRequestRefreshListener {
            io {
                audioDatabaseHelper.clearTable(TABLE_AUDIO)
                audioDatabaseHelper.clearTable(TABLE_ALBUM)
                activityInterface.clearLists()
                initLaunchProcess()
            }
        }

        contentResolver.apply {
            registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
            registerContentObserver(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
            registerContentObserver(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
            registerContentObserver(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true, mediaStoreObserver)
        }

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            when {
                it -> io { initLaunchProcess() }
                else -> snackBar.show()
            }
        }

        if (ActivityCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermission.launch(READ_EXTERNAL_STORAGE)
            return
        }
        io { launchProcess() }
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
            var data: String?
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
                @Suppress("DEPRECATION")
                data = getStringOrNull(getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                if (id != null && title != null && artist != null && album != null && albumId != null && duration != null && size != null && data != null) {
                    activityInterface.audioInfoFullList.add(
                        AudioInfo(
                            id,
                            title,
                            artist,
                            album,
                            albumId,
                            duration,
                            size,
                            data
                        )
                    )
                }
            }
            close()
        }

        if (activityInterface.audioInfoFullList.isNotEmpty()) {
            activityInterface.audioInfoFullList.forEach { Log.e("TAG", it.audioTitle) }
            activityInterface.audioInfoFullList.sortBy { it.audioId }
            audioDatabaseHelper.insertAudio(TABLE_AUDIO, activityInterface.audioInfoFullList)
            activityInterface.audioInfoFullList.forEach { audioInfo ->
                when (val index =
                        activityInterface.albumList.indexOfFirst { mediaAlbum -> mediaAlbum.albumId == audioInfo.audioAlbumId }) {
                    -1 -> activityInterface.albumList.add(
                        MediaAlbum(
                            audioInfo.audioAlbumId,
                            audioInfo.audioAlbum,
                            audioInfo.audioAlbumPinyin
                        )
                    )
                    else -> activityInterface.albumList[index].numberOfAudio++
                }
            }
            activityInterface.albumList.sortBy { mediaAlbum -> mediaAlbum.albumId }
            audioDatabaseHelper.insertMediaAlbum(TABLE_ALBUM, activityInterface.albumList)

            activityInterface.audioInfoList = activityInterface.audioInfoFullList.copy().apply {
                if (getBooleanSetting(R.string.key_audio_filter_size_enable)) {
                    tryOnly { removeAll { audioInfo -> audioInfo.audioSize < getIntSettingOrThrow(R.string.key_audio_filter_size_value) } }
                }
                if (getBooleanSetting(R.string.key_audio_filter_duration_enable)) {
                    tryOnly { removeAll { audioInfo -> audioInfo.audioDuration < getIntSettingOrThrow(R.string.key_audio_filter_duration_value) } }
                }
                sortBy { it.audioTitlePinyin }
                forEachIndexed { index, audioInfo -> audioInfo.index = index }
            }
            ui { activityInterface.onLoadStageChange() }

            var bitmap: Bitmap?
            val matrix = Matrix()
            val bitmapSize = resources.getDimensionPixelSize(R.dimen.home_recycler_view_image_view_size)
            activityInterface.audioInfoFullList.forEach { audioInfo ->
                if (!activityInterface.bitmapMap.containsKey(audioInfo.audioAlbumId)) {
                    tryOnly {
                        bitmap = null
                        bitmap = loadAlbumArt(audioInfo.audioAlbumId)
                        activityInterface.byteArrayMap[audioInfo.audioAlbumId] = writeAlbumArtRaw(audioInfo.audioAlbumId, bitmap)!!
                        matrix.apply {
                            setScale(bitmapSize / bitmap!!.widthF, bitmapSize / bitmap!!.heightF)
                        }
                        bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap!!.width, bitmap!!.height, matrix, false)
                        writeAlbumArt40Dp(audioInfo.audioAlbumId, bitmap)
                        activityInterface.bitmapMap[audioInfo.audioAlbumId] = bitmap
                    }
                }
            }
        }
        loadAudioArt40Dp(activityInterface.audioBitmapMap)
        ui { activityInterface.onCompleteLoading() }
    }

    private fun launchProcess() {
        activityInterface.audioInfoList = activityInterface.audioInfoFullList.run {
                clear()
                audioDatabaseHelper.queryAllAudio(this)
                copy()
        }.apply {
            if (getBooleanSetting(R.string.key_audio_filter_size_enable)) {
                tryOnly { removeAll { audioInfo -> audioInfo.audioSize < getIntSettingOrThrow(R.string.key_audio_filter_size_value) } }
            }
            if (getBooleanSetting(R.string.key_audio_filter_duration_enable)) {
                tryOnly { removeAll { audioInfo -> audioInfo.audioDuration < getIntSettingOrThrow(R.string.key_audio_filter_duration_value) } }
            }
            sortBy { it.audioTitlePinyin }
            forEachIndexed { index, audioInfo -> audioInfo.index = index }
        }

        ui { activityInterface.onLoadStageChange() }

        loadAudioArt40Dp(activityInterface.audioBitmapMap)
        loadAlbumArts40Dp(activityInterface.bitmapMap)
        ui { activityInterface.onCompleteLoading() }
        activityInterface.refreshCompleted = true

        loadAlbumArtRaw(activityInterface.byteArrayMap)
        audioDatabaseHelper.queryMediaAlbum(activityInterface.albumList)
    }
    
    override fun onMediaBrowserConnected() {
        Log.e(TAG, "MediaBrowserCompat.ConnectionCallback.onConnected")
        if (mediaBrowserCompat.isConnected) {
            registerMediaController()
            activityInterface.setMediaBrowserCompat(mediaBrowserCompat)
    
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
                        io {
                            val bitmap =
                                activityInterface.audioBitmapMap[audioInfo.audioId]
                                    ?: activityInterface.bitmapMap[audioInfo.audioAlbumId]
                                    ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
                            ui { imageView.setImageBitmap(bitmap) }
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
        io {
            val bitmap =
                activityInterface.audioBitmapMap[metadata.getString(METADATA_KEY_MEDIA_ID)]
                        ?: activityInterface.bitmapMap[metadata.getString(METADATA_KEY_ALBUM_ART_URI).toLong()]
                        ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
            ui { imageView.setImageBitmap(bitmap) }
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

        if (snackBar.isShown && ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            snackBar.dismiss()
        }

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
                    io {
                        val bitmap =
                            activityInterface.audioBitmapMap[audioInfo.audioId]
                                ?: activityInterface.bitmapMap[audioInfo.audioAlbumId]
                                ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)?.toBitmap()
                        ui { imageView.setImageBitmap(bitmap) }
                    }
                }
            })
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