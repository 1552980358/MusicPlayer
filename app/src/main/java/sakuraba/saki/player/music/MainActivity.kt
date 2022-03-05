package sakuraba.saki.player.music

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.Intent.CATEGORY_HOME
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Matrix
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
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
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import lib.github1552980358.ktExtension.android.content.intent
import lib.github1552980358.ktExtension.android.graphics.heightF
import lib.github1552980358.ktExtension.android.graphics.toBitmap
import lib.github1552980358.ktExtension.android.graphics.toDrawable
import lib.github1552980358.ktExtension.android.graphics.widthF
import lib.github1552980358.ktExtension.android.os.bundle
import lib.github1552980358.ktExtension.android.view.getDimensionPixelSize
import lib.github1552980358.ktExtension.androidx.coordinatorlayout.widget.makeSnack
import lib.github1552980358.ktExtension.jvm.keyword.tryOnly
import lib.github1552980358.ktExtension.jvm.keyword.tryRun
import lib.github1552980358.ktExtension.jvm.util.addInstance
import lib.github1552980358.ktExtension.kotlinx.coroutines.ioScope
import sakuraba.saki.player.music.BuildConfig.APPLICATION_ID
import sakuraba.saki.player.music.base.BaseMediaControlActivity
import sakuraba.saki.player.music.database.AudioDatabaseHelper
import sakuraba.saki.player.music.databinding.ActivityMainBinding
import sakuraba.saki.player.music.service.util.AudioInfo
import sakuraba.saki.player.music.util.MainActivityInterface
import sakuraba.saki.player.music.base.BaseMainFragment
import sakuraba.saki.player.music.base.BasePreferenceFragmentCompat
import sakuraba.saki.player.music.ui.webDav.webDavDirectory.WebDavDirectoryFragment
import sakuraba.saki.player.music.util.ActivityUtil.setLightNavigationBar
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArt
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArtRaw
import sakuraba.saki.player.music.util.BitmapUtil.loadAlbumArts40Dp
import sakuraba.saki.player.music.util.BitmapUtil.loadAudioArt40Dp
import sakuraba.saki.player.music.util.BitmapUtil.loadPlaylist40Dp
import sakuraba.saki.player.music.util.BitmapUtil.removeAlbumArts
import sakuraba.saki.player.music.util.BitmapUtil.removeAudioArt
import sakuraba.saki.player.music.util.BitmapUtil.writeAlbumArt40Dp
import sakuraba.saki.player.music.util.BitmapUtil.writeAlbumArtRaw
import sakuraba.saki.player.music.util.Constants.ACTION_REQUEST_STATUS
import sakuraba.saki.player.music.util.Constants.ANIMATION_DURATION
import sakuraba.saki.player.music.util.Constants.ANIMATION_DURATION_LONG
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_LIST
import sakuraba.saki.player.music.util.Constants.EXTRAS_AUDIO_INFO_POS
import sakuraba.saki.player.music.util.Constants.EXTRAS_DATA
import sakuraba.saki.player.music.util.Constants.EXTRAS_PROGRESS
import sakuraba.saki.player.music.util.Constants.EXTRAS_STATUS
import sakuraba.saki.player.music.util.Constants.TRANSITION_IMAGE_VIEW
import sakuraba.saki.player.music.util.CoroutineUtil.delay1second
import sakuraba.saki.player.music.util.CoroutineUtil.io
import sakuraba.saki.player.music.util.CoroutineUtil.ms_1000_int
import sakuraba.saki.player.music.util.CoroutineUtil.ui
import sakuraba.saki.player.music.util.MediaAlbum
import sakuraba.saki.player.music.util.SettingUtil.getBooleanSetting
import sakuraba.saki.player.music.util.SettingUtil.getIntSettingOrThrow

class MainActivity: BaseMediaControlActivity() {
    
    private companion object {
        const val TAG = "MainActivity"
    }
    
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private var _activityMainMainBinding: ActivityMainBinding? = null
    private val activityMain get() = _activityMainMainBinding!!
    private lateinit var viewModel: MainViewModel
    
    private lateinit var behavior: BottomSheetBehavior<RelativeLayout>

    private lateinit var activityInterface: MainActivityInterface
    
    private var isOnPaused = false

    private val appBarMain get() = activityMain.appBarMain
    private val contentMain get() = activityMain.appBarMain.contentMain
    private val contentBottomSheet get() = appBarMain.contentBottomSheet

    private val toolbar get() = appBarMain.toolbar

    private val imageView get() = contentBottomSheet.imageView
    private val imageButton get() = contentBottomSheet.imageButton
    private val textView get() = contentBottomSheet.textView
    private val playProgressBar get() = contentBottomSheet.playProgressBar

    private val relativeLayout get() = contentBottomSheet.relativeLayout
    
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

    private val audioDatabaseHelper get() = activityInterface.audioDatabaseHelper

    private lateinit var snackBar: Snackbar

    private lateinit var mediaStoreObserver: ContentObserver

    private var lastDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setLightNavigationBar()
        window.navigationBarColor = WHITE

        super.onCreate(savedInstanceState)

        installSplashScreen()
        
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
        
        activityInterface = MainActivityInterface { pos, audioInfo, audioInfoList ->
            audioInfo?.let { info ->
                this.audioInfo = info
                playProgressBar.max = info.audioDuration
                mediaControllerCompat.transportControls.playFromMediaId(info.audioId, bundle {
                    putInt(EXTRAS_AUDIO_INFO_POS, pos)
                    putSerializable(EXTRAS_AUDIO_INFO, info)
                    putSerializable(EXTRAS_AUDIO_INFO_LIST, audioInfoList)
                })
            }
        }

        activityInterface.setOnArtUpdate { audioInfo ->
            io {
                when (val bitmap = tryRun { loadAudioArt40Dp(audioInfo.audioId) }) {
                    null -> activityInterface.audioBitmapMap.remove(audioInfo.audioId)
                    else -> activityInterface.audioBitmapMap[audioInfo.audioId] = bitmap
                }
                ui { activityInterface.onCompleteLoading() }
            }
        }
        
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _activityMainMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMain.root)
        setSupportActionBar(toolbar)

        /**
         * Token from { @link https://stackoverflow.com/a/59275182/11685230 }
         **/
        navController = contentMain.navHostFragment.getFragment<NavHostFragment>().navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_playlist, R.id.nav_album), activityMain.drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        activityMain.navView.setupWithNavController(navController)
        
        behavior = BottomSheetBehavior.from(contentBottomSheet.root)
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

        imageButton.setOnClickListener {
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

        viewModel.progress.observe(this) { progress ->
            playProgressBar.progress = progress
        }
        val playActivityLauncher = registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                io {
                    loadAudioArt40Dp(activityInterface.audioBitmapMap)
                    loadAlbumArts40Dp(activityInterface.bitmapMap)
                    ui { activityInterface.onCompleteLoading() }
                }
            }
        }

        relativeLayout.setOnClickListener {
            if (bottomSheetClickLock) {
                bottomSheetClickLock = false
                playActivityLauncher.launch(
                    intent(this, PlayActivity::class.java) {
                        putExtra(EXTRAS_DATA, activityInterface.byteArrayMap[audioInfo.audioAlbumId])
                        putExtra(EXTRAS_AUDIO_INFO, audioInfo)
                    },
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this, imageView, TRANSITION_IMAGE_VIEW)
                )
            }
        }
        
        viewModel.state.observe(this) { newState ->
            imageButton.apply {
                setImageResource(if (newState == STATE_PLAYING) R.drawable.ani_play_to_pause else R.drawable.ani_pause_to_play)
                (drawable as AnimatedVectorDrawable).start()
            }
        }

        activityInterface.audioDatabaseHelper = AudioDatabaseHelper(this)

        snackBar = activityMain.appBarMain.root.makeSnack(R.string.main_snack_open_setting_text, LENGTH_INDEFINITE)
            .setAction(R.string.main_snack_open_setting_button) {
                startActivity(Intent(ACTION_APPLICATION_DETAILS_SETTINGS, fromParts("package", APPLICATION_ID, null)))
            }

        mediaStoreObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if (audioDatabaseHelper.hasTask) {
                    return
                }
                io {
                    compareDatabase()
                    activityInterface.clearLists()
                    launchProcess()
                }
            }
        }

        activityInterface.setRequestRefreshListener {
            if (audioDatabaseHelper.hasTask) {
                return@setRequestRefreshListener
            }
            io {
                compareDatabase()
                activityInterface.clearLists()
                launchProcess()
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
                it -> io { initialApplication() }
                else -> snackBar.show()
            }
        }

        when (ActivityCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)) {
            PERMISSION_GRANTED -> io { launchProcess() }
            else -> requestPermission.launch(READ_EXTERNAL_STORAGE)
        }
    }

    private fun scanSystemDatabase() = arrayListOf<AudioInfo>().apply {
        contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC)?.apply {
            while (moveToNext()) {
                tryOnly {
                    addInstance(
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)),
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)),
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)),
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)),
                        getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)),
                        getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)),
                        getLong(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)),
                        getString(getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA))
                    )
                }
            }
            close()
        }
        sortBy { it.audioId }
    }

    private fun analyzeMediaAlbum(audioInfoList: ArrayList<AudioInfo>) = arrayListOf<MediaAlbum>().apply {
        audioInfoList.forEach { audioInfo ->
            when (val index = activityInterface.albumList.indexOfFirst { mediaAlbum -> mediaAlbum.albumId == audioInfo.audioAlbumId }) {
                -1 -> addInstance(audioInfo.audioAlbumId, audioInfo.audioAlbum, audioInfo.audioAlbumPinyin)
                else -> activityInterface.albumList[index].numberOfAudio++
            }
        }
        sortBy { mediaAlbum -> mediaAlbum.albumId }
    }

    private fun initialDatabase() {
        val audioInfoList = scanSystemDatabase().apply {
            audioDatabaseHelper.insertAudio(this)
            activityInterface.audioInfoFullList.addAll(this)
            activityInterface.audioInfoList.also { arrayList ->
                arrayList.addAll(this)
                arrayList.sortBy { it.audioTitlePinyin }
            }
            ui { activityInterface.onLoadStageChange() }
        }
        val albumList = analyzeMediaAlbum(audioInfoList)
        audioDatabaseHelper.insertMediaAlbum(albumList)
        audioDatabaseHelper.writeComplete()
        activityInterface.albumList.addAll(albumList)
    }

    private fun initialCoverImage(albumList: ArrayList<MediaAlbum>) {
        var bitmap: Bitmap?
        val matrix = Matrix()
        val bitmapSize = resources.getDimensionPixelSize(R.dimen.home_recycler_view_image_view_size)
        for (mediaAlbum in albumList) {
            bitmap = tryRun { loadAlbumArt(mediaAlbum.albumId) } ?: continue
            activityInterface.byteArrayMap[mediaAlbum.albumId] = writeAlbumArtRaw(mediaAlbum.albumId, bitmap)!!
            matrix.apply { setScale(bitmapSize / bitmap!!.widthF, bitmapSize / bitmap!!.heightF) }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
            writeAlbumArt40Dp(mediaAlbum.albumId, bitmap)
            activityInterface.bitmapMap[mediaAlbum.albumId] = bitmap
        }
    }

    private fun initialApplication() {
        initialDatabase()
        initialCoverImage(activityInterface.albumList)
        ui { activityInterface.onCompleteLoading() }
    }

    private fun queryDatabase() = arrayListOf<AudioInfo>().apply {
        audioDatabaseHelper.queryAllAudio(this)
    }

    private suspend fun launchProcess() {

        val loadAudioArt40Dp = ioScope.async { loadAudioArt40Dp(activityInterface.audioBitmapMap) }
        loadAudioArt40Dp.start()

        val loadAlbumArts40Dp = ioScope.async { loadAlbumArts40Dp(activityInterface.bitmapMap) }
        loadAlbumArts40Dp.start()

        val loadPlaylist40Dp = ioScope.async { loadPlaylist40Dp(activityInterface.playlistMap) }
        loadPlaylist40Dp.start()

        queryDatabase().apply {
            forEach {
                Log.e("TAG", it.audioTitle)
            }
            activityInterface.audioInfoFullList.addAll(this)
            if (getBooleanSetting(R.string.key_audio_filter_size_enable)) {
                tryOnly { removeAll { audioInfo -> audioInfo.audioSize < getIntSettingOrThrow(R.string.key_audio_filter_size_value) } }
            }
            if (getBooleanSetting(R.string.key_audio_filter_duration_enable)) {
                tryOnly { removeAll { audioInfo -> audioInfo.audioDuration < getIntSettingOrThrow(R.string.key_audio_filter_duration_value) } }
            }
            sortBy { it.audioTitlePinyin }
            forEachIndexed { index, audioInfo -> audioInfo.index = index }
            activityInterface.audioInfoList.also {
                if (isNotEmpty()) {
                    it.clear()
                }
                it.addAll(this)
            }
        }

        ui { activityInterface.onLoadStageChange() }

        audioDatabaseHelper.queryAllPlaylist(activityInterface.playlistList)
        activityInterface.playlistList.forEach { audioDatabaseHelper.queryPlaylistContent(it, activityInterface.audioInfoList) }

        loadAudioArt40Dp.await()
        loadAlbumArts40Dp.await()
        loadPlaylist40Dp.await()

        ui { activityInterface.onCompleteLoading() }
        activityInterface.refreshCompleted = true

        loadAlbumArtRaw(activityInterface.byteArrayMap)
        audioDatabaseHelper.queryMediaAlbum(activityInterface.albumList)
    }

    private fun compareAudioList() {
        val audioInfoListSystem = scanSystemDatabase()
        var audioInfoList = queryDatabase()
        // 如果系统数据库没有, 而本地数据库有, 那么文件就可能被删除了, 所以这里需要从数据库删除
        // If not exists in system database, but in local database, the file might be removed,
        // so sync with system
        val removeList = compareAudioID(audioInfoList, audioInfoListSystem)
        audioDatabaseHelper.removeAudio(removeList)
        removeList.forEach { removeAudioArt(it.audioId) }
        // 跟上方相反, 如果本地数据库没有, 系统数据库有, 那么就是新添加的文件, 所以这里需要更新到本地数据库
        // Opposite to above, exists in system database, but not in local database, the file might
        // be newly stored, so sync with system
        audioDatabaseHelper.insertAudio(compareAudioID(audioInfoListSystem, audioInfoList))

        // 刷新本地列表
        audioInfoList = queryDatabase()
        audioDatabaseHelper.updateAudio(compareAudioInfo(audioInfoListSystem, audioInfoList))
    }

    private fun compareMediaAlbum() {
        val mediaAlbumList = analyzeMediaAlbum(queryDatabase())
        var mediaAlbumListOrigin = arrayListOf<MediaAlbum>().apply {
            audioDatabaseHelper.queryMediaAlbum(this)
        }

        var list = compareAlbumId(mediaAlbumListOrigin, mediaAlbumList)
        audioDatabaseHelper.removeMediaAlbum(list)
        list.forEach { mediaAlbum -> removeAlbumArts(mediaAlbum.albumId.toString()) }

        list = compareAlbumId(mediaAlbumList, mediaAlbumListOrigin)
        audioDatabaseHelper.insertMediaAlbum(list)

        var bitmap: Bitmap
        val matrix = Matrix()
        val bitmapSize = resources.getDimensionPixelSize(R.dimen.home_recycler_view_image_view_size)
        for (mediaAlbum in list) {
            bitmap = tryRun { loadAlbumArt(mediaAlbum.albumId) } ?: continue
            writeAlbumArtRaw(mediaAlbum.albumId, bitmap)
            matrix.apply { setScale(bitmapSize / bitmap.widthF, bitmapSize / bitmap.heightF) }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
            writeAlbumArt40Dp(mediaAlbum.albumId, bitmap)
        }

        mediaAlbumListOrigin = arrayListOf<MediaAlbum>().apply {
            audioDatabaseHelper.queryMediaAlbum(this)
        }
        list = compareMediaAlbum(mediaAlbumList, mediaAlbumListOrigin)
        audioDatabaseHelper.updateAlbum(list)
    }

    private fun compareDatabase() {
        compareAudioList()
        compareMediaAlbum()
        audioDatabaseHelper.writeComplete()
    }

    private fun compareAudioID(list0: ArrayList<AudioInfo>, list1: ArrayList<AudioInfo>) = arrayListOf<AudioInfo>().apply {
        list0.forEach { audioInfo ->
            if (list1.indexOfFirst { it.audioId == audioInfo.audioId } == -1) {
                add(audioInfo)
            }
        }
    }

    private fun compareAudioInfo(audioInfoListSystem: ArrayList<AudioInfo>, audioInfoList: ArrayList<AudioInfo>) = arrayListOf<AudioInfo>().apply {
        audioInfoListSystem.forEach { audioInfo ->
            audioInfoList.find { it.audioId == audioInfo.audioId }?.let {
                if (compareAudioInfo(audioInfo, it)) {
                    add(audioInfo)
                }
            }
        }
    }

    private fun compareAudioInfo(audioInfo0: AudioInfo, audioInfo1: AudioInfo) =
        audioInfo0.audioTitle != audioInfo1.audioTitle ||
            audioInfo0.audioAlbum != audioInfo1.audioAlbum ||
            audioInfo0.audioAlbumId != audioInfo1.audioAlbumId ||
            audioInfo0.audioArtist != audioInfo1.audioArtist ||
            audioInfo0.audioDuration != audioInfo1.audioDuration ||
            audioInfo0.audioSize != audioInfo1.audioSize ||
            audioInfo0.audioPath != audioInfo1.audioPath

    private fun compareAlbumId(list0: ArrayList<MediaAlbum>, list1: ArrayList<MediaAlbum>) = arrayListOf<MediaAlbum>().apply {
        list0.forEach { audioInfo ->
            if (list1.indexOfFirst { it.albumId == audioInfo.albumId } == -1) {
                add(audioInfo)
            }
        }
    }

    private fun compareMediaAlbum(list0: ArrayList<MediaAlbum>, list1: ArrayList<MediaAlbum>) = arrayListOf<MediaAlbum>().apply {
        list0.forEach { mediaAlbum ->
            list1.find { it.albumId == mediaAlbum.albumId }?.let {
                if (compareMediaAlbum(mediaAlbum, it)) {
                    add(mediaAlbum)
                }
            }
        }
    }

    private fun compareMediaAlbum(mediaAlbum0: MediaAlbum, mediaAlbum1: MediaAlbum) =
        mediaAlbum0.title != mediaAlbum1.title || mediaAlbum0.numberOfAudio != mediaAlbum1.numberOfAudio

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
                        io {
                            var drawable =
                                activityInterface.audioBitmapMap[audioInfo.audioId].toDrawable(this@MainActivity)
                                    ?: activityInterface.bitmapMap[audioInfo.audioAlbumId].toDrawable(this@MainActivity)
                                    ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)!!.toBitmap().toDrawable(this@MainActivity) as Drawable
                            if (lastDrawable != null) {
                                drawable = TransitionDrawable(arrayOf(lastDrawable, drawable))
                            }
                            ui {
                                imageView.setImageDrawable(drawable)
                                lastDrawable = when (drawable) {
                                    is TransitionDrawable -> {
                                        drawable.startTransition(ANIMATION_DURATION)
                                        drawable.getDrawable(1)
                                    }
                                    else -> drawable
                                }
                            }
                        }
                        ValueAnimator.ofArgb(BLACK, WHITE).apply {
                            duration = ANIMATION_DURATION_LONG / 2
                            addUpdateListener { textView.setTextColor(animatedValue as Int) }
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    textView.text = audioInfo.audioTitle
                                    ValueAnimator.ofArgb(WHITE, BLACK).apply {
                                        duration = ANIMATION_DURATION_LONG / 2
                                        addUpdateListener { textView.setTextColor(animatedValue as Int) }
                                    }.start()
                                }
                            })
                        }.start()
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
                    contentMain.root.apply {
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
            var drawable =
                activityInterface.audioBitmapMap[metadata.getString(METADATA_KEY_MEDIA_ID)].toDrawable(this@MainActivity)
                    ?: activityInterface.bitmapMap[metadata.getString(METADATA_KEY_ALBUM_ART_URI).toLong()].toDrawable(this@MainActivity)
                    ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)!!.toBitmap().toDrawable(this@MainActivity) as Drawable
            if (lastDrawable != null) {
                drawable = TransitionDrawable(arrayOf(lastDrawable, drawable))
            }
            ui {
                imageView.setImageDrawable(drawable)
                lastDrawable = when (drawable) {
                    is TransitionDrawable -> {
                        drawable.startTransition(ANIMATION_DURATION)
                        drawable.getDrawable(1)
                    }
                    else -> drawable
                }
            }
        }
        when {
            textView.text.isNullOrBlank() -> {
                textView.text = metadata.getString(METADATA_KEY_TITLE)
            }
            else -> {
                ValueAnimator.ofArgb(BLACK, WHITE).apply {
                    duration = ANIMATION_DURATION_LONG / 2
                    addUpdateListener { textView.setTextColor(animatedValue as Int) }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            textView.text = metadata.getString(METADATA_KEY_TITLE)
                            ValueAnimator.ofArgb(WHITE, BLACK).apply {
                                duration = ANIMATION_DURATION_LONG / 2
                                addUpdateListener { textView.setTextColor(animatedValue as Int) }
                            }.start()
                        }
                    })
                }.start()
            }
        }
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
                    io {
                        var drawable =
                            activityInterface.audioBitmapMap[audioInfo.audioId].toDrawable(this@MainActivity)
                                ?: activityInterface.bitmapMap[audioInfo.audioAlbumId].toDrawable(this@MainActivity)
                                ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_music)!!.toBitmap().toDrawable(this@MainActivity) as Drawable
                        if (lastDrawable != null) {
                            drawable = TransitionDrawable(arrayOf(lastDrawable, drawable))
                        }
                        ui {
                            imageView.setImageDrawable(drawable)
                            lastDrawable = when (drawable) {
                                is TransitionDrawable -> {
                                    drawable.startTransition(ANIMATION_DURATION)
                                    drawable.getDrawable(1)
                                }
                                else -> drawable
                            }
                        }
                    }
                    ValueAnimator.ofArgb(BLACK, WHITE).apply {
                        duration = ANIMATION_DURATION_LONG / 2
                        addUpdateListener { textView.setTextColor(animatedValue as Int) }
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                textView.text = audioInfo.audioTitle
                                ValueAnimator.ofArgb(WHITE, BLACK).apply {
                                    duration = ANIMATION_DURATION_LONG / 2
                                    addUpdateListener { textView.setTextColor(animatedValue as Int) }
                                }.start()
                            }
                        })
                    }.start()
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
        when (navController.currentDestination?.id) {
            R.id.nav_home, R.id.nav_album, R.id.nav_playlist -> startActivity(intent(ACTION_MAIN) { flags = FLAG_ACTIVITY_NEW_TASK; addCategory(CATEGORY_HOME) })
            R.id.nav_web_dav_directory ->
                (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.first() as WebDavDirectoryFragment)
                    .onBackPressed()
            else -> super.onBackPressed()
        }
    }
    
}